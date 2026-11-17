package com.parcial.accounts.domain.model;

import java.time.LocalDateTime;

/**
 * Datos estables de una cuenta: se pueden guardar en cache porque casi nunca cambian.
 *
 * <p>No incluye el saldo (balance): el saldo cambia con cada movimiento y siempre se lee de la
 * base de datos, para no mostrar un valor viejo.
 */
public record AccountSummaryModel(
    String id,
    String userId,
    String accountNumber,
    String email,
    String status,
    LocalDateTime createdAt) {

  public static AccountSummaryModel from(final AccountModel account) {
    return new AccountSummaryModel(
        account.getId().value(),
        account.getUserId().value(),
        account.getAccountNumber().value(),
        account.getEmail().value(),
        account.getStatus().name(),
        account.getCreatedAt());
  }
}
