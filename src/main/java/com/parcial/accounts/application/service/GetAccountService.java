package com.parcial.accounts.application.service;

import com.parcial.accounts.application.port.in.GetAccountUseCase;
import com.parcial.accounts.application.port.out.AccountCachePort;
import com.parcial.accounts.application.port.out.GetAccountPort;
import com.parcial.accounts.domain.exception.AccountNotFoundException;
import com.parcial.accounts.domain.model.AccountModel;
import com.parcial.accounts.domain.model.AccountSummaryModel;
import com.parcial.accounts.domain.valueobject.AccountId;
import com.parcial.accounts.domain.valueobject.UserId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAccountService implements GetAccountUseCase {

  private final GetAccountPort getAccountPort;
  private final AccountCachePort accountCachePort;

  @Override
  public AccountModel getById(final String accountId) {
    final AccountId id = new AccountId(accountId);
    return getAccountPort
        .getById(id)
        .orElseThrow(() -> AccountNotFoundException.becauseIdWasNotFound(id.value()));
  }

  /** Siempre va a la base de datos: incluye el saldo, que no se guarda en cache. */
  @Override
  public AccountModel getByUserId(final String userId) {
    final UserId id = new UserId(userId);
    return getAccountPort
        .getByUserId(id)
        .orElseThrow(() -> AccountNotFoundException.becauseUserHasNoAccount(id.value()));
  }

  /**
   * Guia 10 – patron cache-aside:
   *
   * <ol>
   *   <li>Se busca en Valkey. Si esta (HIT), se responde sin tocar la base de datos.
   *   <li>Si no esta (MISS), se consulta TiDB y se guarda en Valkey con TTL para la proxima vez.
   * </ol>
   */
  @Override
  public AccountSummaryModel getSummaryByUserId(final String userId) {
    final UserId id = new UserId(userId);

    final Optional<AccountSummaryModel> cached = accountCachePort.get(id);
    if (cached.isPresent()) {
      log.info("[GetAccountService] CACHE HIT  account:user:{} (respuesta desde Valkey)", id.value());
      return cached.get();
    }

    log.info("[GetAccountService] CACHE MISS account:user:{} (consulta a TiDB)", id.value());
    final AccountModel account =
        getAccountPort
            .getByUserId(id)
            .orElseThrow(() -> AccountNotFoundException.becauseUserHasNoAccount(id.value()));
    final AccountSummaryModel summary = AccountSummaryModel.from(account);
    accountCachePort.put(summary);
    return summary;
  }

  @Override
  public List<AccountModel> getAll() {
    return getAccountPort.getAll();
  }
}
