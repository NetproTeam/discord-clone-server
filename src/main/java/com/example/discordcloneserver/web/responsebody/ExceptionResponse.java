package com.example.discordcloneserver.web.responsebody;

public class ExceptionResponse {

  private final String name;
  private final String message;

  public ExceptionResponse(String name, String message) {
    this.name = name;
    this.message = message;
  }

  public String getName() {
    return name;
  }

  public String getMessage() {
    return message;
  }
}
