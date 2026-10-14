package com.parcial.accounts;

import com.parcial.accounts.domain.enums.AccountStatus;
import com.parcial.accounts.domain.model.AccountModel;
import com.parcial.accounts.domain.valueobject.AccountEmail;
import com.parcial.accounts.domain.valueobject.AccountId;
import com.parcial.accounts.domain.valueobject.AccountNumber;
import com.parcial.accounts.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestData {

  public static final String ACCOUNT_ID = "8f684dc4-0c67-4813-8d58-7b5e7f7937b8";
  public static final String USER_ID = "1043960765";
  public static final String EMAIL = "ana@gmail.com";
  public static final String NUMBER = "0123456789";

  private TestData() {}

  public static AccountModel account() {
    return new AccountModel(
        new AccountId(ACCOUNT_ID),
        new UserId(USER_ID),
        new AccountNumber(NUMBER),
        new AccountEmail(EMAIL),
        new BigDecimal("0.00"),
        AccountStatus.ACTIVE,
        LocalDateTime.of(2026, 10, 14, 5, 0, 0));
  }
}
