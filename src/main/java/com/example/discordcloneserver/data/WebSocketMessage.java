package com.example.discordcloneserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {
  private String from; // sender's unique name
  private String type; // message type
  private String data; // room id
  private Object candidate; // ice candidate
  private Object sdp; // session description protocol
  private Object other; // other data
  private String to; // receiver's unique name or unique name list
}
