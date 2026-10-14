package com.parcial.accounts.infrastructure.entrypoint.rest.dto.response;

import com.parcial.accounts.domain.model.AccountModel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountRestResponse(
    String id,
    String accountNumber,
    String userId,
    String email,
    BigDecimal balance,
    String status,
    LocalDateTime createdAt) {

  public static AccountRestResponse from(final AccountModel account) {
    return new AccountRestResponse(
        account.getId().value(),
        account.getAccountNumber().value(),
        account.getUserId().value(),
        account.getEmail().value(),
        account.getBalance(),
        account.getStatus().name(),
        account.getCreatedAt());
  }
}
