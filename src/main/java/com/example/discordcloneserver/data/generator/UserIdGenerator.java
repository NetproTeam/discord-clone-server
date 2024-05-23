package com.example.discordcloneserver.data.generator;

import java.util.HashMap;
import java.util.Map;

public class UserIdGenerator {

  private static UserIdGenerator instance;

  public static UserIdGenerator getInstance() {
    if (instance == null) {
      instance = new UserIdGenerator();
    }
    return instance;
  }

  private final Map<String, Integer> idMap;

  private UserIdGenerator() {
    idMap = new HashMap<>();
  }

  public int generateId(String key) {
    if (!idMap.containsKey(key)) {
      idMap.put(key, 1);
    }
    int id = idMap.get(key);
    idMap.put(key, id + 1);
    return id;
  }
}
