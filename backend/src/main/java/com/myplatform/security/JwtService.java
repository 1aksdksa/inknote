package com.myplatform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

  private final SecretKey secretKey;
  private final long expireSeconds;

  public JwtService(
      @Value("${inknote.jwt.secret}") String secret,
      @Value("${inknote.jwt.expire-seconds:604800}") long expireSeconds) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expireSeconds = expireSeconds;
  }

  public String createToken(String userId, String username) {
    Instant now = Instant.now();
    Instant expireAt = now.plusSeconds(expireSeconds);
    return Jwts.builder()
        .subject(userId)
        .claim("username", username)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expireAt))
        .signWith(secretKey)
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
