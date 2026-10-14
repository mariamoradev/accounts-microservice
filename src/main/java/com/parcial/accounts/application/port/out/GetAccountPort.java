package com.parcial.accounts.application.port.out;

import com.parcial.accounts.domain.model.AccountModel;
import com.parcial.accounts.domain.valueobject.AccountId;
import com.parcial.accounts.domain.valueobject.UserId;
import java.util.List;
import java.util.Optional;

public interface GetAccountPort {
  Optional<AccountModel> getById(AccountId id);

  Optional<AccountModel> getByUserId(UserId userId);

  List<AccountModel> getAll();
}
