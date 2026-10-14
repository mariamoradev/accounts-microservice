package com.parcial.accounts.domain.valueobject;

import com.parcial.accounts.domain.exception.InvalidUserIdException;

/** Id del usuario dueno de la cuenta, tal como lo envia el microservicio users. */
public record UserId(String value) {

  private static final int MAX_LENGTH = 36;

  public UserId {
    if (value == null || value.isBlank() || value.trim().length() > MAX_LENGTH) {
      throw new InvalidUserIdException();
    }
    value = value.trim();
  }
}
