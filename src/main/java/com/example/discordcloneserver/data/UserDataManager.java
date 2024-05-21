package com.example.discordcloneserver.data;

import com.example.discordcloneserver.data.generator.IdGenerator;
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
    IdGenerator idGenerator = IdGenerator.getInstance();
    User user = User.init(name, idGenerator.generateId(name));
    userList.add(user);
    return user;
  }

  public List<User> getUserList() {
    return List.copyOf(userList);
  }
}
