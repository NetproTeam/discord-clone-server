package com.example.discordcloneserver.error.info;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum CommonErrorInfo implements ErrorInfo {
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "message.error.badRequest"), INVALID_INDEX(
      HttpStatus.BAD_REQUEST, "message.error.invalidIndex"), UNAUTHORIZED(
      HttpStatus.UNAUTHORIZED, "message.error.unauthorized"), FORBIDDEN(
      HttpStatus.FORBIDDEN, "message.error.forbidden"), NOT_FOUND(HttpStatus.NOT_FOUND,
      "message.error.notFound"), CONFLICT(HttpStatus.CONFLICT,
      "message.error.conflict"), INTERNAL_SERVER_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "message.error.internalServerError"), BAD_GATEWAY(HttpStatus.BAD_GATEWAY,
      "message.error.badGateway"), SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE,
      "message.error.serviceUnavailable");

  private final HttpStatusCode httpStatus;
  private final String responseMessage;

  CommonErrorInfo(HttpStatus httpStatus, String responseMessage) {
    this.httpStatus = httpStatus;
    this.responseMessage = responseMessage;
  }

  public static ErrorInfo getByHttpStatus(HttpStatusCode status) {
    for (ErrorInfo errorInfo : CommonErrorInfo.values()) {
      if (errorInfo.httpStatus().equals(status)) {
        return errorInfo;
      }
    }
    return INTERNAL_SERVER_ERROR;
  }

  @Override
  public HttpStatusCode httpStatus() {
    return httpStatus;
  }

  @Override
  public String responseMessage() {
    return responseMessage;
  }
}
