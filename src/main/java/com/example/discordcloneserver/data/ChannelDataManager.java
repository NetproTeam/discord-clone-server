package com.example.discordcloneserver.data;

import com.example.discordcloneserver.data.generator.ChannelIdGenerator;
import com.example.discordcloneserver.domain.dto.Channel;
import com.example.discordcloneserver.error.exception.NotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.socket.WebSocketSession;

public class ChannelDataManager {

  private static ChannelDataManager instance;

  private ChannelDataManager() {
    this.channelList = new ArrayList<>();
  }

  public static ChannelDataManager getInstance() {
    if (instance == null) {
      instance = new ChannelDataManager();
    }
    return instance;
  }

  private final List<Channel> channelList;

  public List<Channel> getChannelList() {
    System.out.println("channelList: " + channelList); // TODO channel에 null이 들어가는 이유?
    if (channelList.isEmpty()) {
      return List.of();
    }
    return List.copyOf(channelList);
  }

  public Channel addChannel(String name) {
    ChannelIdGenerator idGenerator = ChannelIdGenerator.getInstance();
    Channel channel = new Channel(name, idGenerator.generateId(), new HashMap<>());
    channelList.add(channel);
    return channel;
  }

  public Channel updateChannelName(long id, String newName) {
    List<Channel> candidates = channelList.stream().filter(channel -> channel.id() == id).toList();
    if (candidates.isEmpty()) {
      throw new NotFoundException();
    }
    if (candidates.size() > 1) {
      throw new IllegalStateException();
    }
    Channel channel = candidates.get(0);
    channelList.remove(channel);

    Channel newChannel = new Channel(newName, id, new HashMap<>(channel.clients()));
    channelList.add(newChannel);
    return newChannel;
  }

  public Channel addClients(long id, String clientName, WebSocketSession session) {
    List<Channel> candidates = channelList.stream().filter(channel -> channel.id() == id).toList();
    if (candidates.isEmpty()) {
      throw new NotFoundException();
    }

    if (candidates.size() > 1) {
      throw new IllegalStateException();
    }
    Channel channel = candidates.get(0);

    channelList.remove(channel);
    Map<String, WebSocketSession> newClients = new HashMap<>(channel.clients());
    newClients.put(clientName, session);
    Channel newChannel = new Channel(channel.name(), id, newClients);
    channelList.add(newChannel);
    return newChannel;
  }

  public Channel removeClients(long id, String clientName) {
    List<Channel> candidates = channelList.stream().filter(channel -> channel.id() == id).toList();
    if (candidates.isEmpty()) {
      throw new NotFoundException();
    }
    if (candidates.size() > 1) {
      throw new IllegalStateException();
    }
    Channel channel = candidates.get(0);
    channelList.remove(channel);

    Map<String, WebSocketSession> newClients = new HashMap<>(channel.clients());
    newClients.remove(clientName);
    Channel newChannel = new Channel(channel.name(), id, newClients);
    channelList.add(newChannel);
    return newChannel;
  }

  public Channel deleteChannel(long id) {
    List<Channel> candidates = channelList.stream().filter(channel -> channel.id() == id).toList();
    if (candidates.isEmpty()) {
      throw new NotFoundException();
    }
    if (candidates.size() > 1) {
      throw new IllegalStateException();
    }
    Channel channel = candidates.get(0);
    channelList.remove(channel);

    return channel;
  }

  public boolean existsChannel(long id) {
    return channelList.stream().anyMatch(channel -> channel.id() == id);
  }
}
