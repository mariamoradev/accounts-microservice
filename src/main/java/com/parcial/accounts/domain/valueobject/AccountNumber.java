package com.parcial.accounts.domain.valueobject;

import com.parcial.accounts.domain.exception.InvalidAccountNumberException;
import java.util.regex.Pattern;

/** Numero de cuenta visible para el usuario: exactamente 10 digitos. */
public record AccountNumber(String value) {

  private static final Pattern TEN_DIGITS = Pattern.compile("^\\d{10}$");

  public AccountNumber {
    if (value == null || !TEN_DIGITS.matcher(value).matches()) {
      throw new InvalidAccountNumberException();
    }
  }
}
