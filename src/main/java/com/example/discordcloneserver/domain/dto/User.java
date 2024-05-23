package com.example.discordcloneserver.domain.dto;

public record User (
  String name,
  int id
) {

  public String getUniqueUserName() {
    return name + "#" + String.format("%04d", id);
  }
}
