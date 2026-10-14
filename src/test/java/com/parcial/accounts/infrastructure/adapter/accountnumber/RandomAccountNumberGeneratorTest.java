package com.parcial.accounts.infrastructure.adapter.accountnumber;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RandomAccountNumberGenerator")
class RandomAccountNumberGeneratorTest {

  @Test
  @DisplayName("genera siempre 10 digitos")
  void shouldGenerateTenDigits() {
    final RandomAccountNumberGenerator generator = new RandomAccountNumberGenerator();
    for (int i = 0; i < 100; i++) {
      assertTrue(generator.next().value().matches("\\d{10}"));
    }
  }
}
