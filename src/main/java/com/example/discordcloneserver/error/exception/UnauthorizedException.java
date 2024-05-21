package com.example.discordcloneserver.error.exception;

import com.example.discordcloneserver.error.info.CommonErrorInfo;

public class UnauthorizedException extends ServerException {

  public UnauthorizedException() {
    super(CommonErrorInfo.UNAUTHORIZED);
  }
}
