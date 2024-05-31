package com.example.discordcloneserver.domain.service;

import com.example.discordcloneserver.data.ChannelDataManager;
import com.example.discordcloneserver.data.WebSocketMessage;
import com.example.discordcloneserver.domain.dto.Channel;
import com.example.discordcloneserver.domain.exception.MaxChannelCountException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class ChannelService {

  ChannelDataManager channelDataManager = ChannelDataManager.getInstance();

  public List<Channel> getChannelList() {
    return channelDataManager.getChannelList();
  }

  public Channel makeChannel(String name) {
    if (channelDataManager.getChannelList().size() >= 6) {
      throw new MaxChannelCountException();
    }
    return channelDataManager.addChannel(name);
  }

  public Channel updateChannel(long id, String name) {
    return channelDataManager.updateChannelName(id, name);
  }

  public Channel deleteChannel(long id) {
    return channelDataManager.deleteChannel(id);
  }

  public void addClient(Channel channel, String uniqueName, WebSocketSession session) {
    channelDataManager.addClients(channel.id(), uniqueName, session);
  }

  public Map<String, WebSocketSession> getClients(Channel channel){
    Optional<Channel> channelDto = Optional.ofNullable(channel);

    return channelDto.map(Channel::clients).orElse(null);
  }
  public void removeClientByName(Channel channel, String uniqueName) {
    channelDataManager.removeClients(channel.id(), uniqueName);
  }

  public boolean findClientCount(WebSocketMessage webSocketMessage){
    Channel channel = ChannelDataManager.getInstance().getChannelList().stream()
        .filter(c -> c.id() == Long.parseLong(webSocketMessage.getData()))
        .findFirst().get();
    return channel.clients().size() > 1;
  }

  public Integer getTotalClientCount() {
    return channelDataManager.getChannelList().stream()
        .map(Channel::clients)
        .map(Map::size)
        .reduce(0, Integer::sum);
  }

  public Map<String, WebSocketSession> getAllClients() {
    return channelDataManager.getChannelList().stream()
        .map(Channel::clients)
        .reduce(new HashMap<>(), (acc, clients) -> {
          acc.putAll(clients);
          return acc;
        });
  }
}
