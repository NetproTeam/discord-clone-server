package com.example.discordcloneserver.error.exception;

import com.example.discordcloneserver.error.info.CommonErrorInfo;

public class NotFoundException extends ServerException {

  public NotFoundException() {
    super(CommonErrorInfo.NOT_FOUND);
  }
}
