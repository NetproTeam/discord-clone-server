package com.example.discordcloneserver.web.controller;

import com.example.discordcloneserver.domain.dto.Channel;
import com.example.discordcloneserver.domain.service.ChannelService;
import com.example.discordcloneserver.web.requestbody.ChannelCreatedRequestBody;
import com.example.discordcloneserver.web.requestbody.ChannelRequestBody;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/channel")
public class ChannelController {

  private final ChannelService channelService;

  public ChannelController(ChannelService channelService) {
    this.channelService = channelService;
  }

  @GetMapping
  public List<Channel> getAllChannel() {
    return channelService.getChannelList();
  }

  @PostMapping
  public Channel addChannel(@RequestBody ChannelCreatedRequestBody channelInfo) {
    return channelService.makeChannel(channelInfo.name(), channelInfo.createdBy());
  }

  @PatchMapping("/{id}")
  public Channel updateChannel(@PathVariable long id, @RequestBody ChannelRequestBody channelInfo) {
    return channelService.updateChannel(id, channelInfo.name());
  }

  @DeleteMapping("/{id}")
  public Channel deleteChannel(@PathVariable long id) {
    return channelService.deleteChannel(id);
  }
}
