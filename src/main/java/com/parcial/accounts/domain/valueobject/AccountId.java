package com.parcial.accounts.domain.valueobject;

import com.parcial.accounts.domain.exception.InvalidAccountIdException;

/** Id de la cuenta (UUID generado por accounts). */
public record AccountId(String value) {

  private static final int MAX_LENGTH = 36;

  public AccountId {
    if (value == null || value.isBlank() || value.trim().length() > MAX_LENGTH) {
      throw new InvalidAccountIdException();
    }
    value = value.trim();
  }
}
