package com.example.discordcloneserver.error.exception;

import com.example.discordcloneserver.error.info.CommonErrorInfo;

public class ForbiddenException extends ServerException {

  public ForbiddenException() {
    super(CommonErrorInfo.FORBIDDEN);
  }
}
