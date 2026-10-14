package com.parcial.accounts.domain.valueobject;

import com.parcial.accounts.domain.exception.InvalidAccountEmailException;
import java.util.Locale;
import java.util.regex.Pattern;

/** Email al que se notifica la creacion de la cuenta. Se valida con expresion regular. */
public record AccountEmail(String value) {

  private static final int MAX_LENGTH = 150;
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  public AccountEmail {
    if (value == null) {
      throw new InvalidAccountEmailException();
    }
    final String normalized = value.trim().toLowerCase(Locale.ROOT);
    if (normalized.length() > MAX_LENGTH || !EMAIL_PATTERN.matcher(normalized).matches()) {
      throw new InvalidAccountEmailException();
    }
    value = normalized;
  }
}
