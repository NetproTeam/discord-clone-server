package com.example.discordcloneserver.domain.exception;

import com.example.discordcloneserver.error.exception.ForbiddenException;

public class MaxChannelCountException extends ForbiddenException {

  @Override
  public String getName() {
    return "MAX_CHANNEL_COUNT";
  }

  @Override
  public String getResponseMessage() {
    return "최대 6개 까지 채널을 만들 수 있습니다.";
  }

}
