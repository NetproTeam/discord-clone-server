package com.example.discordcloneserver.data;

import com.example.discordcloneserver.data.generator.ChannelIdGenerator;
import com.example.discordcloneserver.domain.dto.Channel;
import com.example.discordcloneserver.error.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;

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
    return List.copyOf(channelList);
  }

  public Channel addChannel(String name) {
    ChannelIdGenerator idGenerator = ChannelIdGenerator.getInstance();
    Channel channel = new Channel(name, idGenerator.generateId());
    channelList.add(channel);
    return channel;
  }

  public Channel updateChannel(int id, String newName) {
    List<Channel> candidates = channelList.stream().filter(channel -> channel.id() == id).toList();
    if (candidates.isEmpty()) {
      throw new NotFoundException();
    }
    if (candidates.size() > 1) {
      throw new IllegalStateException();
    }
    Channel channel = candidates.get(0);
    channelList.remove(channel);

    Channel newChannel = new Channel(newName, id);
    channelList.add(newChannel);
    return newChannel;
  }

  public Channel deleteChannel(int id) {
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
}
