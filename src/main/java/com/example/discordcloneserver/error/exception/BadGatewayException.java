package com.example.discordcloneserver.error.exception;

import com.example.discordcloneserver.error.info.CommonErrorInfo;

public class BadGatewayException extends ServerException {

  public BadGatewayException() {
    super(CommonErrorInfo.BAD_GATEWAY);
  }
}
