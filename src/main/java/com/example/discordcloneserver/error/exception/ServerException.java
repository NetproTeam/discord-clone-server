package com.example.discordcloneserver.error.exception;

import com.example.discordcloneserver.error.info.ErrorInfo;
import org.springframework.http.HttpStatusCode;

public abstract class ServerException extends RuntimeException {

  private final ErrorInfo errorInfo;

  public ServerException(ErrorInfo errorInfo) {
    this.errorInfo = errorInfo;
  }

  public final HttpStatusCode getHttpStatus() {
    return errorInfo.httpStatus();
  }

  public String getName() {
    return errorInfo.name();
  }

  public String getResponseMessage() {
    return errorInfo.responseMessage();
  }
}
