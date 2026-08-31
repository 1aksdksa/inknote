package com.myplatform.user;

import com.myplatform.common.BusinessException;
import com.myplatform.common.ErrorCodes;
import com.myplatform.security.AuthContext;
import com.myplatform.security.JwtService;
import com.myplatform.user.param.LoginParam;
import com.myplatform.user.param.RegisterParam;
import com.myplatform.user.vo.LoginVo;
import com.myplatform.user.vo.ProfileVo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public LoginVo register(RegisterParam param) {
    String username = param.getUsername().trim();
    if (userRepository.existsByUsername(username)) {
      throw new BusinessException(ErrorCodes.USERNAME_EXISTS, "用户名已存在");
    }
    Instant now = Instant.now();
    UserEntity user = new UserEntity();
    user.setId(UUID.randomUUID().toString());
    user.setUsername(username);
    user.setPasswordHash(passwordEncoder.encode(param.getPassword()));
    String displayName = StringUtils.hasText(param.getDisplayName())
        ? param.getDisplayName().trim()
        : username;
    user.setDisplayName(displayName);
    user.setCreatedAt(now);
    user.setUpdatedAt(now);
    userRepository.save(user);
    String token = jwtService.createToken(user.getId(), user.getUsername());
    return new LoginVo(token, user.getId(), user.getUsername(), user.getDisplayName());
  }

  @Transactional(readOnly = true)
  public LoginVo login(LoginParam param) {
    UserEntity user = userRepository.findByUsername(param.getUsername().trim())
        .orElseThrow(() -> new BusinessException(ErrorCodes.BAD_CREDENTIALS, "用户名或密码错误"));
    if (!passwordEncoder.matches(param.getPassword(), user.getPasswordHash())) {
      throw new BusinessException(ErrorCodes.BAD_CREDENTIALS, "用户名或密码错误");
    }
    String token = jwtService.createToken(user.getId(), user.getUsername());
    return new LoginVo(token, user.getId(), user.getUsername(), user.getDisplayName());
  }

  @Transactional(readOnly = true)
  public ProfileVo profile() {
    String userId = AuthContext.requireUserId();
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCodes.UNAUTHORIZED, "请先登录"));
    return new ProfileVo(user.getId(), user.getUsername(), user.getDisplayName());
  }
}