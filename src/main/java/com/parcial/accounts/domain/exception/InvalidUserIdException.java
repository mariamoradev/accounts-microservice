package com.parcial.accounts.domain.exception;

public final class InvalidUserIdException extends DomainException {

  public InvalidUserIdException() {
    super("El id del usuario no puede estar vacio ni tener mas de 36 caracteres.");
  }
}
