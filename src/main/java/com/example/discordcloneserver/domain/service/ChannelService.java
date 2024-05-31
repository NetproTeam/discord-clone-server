package com.example.discordcloneserver.domain.service;

import com.example.discordcloneserver.data.ChannelDataManager;
import com.example.discordcloneserver.data.WebSocketMessage;
import com.example.discordcloneserver.domain.dto.Channel;
import com.example.discordcloneserver.domain.exception.MaxChannelCountException;
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
    return channelDataManager.updateChannel(id, name);
  }

  public Channel deleteChannel(long id) {
    return channelDataManager.deleteChannel(id);
  }

  public Map<String, WebSocketSession> addClient(Channel channel, String uniqueName, WebSocketSession session) {
    Map<String, WebSocketSession> clients = channel.clients();
    clients.put(uniqueName, session);
    return clients;
  }

  public Map<String, WebSocketSession> getClients(Channel channel){
    Optional<Channel> channelDto = Optional.ofNullable(channel);

    return channelDto.map(Channel::clients).orElse(null);
  }
  public void removeClientByName(Channel channel, String uniqueName) {
    channel.clients().remove(uniqueName);
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
}
