package com.myplatform.user;

import com.myplatform.common.ApiResult;
import com.myplatform.user.param.LoginParam;
import com.myplatform.user.param.RegisterParam;
import com.myplatform.user.vo.LoginVo;
import com.myplatform.user.vo.ProfileVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ApiResult<LoginVo> register(@Valid @RequestBody RegisterParam param) {
    return ApiResult.ok(authService.register(param));
  }

  @PostMapping("/login")
  public ApiResult<LoginVo> login(@Valid @RequestBody LoginParam param) {
    return ApiResult.ok(authService.login(param));
  }

  @GetMapping("/profile")
  public ApiResult<ProfileVo> profile() {
    return ApiResult.ok(authService.profile());
  }
}
