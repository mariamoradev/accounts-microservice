package com.parcial.accounts.application.port.in;

import com.parcial.accounts.domain.model.AccountModel;
import com.parcial.accounts.domain.model.AccountSummaryModel;
import java.util.List;

/** Puerto de entrada para consultar cuentas. */
public interface GetAccountUseCase {
  AccountModel getById(String accountId);

  AccountModel getByUserId(String userId);

  /** Guia 10: datos estables de la cuenta del usuario, servidos desde la cache si estan. */
  AccountSummaryModel getSummaryByUserId(String userId);

  List<AccountModel> getAll();
}
