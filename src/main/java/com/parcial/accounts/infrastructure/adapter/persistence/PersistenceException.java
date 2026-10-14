package com.parcial.accounts.infrastructure.adapter.persistence;

/** Error tecnico al hablar con la base de datos (no es un error de negocio). */
public class PersistenceException extends RuntimeException {

  public PersistenceException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
