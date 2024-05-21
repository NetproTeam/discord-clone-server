package com.example.discordcloneserver.domain.dto;

public record User (
  String name,
  int id
) {

  public static User init(String nickname, int id) {
    return new User(nickname, id);
  }

  public String getUniqueUserName() {
    return name + "#" + String.format("%04d", id);
  }
}
