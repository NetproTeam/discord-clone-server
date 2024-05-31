package com.example.discordcloneserver.web.controller;

import com.example.discordcloneserver.domain.dto.User;
import com.example.discordcloneserver.domain.exception.MaxLoginCountException;
import com.example.discordcloneserver.domain.service.ChannelService;
import com.example.discordcloneserver.domain.service.UserService;
import com.example.discordcloneserver.web.requestbody.LoginRequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class UserController {

  private final UserService userService;

  private final ChannelService channelService;

  public UserController(
      UserService userService, ChannelService channelService) {
    this.userService = userService;
    this.channelService = channelService;
  }

  @PostMapping
  public User login(@RequestBody LoginRequestBody loginInfo) {
    if (channelService.getTotalClientCount() >= 6) {
      throw new MaxLoginCountException();
    }
    return userService.makeUser(loginInfo.name());
  }
}
