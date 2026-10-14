package com.parcial.accounts.domain.exception;

public final class AccountAlreadyExistsException extends DomainException {

  private AccountAlreadyExistsException(final String message) {
    super(message);
  }

  public static AccountAlreadyExistsException becauseUserAlreadyHasAccount(final String userId) {
    return new AccountAlreadyExistsException(
        "El usuario '" + userId + "' ya tiene una cuenta creada.");
  }
}
