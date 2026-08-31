package com.myplatform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myplatform.common.ApiResult;
import com.myplatform.common.ErrorCodes;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  public AuthInterceptor(JwtService jwtService, ObjectMapper objectMapper) {
    this.jwtService = jwtService;
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      return true;
    }
    String authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      writeUnauthorized(response);
      return false;
    }
    String token = authorization.substring("Bearer ".length()).trim();
    try {
      String userId = jwtService.parse(token).getSubject();
      AuthContext.setUserId(userId);
      return true;
    } catch (JwtException | IllegalArgumentException ex) {
      writeUnauthorized(response);
      return false;
    }
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    AuthContext.clear();
  }

  private void writeUnauthorized(HttpServletResponse response) throws Exception {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(
        response.getWriter(),
        ApiResult.fail(ErrorCodes.UNAUTHORIZED, "请先登录"));
  }
}