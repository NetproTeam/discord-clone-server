package com.example.discordcloneserver.domain.dto;

import java.time.LocalDateTime;

public record Chat(
    String senderUniqueName,
    String content,
    Long roomId,
    LocalDateTime sentAt
){
  public static Chat init(String senderUniqueName, String content, Long roomId) {
    return new Chat(senderUniqueName, content, roomId, LocalDateTime.now());
  }

  @Override
  public String toString() {
    return "ChatMessage{" +
        "sender=" + senderUniqueName +
        ", content='" + content + '\'' +
        ", roomId='" + roomId + '\'' +
        ", sentAt=" + sentAt +
        '}';
  }
}
