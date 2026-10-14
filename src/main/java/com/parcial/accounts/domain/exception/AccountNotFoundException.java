package com.parcial.accounts.domain.exception;

public final class AccountNotFoundException extends DomainException {

  private AccountNotFoundException(final String message) {
    super(message);
  }

  public static AccountNotFoundException becauseIdWasNotFound(final String accountId) {
    return new AccountNotFoundException("No existe una cuenta con id '" + accountId + "'.");
  }

  public static AccountNotFoundException becauseUserHasNoAccount(final String userId) {
    return new AccountNotFoundException("El usuario '" + userId + "' no tiene cuenta.");
  }
}
