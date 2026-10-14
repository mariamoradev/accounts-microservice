package com.parcial.accounts.application.port.out;

import com.parcial.accounts.domain.model.AccountModel;

public interface SaveAccountPort {
  AccountModel save(AccountModel account);
}
