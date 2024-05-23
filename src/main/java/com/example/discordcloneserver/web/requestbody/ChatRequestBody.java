package com.example.discordcloneserver.web.requestbody;

public record ChatRequestBody(
    String senderUniqueName,
    String content
){
    public static ChatRequestBody init(String senderUniqueName, String content) {
        return new ChatRequestBody(senderUniqueName, content);
    }

    @Override
    public String toString() {
        return "ChatRequestBody{" +
            "sender=" + senderUniqueName +
            ", content='" + content + '\'' +
            '}';
    }
}
