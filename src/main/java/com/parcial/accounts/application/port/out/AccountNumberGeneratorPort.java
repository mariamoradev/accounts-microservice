package com.parcial.accounts.application.port.out;

import com.parcial.accounts.domain.valueobject.AccountNumber;

/** Genera numeros de cuenta. Es un puerto para que los tests no dependan del azar. */
public interface AccountNumberGeneratorPort {
  AccountNumber next();
}
