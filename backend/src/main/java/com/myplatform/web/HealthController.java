package com.myplatform.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of(
        "status", "ok",
        "service", "my-platform-backend",
        "time", Instant.now().toString()
    );
  }

  @GetMapping("/hello")
  public Map<String, String> hello() {
    return Map.of(
        "message", "Hello from My Platform"
    );
  }
}
