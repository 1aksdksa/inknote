package com.myplatform.web;

import com.myplatform.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

  private final DataSource dataSource;

  public HealthController(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @GetMapping("/health")
  public ApiResult<Map<String, Object>> health() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", "ok");
    body.put("service", "inknote-backend");
    body.put("time", Instant.now().toEpochMilli());
    body.put("database", databaseStatus());
    return ApiResult.ok(body);
  }

  @GetMapping("/hello")
  public ApiResult<Map<String, String>> hello() {
    return ApiResult.ok(Map.of("message", "Hello from InkNote"));
  }

  private String databaseStatus() {
    try (Connection connection = dataSource.getConnection()) {
      return connection.isValid(2) ? "up" : "down";
    } catch (Exception ex) {
      return "down";
    }
  }
}
