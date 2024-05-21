package com.example.discordcloneserver.error.exception;

import com.example.discordcloneserver.error.info.CommonErrorInfo;

public class BadRequestException extends ServerException {

  public BadRequestException() {
    super(CommonErrorInfo.BAD_REQUEST);
  }
}
