package com.example.discordcloneserver.domain.service;

import com.example.discordcloneserver.data.ChannelDataManager;
import com.example.discordcloneserver.domain.dto.Channel;
import com.example.discordcloneserver.domain.exception.MaxChannelCountException;
import java.util.List;
import org.springframework.stereotype.Service;

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

  public Channel updateChannel(int id, String name) {
    return channelDataManager.updateChannel(id, name);
  }

  public Channel deleteChannel(int id) {
    return channelDataManager.deleteChannel(id);
  }
}
