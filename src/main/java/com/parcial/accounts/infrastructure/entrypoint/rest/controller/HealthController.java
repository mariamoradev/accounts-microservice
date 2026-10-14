package com.parcial.accounts.infrastructure.entrypoint.rest.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Para el health check de Render y para "despertar" el servicio antes de probar. */
@RestController
public class HealthController {

  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "UP", "service", "accounts");
  }
}
