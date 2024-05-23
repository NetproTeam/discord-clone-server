package com.example.discordcloneserver.web.controller;

import com.example.discordcloneserver.data.ChatDataManager;
import com.example.discordcloneserver.domain.dto.Chat;
import com.example.discordcloneserver.web.requestbody.ChatRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {
  ChatDataManager dataManager = ChatDataManager.getInstance();

  @MessageMapping("/{roomId}")
  @SendTo("/send/{roomId}")
  public Chat chat(@DestinationVariable Long roomId, ChatRequestBody message) {
    Chat chat = Chat.init(message.senderUniqueName(), message.content(), roomId);
    dataManager.addChat(chat);
    return chat;
  }
}
