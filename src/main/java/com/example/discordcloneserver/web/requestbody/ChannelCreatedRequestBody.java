package com.example.discordcloneserver.web.requestbody;

public record ChannelCreatedRequestBody(
    String name,
    String createdBy
) {

}
