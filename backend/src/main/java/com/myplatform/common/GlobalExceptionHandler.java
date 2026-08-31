package com.myplatform.common;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ApiResult<Void> handleBusiness(BusinessException ex) {
    return ApiResult.fail(ex.getCode(), ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResult<Void> handleValidation(MethodArgumentNotValidException ex) {
    FieldError fieldError = ex.getBindingResult().getFieldError();
    String message = fieldError == null ? "参数校验失败" : fieldError.getDefaultMessage();
    return ApiResult.fail(ErrorCodes.VALIDATION_ERROR, message);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ApiResult<Void> handleUnreadable(HttpMessageNotReadableException ex) {
    return ApiResult.fail(ErrorCodes.VALIDATION_ERROR, "请求体格式错误");
  }

  @ExceptionHandler(Exception.class)
  public ApiResult<Void> handleOther(Exception ex) {
    ex.printStackTrace();
    return ApiResult.fail(ErrorCodes.SYSTEM_ERROR, "系统异常，请稍后重试");
  }
}