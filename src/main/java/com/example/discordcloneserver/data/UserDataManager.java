package com.example.discordcloneserver.data;

import com.example.discordcloneserver.data.generator.UserIdGenerator;
import com.example.discordcloneserver.domain.dto.User;
import java.util.ArrayList;
import java.util.List;

public class UserDataManager {

  private static UserDataManager instance;

  public UserDataManager(
      List<User> userList) {
    this.userList = userList;
  }

  public static UserDataManager getInstance() {
    if (instance == null) {
      instance = new UserDataManager(new ArrayList<>());
    }
    return instance;
  }

  private final List<User> userList;

  public User addUser(String name) {
    UserIdGenerator userIdGenerator = UserIdGenerator.getInstance();
    User user = new User(name, userIdGenerator.generateId(name));
    userList.add(user);
    return user;
  }

  public List<User> getUserList() {
    if (userList.isEmpty()) {
      return List.of();
    }
    return List.copyOf(userList);
  }
}
