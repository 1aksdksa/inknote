package com.myplatform.security;

import com.myplatform.common.BusinessException;
import com.myplatform.common.ErrorCodes;

public final class AuthContext {
  private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

  private AuthContext() {
  }

  public static void setUserId(String userId) {
    USER_ID.set(userId);
  }

  public static String requireUserId() {
    String userId = USER_ID.get();
    if (userId == null || userId.isBlank()) {
      throw new BusinessException(ErrorCodes.UNAUTHORIZED, "请先登录");
    }
    return userId;
  }

  public static void clear() {
    USER_ID.remove();
  }
}