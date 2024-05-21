package com.example.discordcloneserver.domain.exception;

import com.example.discordcloneserver.error.exception.ForbiddenException;

public class MaxLoginCountException extends ForbiddenException {

  @Override
  public String getName() {
    return "MAX_LOGIN_COUNT";
  }

  @Override
  public String getResponseMessage() {
    return "6명 이상 로그인 할 수 없습니다.";
  }

}
