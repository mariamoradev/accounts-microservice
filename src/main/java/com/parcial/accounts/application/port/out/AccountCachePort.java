package com.parcial.accounts.application.port.out;

import com.parcial.accounts.domain.model.AccountSummaryModel;
import com.parcial.accounts.domain.valueobject.UserId;
import java.util.Optional;

/**
 * Guia 10: cache de los datos estables de la cuenta de un usuario (Valkey).
 *
 * <p>Si la cache falla, la implementacion no lanza error: devuelve vacio y la app consulta la
 * base de datos (fail-open).
 */
public interface AccountCachePort {

  Optional<AccountSummaryModel> get(UserId userId);

  void put(AccountSummaryModel summary);

  void evict(UserId userId);
}
