package com.parcial.accounts.infrastructure.entrypoint.rest.dto.response;

import com.parcial.accounts.domain.model.AccountSummaryModel;
import java.time.LocalDateTime;

public record AccountSummaryRestResponse(
    String id,
    String accountNumber,
    String userId,
    String email,
    String status,
    LocalDateTime createdAt) {

  public static AccountSummaryRestResponse from(final AccountSummaryModel summary) {
    return new AccountSummaryRestResponse(
        summary.id(),
        summary.accountNumber(),
        summary.userId(),
        summary.email(),
        summary.status(),
        summary.createdAt());
  }
}
