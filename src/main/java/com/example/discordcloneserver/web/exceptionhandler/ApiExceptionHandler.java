package com.example.discordcloneserver.web.exceptionhandler;

import com.example.discordcloneserver.error.exception.ServerException;
import com.example.discordcloneserver.error.info.CommonErrorInfo;
import com.example.discordcloneserver.error.info.ErrorInfo;
import com.example.discordcloneserver.web.responsebody.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  public ApiExceptionHandler() {
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAllException(Exception e, WebRequest request) {
    HttpHeaders headers = new HttpHeaders();
    ErrorInfo errorInfo = CommonErrorInfo.INTERNAL_SERVER_ERROR;
    ExceptionResponse errorBody = makeResponse(errorInfo);
    return handleExceptionInternal(e, errorBody, headers, errorInfo.httpStatus(), request);
  }

  @ExceptionHandler({ServerException.class})
  public ResponseEntity<Object> handleServerException(ServerException e, WebRequest request) {
    HttpHeaders headers = new HttpHeaders();
    ExceptionResponse errorBody = makeResponse(e);
    return handleExceptionInternal(e, errorBody, headers, e.getHttpStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception e, @Nullable Object body,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    e.printStackTrace();
    if (body == null) {
      ErrorInfo errorInfo = CommonErrorInfo.getByHttpStatus(status);
      body = makeResponse(errorInfo);
      return super.handleExceptionInternal(e, body, headers, errorInfo.httpStatus(), request);
    }
    return super.handleExceptionInternal(e, body, headers, status, request);
  }

  private ExceptionResponse makeResponse(ServerException e) {
    return new ExceptionResponse(e.getName(), e.getResponseMessage());
  }

  private ExceptionResponse makeResponse(ErrorInfo errorInfo) {
    return new ExceptionResponse(errorInfo.name(), errorInfo.responseMessage());
  }
}
