package com.example.discordcloneserver.config.handler;

import com.example.discordcloneserver.data.ChannelDataManager;
import com.example.discordcloneserver.data.WebSocketMessage;
import com.example.discordcloneserver.domain.dto.Channel;
import com.example.discordcloneserver.domain.exception.MaxLoginCountException;
import com.example.discordcloneserver.domain.service.ChannelService;
import com.example.discordcloneserver.error.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class SignalHandler extends TextWebSocketHandler {
  private final ChannelService channelService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final ChannelDataManager channelDataManager = ChannelDataManager.getInstance();

  private static final String MSG_TYPE_OFFER = "offer";
  private static final String MSG_TYPE_ANSWER = "answer";
  private static final String MSG_TYPE_ICE = "ice";
  private static final String MSG_TYPE_JOIN = "join";
  private static final String MSG_TYPE_LEAVE = "leave";
  private static final String MSG_TYPE_STATE = "state";

  private static final String MSG_TYPE_MODE_CHANGE = "mode_change";

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
    List<Channel> rooms = channelDataManager.getChannelList();

    Optional<Channel> channelOptional = rooms.stream()
        .filter(c -> channelService.getClients(c).containsValue(session))
        .findFirst();
    if (channelOptional.isPresent()) {
      Channel channel = channelOptional.get();
      String uniqueName = channel.clients().entrySet().stream()
          .filter(entry -> entry.getValue().equals(session))
          .findFirst()
          .orElseGet(() -> Map.entry("exception", session))
          .getKey();

      channelService.removeClientByName(channel, uniqueName);

      System.out.println("[ws] " + "Notify to all members");
      sendAllMessage(MSG_TYPE_STATE, toJsonString(channelService.getChannelList()), null, null, null);
    }
    System.out.println("[ws] Session has been closed: " + session.getId() + "with status: " + status);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session){
    System.out.println("[ws] Session has been established: " + session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage textMessage){
    List<Channel> rooms = channelDataManager.getChannelList();
    try {
      WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(),WebSocketMessage.class);
      String uniqueName = message.getFrom();
      long roomId;
      if (message.getData() == null || message.getData().isEmpty()){
        roomId = 1;
      } else {
        roomId = Long.parseLong(message.getData());
      }
      Channel channel;
      switch (message.getType()) {
        case MSG_TYPE_ICE -> {
          Object candidate = message.getCandidate();
          Object sdp = message.getSdp();
          Map<String, WebSocketSession> clients = channelService.getAllClients();
          clients.forEach((key, value) -> {
            if (!key.equals(uniqueName) && key.equals(message.getTo())) {
              System.out.println("[ws] Send to: " + key + " in room: " + roomId);
              sendMessage(value, new WebSocketMessage(
                  uniqueName,
                  message.getType(),
                  Long.toString(roomId),
                  candidate,
                  sdp,
                  null,
                  key
              ));
            }
          });
        }
        case MSG_TYPE_OFFER, MSG_TYPE_ANSWER -> {
          System.out.println("[ws] Send to: in room: " + roomId + " in type: " + message.getType());
          Object candidate = message.getCandidate();
          Object sdp = message.getSdp();
          Optional<Channel> channelDto = rooms.stream()
              .filter(c -> c.id() == roomId)
              .findFirst();
          if (channelDto.isPresent()) {
            Map<String, WebSocketSession> clients = channelService.getClients(channelDto.get());
            System.out.println("Clients " + clients.toString());
            clients.forEach((key, value) -> {
              if (!key.equals(uniqueName) && key.equals(message.getTo())) {
                System.out.println("[ws] Send to: " + key + " in room: " + roomId + " in type: " + message.getType());
                sendMessage(value, new WebSocketMessage(
                    uniqueName,
                    message.getType(),
                    Long.toString(roomId),
                    candidate,
                    sdp,
                    null,
                    key
                ));
              }
            });
          }
        }
        case MSG_TYPE_JOIN -> {
          System.out.println("[ws] Join: " + uniqueName + " to " + roomId);
          Optional<Channel> cha = ChannelDataManager.getInstance().getChannelList().stream()
              .filter(c -> c.id() == roomId)
              .findFirst();

          if (cha.isEmpty()) {
            sendMessage(session,
                new WebSocketMessage("Server", MSG_TYPE_JOIN, "-1", null, null,null, uniqueName));
            throw new NotFoundException();
          }
          if (channelService.getTotalClientCount() >= 6) {
            sendMessage(session,
                new WebSocketMessage("Server", MSG_TYPE_JOIN, "-1", null, null, null, uniqueName));
            throw new MaxLoginCountException();
          }
          channel = cha.get();
          channelService.addClient(channel, uniqueName, session);

          List<String> otherPeerNamesOnChannel = channel.clients().keySet().stream().filter(
              name -> !name.equals(uniqueName)
          ).toList();
          Object other = objectMapper.readValue("{\"readyList\":" + toJsonString(otherPeerNamesOnChannel) + '}', Object.class);
          sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_JOIN, String.valueOf(channel.id()), null, null, other, uniqueName));

          sendAllMessage(MSG_TYPE_STATE, toJsonString(channelService.getChannelList()), null, null, null);
        }
        case MSG_TYPE_LEAVE -> {
          System.out.println("[ws] Leave: " + uniqueName + " from " + roomId);
          Optional<Channel> channelDto = rooms.stream()
              .filter(c -> c.id() == roomId)
              .findFirst();
          if (channelDto.isPresent()) {
            channel = channelDto.get();
            Optional<String> clientName = channelService.getClients(channel).keySet().stream()
                .filter(key -> Objects.equals(key, uniqueName))
                .findFirst();
            if (clientName.isPresent()) {
              channelService.removeClientByName(channel, clientName.get());

              sendAllMessage(MSG_TYPE_STATE, toJsonString(channelService.getChannelList()), null, null, null);

              channelService.getClients(channel).forEach((key, value) -> {
                System.out.println("[ws] Send to: " + key + " in room: " + roomId + " in type: " + MSG_TYPE_LEAVE);
                sendMessage(value, new WebSocketMessage(clientName.get(), MSG_TYPE_LEAVE, Objects.toString(channel.id()), null, null, null, key));
              });
              System.out.println("[ws] " + uniqueName + "삭제 완료 in room : " + roomId);
            }
          }
        }
        case MSG_TYPE_MODE_CHANGE -> {
          System.out.println("[ws] Mode Change: " + message.getData());
          Optional<Channel> channelDto = rooms.stream()
              .filter(c -> c.id() == roomId)
              .findFirst();
          if (channelDto.isPresent()) {
            channel = channelDto.get();
            channel.clients().forEach((key, value) -> {
              if (key.equals(message.getTo())) {
                System.out.println("[ws] Send to: " + key + " in room: " + roomId + " in type: "
                    + MSG_TYPE_MODE_CHANGE);
                sendMessage(value,
                    new WebSocketMessage("Server", MSG_TYPE_MODE_CHANGE, message.getData(), null,
                        null, message.getOther(), key));
              }
            });
          }
        }
        default -> {
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendMessage(WebSocketSession session, WebSocketMessage message){
    try {
      String json = objectMapper.writeValueAsString(message);
      session.sendMessage(new TextMessage(json));
    } catch (Exception e) {
      System.out.println("[ws] closed session: " + session.getId() + " in sendMessage");
    }
  }

  private String toJsonString(Object object){
    try {
      System.out.println(object.toString());
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void sendAllMessage(String type, String data, Object candidate, Object sdp, Object other){
    channelService.getChannelList().forEach(c -> {
      c.clients().forEach((key, value) -> sendMessage(value,
          new WebSocketMessage("SERVER", type, data,
              null, null, null, key)));
    });
  }
}
