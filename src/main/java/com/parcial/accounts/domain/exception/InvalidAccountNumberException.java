package com.parcial.accounts.domain.exception;

public final class InvalidAccountNumberException extends DomainException {

  public InvalidAccountNumberException() {
    super("El numero de cuenta debe tener exactamente 10 digitos.");
  }
}
