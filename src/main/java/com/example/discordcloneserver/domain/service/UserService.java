package com.example.discordcloneserver.domain.service;

import com.example.discordcloneserver.data.UserDataManager;
import com.example.discordcloneserver.domain.dto.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  UserDataManager userDataManager = UserDataManager.getInstance();

  public User makeUser(String name) {

    return userDataManager.addUser(name);
  }
}
