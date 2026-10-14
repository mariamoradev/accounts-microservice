package com.parcial.accounts.application.service;

import com.parcial.accounts.application.port.in.GetAccountUseCase;
import com.parcial.accounts.application.port.out.GetAccountPort;
import com.parcial.accounts.domain.exception.AccountNotFoundException;
import com.parcial.accounts.domain.model.AccountModel;
import com.parcial.accounts.domain.valueobject.AccountId;
import com.parcial.accounts.domain.valueobject.UserId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAccountService implements GetAccountUseCase {

  private final GetAccountPort getAccountPort;

  @Override
  public AccountModel getById(final String accountId) {
    final AccountId id = new AccountId(accountId);
    return getAccountPort
        .getById(id)
        .orElseThrow(() -> AccountNotFoundException.becauseIdWasNotFound(id.value()));
  }

  @Override
  public AccountModel getByUserId(final String userId) {
    final UserId id = new UserId(userId);
    return getAccountPort
        .getByUserId(id)
        .orElseThrow(() -> AccountNotFoundException.becauseUserHasNoAccount(id.value()));
  }

  @Override
  public List<AccountModel> getAll() {
    return getAccountPort.getAll();
  }
}
