package com.parcial.accounts.infrastructure.adapter.accountnumber;

import com.parcial.accounts.application.port.out.AccountNumberGeneratorPort;
import com.parcial.accounts.domain.valueobject.AccountNumber;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/** Numero de cuenta aleatorio de 10 digitos. La base de datos garantiza que no se repita. */
@Component
public class RandomAccountNumberGenerator implements AccountNumberGeneratorPort {

  private static final long TEN_DIGITS = 10_000_000_000L;
  private final SecureRandom random = new SecureRandom();

  @Override
  public AccountNumber next() {
    return new AccountNumber(String.format("%010d", random.nextLong(TEN_DIGITS)));
  }
}
