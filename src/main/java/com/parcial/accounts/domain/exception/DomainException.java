package com.parcial.accounts.domain.exception;

/** Error de negocio del dominio de cuentas. */
public abstract class DomainException extends RuntimeException {

  protected DomainException(final String message) {
    super(message);
  }
}
