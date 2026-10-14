package com.parcial.accounts.application.port.in;

import com.parcial.accounts.domain.model.AccountModel;
import java.util.List;

/** Puerto de entrada para consultar cuentas. */
public interface GetAccountUseCase {
  AccountModel getById(String accountId);

  AccountModel getByUserId(String userId);

  List<AccountModel> getAll();
}
