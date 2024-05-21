package com.example.discordcloneserver.domain.service;

import com.example.discordcloneserver.data.UserDataManager;
import com.example.discordcloneserver.domain.dto.User;
import com.example.discordcloneserver.domain.exception.MaxLoginCountException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  UserDataManager userDataManager = UserDataManager.getInstance();

  public User makeUser(String name) {
    if (userDataManager.getUserList().size() >= 6) {
      throw new MaxLoginCountException();
    }
    return userDataManager.addUser(name);
  }
}
