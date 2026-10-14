package com.parcial.accounts.domain.model;

import com.parcial.accounts.domain.enums.AccountStatus;
import com.parcial.accounts.domain.valueobject.AccountEmail;
import com.parcial.accounts.domain.valueobject.AccountId;
import com.parcial.accounts.domain.valueobject.AccountNumber;
import com.parcial.accounts.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.Value;

/** Cuenta de un usuario. Se crea ACTIVE y con saldo 0. */
@Value
public class AccountModel {

  AccountId id;
  UserId userId;
  AccountNumber accountNumber;
  AccountEmail email;
  BigDecimal balance;
  AccountStatus status;
  LocalDateTime createdAt;

  public AccountModel(
      final AccountId id,
      final UserId userId,
      final AccountNumber accountNumber,
      final AccountEmail email,
      final BigDecimal balance,
      final AccountStatus status,
      final LocalDateTime createdAt) {
    this.id = Objects.requireNonNull(id, "id es requerido");
    this.userId = Objects.requireNonNull(userId, "userId es requerido");
    this.accountNumber = Objects.requireNonNull(accountNumber, "accountNumber es requerido");
    this.email = Objects.requireNonNull(email, "email es requerido");
    this.balance = Objects.requireNonNull(balance, "balance es requerido");
    this.status = Objects.requireNonNull(status, "status es requerido");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt es requerido");
  }

  public static AccountModel open(
      final AccountId id,
      final UserId userId,
      final AccountNumber accountNumber,
      final AccountEmail email) {
    return new AccountModel(
        id,
        userId,
        accountNumber,
        email,
        BigDecimal.ZERO.setScale(2),
        AccountStatus.ACTIVE,
        LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));
  }
}
