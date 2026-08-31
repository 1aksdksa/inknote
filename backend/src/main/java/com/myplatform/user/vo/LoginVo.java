package com.myplatform.user.vo;

public class LoginVo {
  private String token;
  private String userId;
  private String username;
  private String displayName;

  public LoginVo() {
  }

  public LoginVo(String token, String userId, String username, String displayName) {
    this.token = token;
    this.userId = userId;
    this.username = username;
    this.displayName = displayName;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
