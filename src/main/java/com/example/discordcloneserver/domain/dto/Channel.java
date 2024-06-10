package com.example.discordcloneserver.domain.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import org.springframework.web.socket.WebSocketSession;

public record Channel (
  String name,
  long id,
  String createdBy,
  @JsonIgnore Map<String, WebSocketSession> clients
) {

  @JsonGetter("clients")
  List<String> clientNames() {
    if (clients.isEmpty()) {
      return List.of();
    }
    return List.copyOf(clients.keySet());
  }

}