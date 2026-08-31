package com.myplatform.common;

public record ApiResult<T>(String code, String message, T data) {

  public static <T> ApiResult<T> ok(T data) {
    return new ApiResult<>(ErrorCodes.SUCCESS, "ok", data);
  }

  public static ApiResult<Void> ok() {
    return new ApiResult<>(ErrorCodes.SUCCESS, "ok", null);
  }

  public static ApiResult<Void> fail(String code, String message) {
    return new ApiResult<>(code, message, null);
  }
}
