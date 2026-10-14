package com.parcial.accounts.domain.exception;

public final class InvalidAccountStatusException extends DomainException {

  public InvalidAccountStatusException(final String value) {
    super("El estado de cuenta '" + value + "' no es valido.");
  }
}
