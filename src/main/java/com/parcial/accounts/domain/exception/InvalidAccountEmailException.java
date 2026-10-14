package com.parcial.accounts.domain.exception;

public final class InvalidAccountEmailException extends DomainException {

  public InvalidAccountEmailException() {
    super("El email de la cuenta no es valido.");
  }
}
