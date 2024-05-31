package com.example.discordcloneserver.preprocessing;

import com.example.discordcloneserver.domain.service.ChannelService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
public class ChannelInitializer implements CommandLineRunner {

  private final ChannelService channelService;

  public ChannelInitializer(ChannelService channelService) {
    this.channelService = channelService;
  }

  @Override
  public void run(String... args) {
    channelService.makeChannel("general");
  }
}
