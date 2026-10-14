package com.parcial.accounts.domain.enums;

import com.parcial.accounts.domain.exception.InvalidAccountStatusException;

public enum AccountStatus {
  ACTIVE,
  BLOCKED,
  CLOSED;

  public static AccountStatus fromString(final String value) {
    for (final AccountStatus status : values()) {
      if (status.name().equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new InvalidAccountStatusException(value);
  }
}
