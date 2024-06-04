package com.example.discordcloneserver.config.handler;

import com.example.discordcloneserver.data.ChannelDataManager;
import com.example.discordcloneserver.data.WebSocketMessage;
import com.example.discordcloneserver.domain.dto.Channel;
import com.example.discordcloneserver.domain.exception.MaxLoginCountException;
import com.example.discordcloneserver.domain.service.ChannelService;
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
    if (rooms.stream().anyMatch(c -> channelService.getClients(c).containsValue(session))) {
      Channel channel = rooms.stream()
          .filter(c -> channelService.getClients(c).containsValue(session)).findFirst().get();
      String uniqueName = rooms.stream().filter(c -> channelService.getClients(c).containsValue(session))
          .findFirst().get().clients().entrySet().stream()
          .filter(entry -> entry.getValue().equals(session))
          .findFirst().get().getKey();
      channelService.removeClientByName(channel, uniqueName);
      System.out.println("[ws] " + "Notify to all members");
      channelService.getChannelList().forEach(c -> {
        c.clients().forEach((key, value) -> {
          System.out.println("[ws] Send to: " + key + " in room: " + c.id() + " in type: " + MSG_TYPE_STATE);
          sendMessage(value,
              new WebSocketMessage("Server", MSG_TYPE_STATE, toJsonString(channelService.getChannelList()), null, null,null));
        });
      });
    }
    System.out.println("[ws] Session has been closed: " + session.getId() + "with status: " + status);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session){
    List<Channel> rooms = channelDataManager.getChannelList();
    sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_JOIN, Boolean.toString(!rooms.isEmpty()), null, null, null));
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage textMessage){
    List<Channel> rooms = channelDataManager.getChannelList();
    try {
      WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(),WebSocketMessage.class);
      String uniqueName = message.getFrom();
      long roomId;
      if (message.getData() == null){
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
            if (!key.equals(uniqueName)) {
              System.out.println("[ws] Send to: " + key + " in room: " + roomId);
              sendMessage(value, new WebSocketMessage(
                  uniqueName,
                  message.getType(),
                  Long.toString(roomId),
                  candidate,
                  sdp,
                  null
              ));
            }
          });
        }
        case MSG_TYPE_OFFER, MSG_TYPE_ANSWER -> {
          Object candidate = message.getCandidate();
          Object sdp = message.getSdp();
          Optional<Channel> channelDto = rooms.stream()
              .filter(c -> c.id() == roomId)
              .findFirst();
          if (channelDto.isPresent()) {
            Map<String, WebSocketSession> clients = channelService.getClients(channelDto.get());
            clients.forEach((key, value) -> {
              if (!key.equals(uniqueName)) {
                System.out.println("[ws] Send to: " + key + " in room: " + roomId + " in type: " + message.getType());
                sendMessage(value, new WebSocketMessage(
                    uniqueName,
                    message.getType(),
                    Long.toString(roomId),
                    candidate,
                    sdp,
                    null
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
          if (!cha.isEmpty()) {
            channel = cha.get();
          } else {
            channel = channelService.makeChannel("room" + roomId);
          }
          if (channelService.getTotalClientCount() >= 6) {
            sendMessage(session,
                new WebSocketMessage("Server", MSG_TYPE_JOIN, "false", null, null,null));
            throw new MaxLoginCountException();
          }

          channelService.addClient(channel, uniqueName, session);
          channelService.getChannelList().forEach(c -> {
            c.clients().forEach((key, value) -> {
              sendMessage(value,
                  new WebSocketMessage("Server", MSG_TYPE_STATE, toJsonString(channelService.getChannelList()), null, null,null));
            });
          });
        }
        case MSG_TYPE_LEAVE -> {
          System.out.println("[ws] Leave: " + uniqueName + " from " + roomId);
          channel = rooms.stream()
              .filter(c -> c.id() == roomId)
              .findFirst().get();
          Optional<String> clientName = channelService.getClients(channel).keySet().stream()
              .filter(key -> Objects.equals(key, uniqueName))
              .findFirst();
          if (clientName.isPresent()) {
            channelService.removeClientByName(channel, clientName.get());
            channelService.getChannelList().forEach(c -> {
              c.clients().forEach((key, value) -> {
                sendMessage(value,
                    new WebSocketMessage("Server", MSG_TYPE_STATE, toJsonString(channelService.getChannelList()), null, null,null));
              });
              channelService.getClients(channel).forEach((key, value) -> {
                System.out.println("[ws] Send to: " + key + " in room: " + roomId + " in type: " + MSG_TYPE_LEAVE);
                sendMessage(value,
                    new WebSocketMessage(clientName.get(), MSG_TYPE_LEAVE, Objects.toString(channel.id()), null, null, null));
              });
            });
            //TODO: 유저 카운트 한다면 추가 필요
            System.out.println("[ws] " + uniqueName + "삭제 완료 in room : " + roomId);
          }
        }
        case MSG_TYPE_MODE_CHANGE -> {
          System.out.println("[ws] Mode Change: " + message.getData());
          channel = rooms.stream()
              .filter(c -> c.id() == roomId)
              .findFirst().get();
          channel.clients().forEach((key, value) -> {
            System.out.println("[ws] Send to: " + key + " in room: " + roomId + " in type: " + MSG_TYPE_MODE_CHANGE);
            sendMessage(value, new WebSocketMessage("Server", MSG_TYPE_MODE_CHANGE, message.getData(), null, null, message.getOther()));
          });
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
      e.printStackTrace();
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
}
