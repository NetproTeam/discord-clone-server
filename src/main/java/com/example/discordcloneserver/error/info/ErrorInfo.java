package com.example.discordcloneserver.error.info;

import org.springframework.http.HttpStatusCode;

public interface ErrorInfo {

  String name();

  HttpStatusCode httpStatus();

  String responseMessage();
}
