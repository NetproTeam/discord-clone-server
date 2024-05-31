package com.example.discordcloneserver.config.handler;

import com.example.discordcloneserver.data.ChannelDataManager;
import com.example.discordcloneserver.data.WebSocketMessage;
import com.example.discordcloneserver.domain.dto.Channel;
import com.example.discordcloneserver.domain.service.ChannelService;
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

  private List<Channel> rooms = ChannelDataManager.getInstance().getChannelList();

  private static final String MSG_TYPE_OFFER = "offer";
  private static final String MSG_TYPE_ANSWER = "answer";
  private static final String MSG_TYPE_ICE = "ice";
  private static final String MSG_TYPE_JOIN = "join";
  private static final String MSG_TYPE_LEAVE = "leave";

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
    System.out.println("[ws] Session has been closed: " + session.getId() + "with status: " + status);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session){
    sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_JOIN, Boolean.toString(!rooms.isEmpty()), null, null));
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage textMessage){
    try {
      WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(),WebSocketMessage.class);
      String uniqueName = message.getFrom();
      Long roomId = Long.parseLong(message.getData());

      Channel channel;
      switch (message.getType()){
        case MSG_TYPE_OFFER:
        case MSG_TYPE_ANSWER:
        case MSG_TYPE_ICE:
          Object candidate = message.getCandidate();
          Object sdp = message.getSdp();

          System.out.println(("[ws] Signal: " + candidate != null
              ? candidate.toString().substring(0, 64)
              : sdp.toString().substring(0, 64)));
          Channel channelDto = rooms.stream()
              .filter(c -> c.id() == roomId)
              .findFirst().get();
          if (channelDto != null) {
            Map<String, WebSocketSession> clients = channelService.getClients(channelDto);
            clients.forEach((key, value) -> {
              if (!key.equals(uniqueName)) {
                sendMessage(value, new WebSocketMessage(
                    uniqueName,
                    message.getType(),
                    roomId.toString(),
                    candidate,
                    sdp
                ));
              }
            });
          }
          break;
        case MSG_TYPE_JOIN:
          System.out.println("[ws] Join: " + uniqueName + " to " + roomId);
          channel = ChannelDataManager.getInstance().getChannelList().stream()
              .filter(c -> c.id() == roomId)
              .findFirst().get();
          channelService.addClient(channel, uniqueName, session);
          // TODO: 유저 카운트 한다면 추가 필요
          rooms.add(channel);
          break;
        case MSG_TYPE_LEAVE:
          System.out.println("[ws] Leave: " + uniqueName + " from " + roomId);
          channel = rooms.stream()
              .filter(c -> c.id() == roomId)
              .findFirst().get();
          Optional<String> clientName = channelService.getClients(channel).keySet().stream()
              .filter(key -> Objects.equals(key, uniqueName))
              .findFirst();
          if (clientName.isPresent()) {
            channelService.removeClientByName(channel, clientName.get());
            //TODO: 유저 카운트 한다면 추가 필요
            System.out.println("[ws] " + uniqueName + "삭제 완료 in room : " + roomId);
          }
          break;
        default:
          break;
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
}