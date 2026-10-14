package com.parcial.accounts.application.port.out;

import com.parcial.accounts.domain.model.AccountModel;

/** Puerto de salida del paso 7: avisar (por Kafka) que la cuenta fue creada. */
public interface AccountCreatedNotifierPort {
  void notifyAccountCreated(AccountModel account, String holderName);
}
