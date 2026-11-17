package com.parcial.accounts.infrastructure.adapter.cache;

import com.parcial.accounts.application.port.out.AccountCachePort;
import com.parcial.accounts.domain.model.AccountSummaryModel;
import com.parcial.accounts.domain.valueobject.UserId;
import java.util.Optional;

/** Se usa cuando no hay VALKEY_URI: la app funciona igual, siempre contra la base de datos. */
public class NoOpAccountCacheAdapter implements AccountCachePort {

  @Override
  public Optional<AccountSummaryModel> get(final UserId userId) {
    return Optional.empty();
  }

  @Override
  public void put(final AccountSummaryModel summary) {
    // sin cache
  }

  @Override
  public void evict(final UserId userId) {
    // sin cache
  }
}
