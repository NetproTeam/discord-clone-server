package com.example.discordcloneserver.data;

import com.example.discordcloneserver.data.generator.IdGenerator;
import com.example.discordcloneserver.domain.dto.Chat;
import com.example.discordcloneserver.domain.dto.User;
import java.util.ArrayList;
import java.util.List;

public class ChatDataManager {
  private static ChatDataManager instance;
  private final List<Chat> chatList;

  public ChatDataManager(List<Chat> chatList) {
    this.chatList = chatList;
  }

  public static ChatDataManager getInstance(){
    if (instance == null) {
      instance = new ChatDataManager(new ArrayList<>());
    }
    return instance;
  }

  public Chat addChat(Chat chat) {
    chatList.add(chat);
    return chat;
  }

  public List<Chat> getChatList() {
    return List.copyOf(chatList);
  }


}
