package com.example.discordcloneserver.error.exception;

import com.example.discordcloneserver.error.info.CommonErrorInfo;

public class InternalServerException extends ServerException {

  public InternalServerException() {
    super(CommonErrorInfo.INTERNAL_SERVER_ERROR);
  }
}
