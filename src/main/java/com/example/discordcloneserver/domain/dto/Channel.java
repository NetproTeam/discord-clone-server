package com.example.discordcloneserver.domain.dto;

import java.util.Map;
import org.springframework.web.socket.WebSocketSession;

public record Channel (
  String name,
  long id,
  Map<String, WebSocketSession> clients
) {

}
