package com.example.discordcloneserver.error.exception;

import com.example.discordcloneserver.error.info.CommonErrorInfo;

public class ConflictException extends ServerException {

  public ConflictException() {
    super(CommonErrorInfo.CONFLICT);
  }
}
