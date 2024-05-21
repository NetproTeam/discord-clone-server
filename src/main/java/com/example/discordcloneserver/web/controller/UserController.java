package com.example.discordcloneserver.web.controller;

import com.example.discordcloneserver.domain.dto.User;
import com.example.discordcloneserver.domain.service.UserService;
import com.example.discordcloneserver.web.vo.LoginRequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class UserController {

  private final UserService userService;

  public UserController(
      UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public User login(@RequestBody LoginRequestBody loginInfo) {
    return userService.makeUser(loginInfo.name());
  }
}
