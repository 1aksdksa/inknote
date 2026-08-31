package com.myplatform.user.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterParam {

  @NotBlank(message = "用户名不能为空")
  @Size(min = 3, max = 32, message = "用户名长度为 3-32")
  private String username;

  @NotBlank(message = "密码不能为空")
  @Size(min = 6, max = 64, message = "密码长度为 6-64")
  private String password;

  @Size(max = 64, message = "昵称过长")
  private String displayName;

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  public String getDisplayName() { return displayName; }
  public void setDisplayName(String displayName) { this.displayName = displayName; }
}