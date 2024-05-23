package com.example.discordcloneserver.web.requestbody;

public record ChatRequestBody(
    String senderUniqueName,
    String content,
    Long roomId
){
    public static ChatRequestBody init(String senderUniqueName, String content, Long roomId) {
        return new ChatRequestBody(senderUniqueName, content, roomId);
    }

    @Override
    public String toString() {
        return "ChatRequestBody{" +
            "sender=" + senderUniqueName +
            ", content='" + content + '\'' +
            ", roomId='" + roomId + '\'' +
            '}';
    }
}
