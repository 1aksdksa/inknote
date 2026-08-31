package com.myplatform.common;

public final class ErrorCodes {
  public static final String SUCCESS = "0";
  public static final String SYSTEM_ERROR = "-1";
  public static final String VALIDATION_ERROR = "101001001";
  public static final String USERNAME_EXISTS = "101001002";
  public static final String BAD_CREDENTIALS = "101001003";
  public static final String UNAUTHORIZED = "101001004";
  public static final String NOTE_NOT_FOUND = "101001005";

  private ErrorCodes() {
  }
}
