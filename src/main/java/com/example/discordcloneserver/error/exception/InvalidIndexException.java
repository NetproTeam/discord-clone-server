package com.example.discordcloneserver.error.exception;

import com.example.discordcloneserver.error.info.CommonErrorInfo;

public class InvalidIndexException extends ServerException {

  public InvalidIndexException() {
    super(CommonErrorInfo.BAD_REQUEST);
  }
}
