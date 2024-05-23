package com.example.discordcloneserver.data.generator;

public class ChannelIdGenerator {

  private static ChannelIdGenerator instance;

  public static ChannelIdGenerator getInstance() {
    if (instance == null) {
      instance = new ChannelIdGenerator();
    }
    return instance;
  }

  private int nextId;

  private ChannelIdGenerator() {
    nextId = 0;
  }

  public int generateId() {
    nextId += 1;
    return nextId;
  }
}
