package com.parcial.accounts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parcial.accounts.domain.enums.AccountStatus;
import com.parcial.accounts.domain.exception.InvalidAccountEmailException;
import com.parcial.accounts.domain.exception.InvalidAccountNumberException;
import com.parcial.accounts.domain.exception.InvalidAccountStatusException;
import com.parcial.accounts.domain.exception.InvalidUserIdException;
import com.parcial.accounts.domain.model.AccountModel;
import com.parcial.accounts.domain.valueobject.AccountEmail;
import com.parcial.accounts.domain.valueobject.AccountId;
import com.parcial.accounts.domain.valueobject.AccountNumber;
import com.parcial.accounts.domain.valueobject.UserId;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Dominio de cuentas")
class DomainValidationTest {

  @Test
  @DisplayName("AccountEmail normaliza a minusculas y rechaza emails invalidos")
  void shouldValidateEmail() {
    assertEquals("ana@gmail.com", new AccountEmail("  Ana@Gmail.com ").value());
    assertThrows(InvalidAccountEmailException.class, () -> new AccountEmail("no-es-email"));
    assertThrows(InvalidAccountEmailException.class, () -> new AccountEmail(null));
  }

  @Test
  @DisplayName("AccountNumber exige exactamente 10 digitos")
  void shouldValidateAccountNumber() {
    assertEquals("0123456789", new AccountNumber("0123456789").value());
    assertThrows(InvalidAccountNumberException.class, () -> new AccountNumber("12345"));
    assertThrows(InvalidAccountNumberException.class, () -> new AccountNumber("12345abcde"));
  }

  @Test
  @DisplayName("UserId no puede estar vacio ni superar 36 caracteres")
  void shouldValidateUserId() {
    assertEquals("1043960765", new UserId(" 1043960765 ").value());
    assertThrows(InvalidUserIdException.class, () -> new UserId(" "));
    assertThrows(InvalidUserIdException.class, () -> new UserId("x".repeat(37)));
  }

  @Test
  @DisplayName("AccountStatus.fromString acepta mayusculas o minusculas y rechaza otros")
  void shouldParseStatus() {
    assertEquals(AccountStatus.ACTIVE, AccountStatus.fromString("active"));
    assertThrows(InvalidAccountStatusException.class, () -> AccountStatus.fromString("RARO"));
  }

  @Test
  @DisplayName("AccountModel.open crea la cuenta ACTIVE con saldo 0.00")
  void shouldOpenActiveAccountWithZeroBalance() {
    final AccountModel account =
        AccountModel.open(
            new AccountId("a-1"),
            new UserId("u-1"),
            new AccountNumber("0000000001"),
            new AccountEmail("ana@gmail.com"));

    assertEquals(AccountStatus.ACTIVE, account.getStatus());
    assertEquals(new BigDecimal("0.00"), account.getBalance());
  }
}
