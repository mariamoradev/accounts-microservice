package com.parcial.accounts.domain.exception;

public final class InvalidAccountIdException extends DomainException {

  public InvalidAccountIdException() {
    super("El id de la cuenta no puede estar vacio ni tener mas de 36 caracteres.");
  }
}
