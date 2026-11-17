package com.parcial.accounts.infrastructure.adapter.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parcial.accounts.application.port.out.AccountCachePort;
import com.parcial.accounts.domain.model.AccountSummaryModel;
import com.parcial.accounts.domain.valueobject.UserId;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPooled;

/**
 * Guia 10: guarda en Valkey (Aiven) los datos estables de la cuenta de cada usuario.
 *
 * <ul>
 *   <li>Clave: {@code account:user:{userId}}
 *   <li>Valor: JSON sin el saldo
 *   <li>Expira solo despues del TTL (SETEX)
 * </ul>
 *
 * <p>Fail-open: si Valkey no responde, se registra un aviso y la app sigue con la base de datos.
 */
@Slf4j
public class ValkeyAccountCacheAdapter implements AccountCachePort, AutoCloseable {

  static final String KEY_PREFIX = "account:user:";

  private final JedisPooled jedis;
  private final long ttlSeconds;
  private final ObjectMapper objectMapper;

  public ValkeyAccountCacheAdapter(final JedisPooled jedis, final long ttlSeconds) {
    this.jedis = jedis;
    this.ttlSeconds = ttlSeconds;
    this.objectMapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  static String keyOf(final String userId) {
    return KEY_PREFIX + userId;
  }

  @Override
  public Optional<AccountSummaryModel> get(final UserId userId) {
    try {
      final String json = jedis.get(keyOf(userId.value()));
      if (json == null) {
        return Optional.empty();
      }
      return Optional.of(objectMapper.readValue(json, AccountSummaryModel.class));
    } catch (final JsonProcessingException exception) {
      log.warn("[Valkey] valor invalido en {}, se ignora: {}", keyOf(userId.value()), exception.getMessage());
      return Optional.empty();
    } catch (final RuntimeException exception) {
      log.warn("[Valkey] no disponible al leer ({}). Se usa la base de datos.", exception.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public void put(final AccountSummaryModel summary) {
    try {
      jedis.setex(keyOf(summary.userId()), ttlSeconds, objectMapper.writeValueAsString(summary));
    } catch (final JsonProcessingException exception) {
      log.warn("[Valkey] no se pudo convertir a JSON: {}", exception.getMessage());
    } catch (final RuntimeException exception) {
      log.warn("[Valkey] no disponible al guardar ({}).", exception.getMessage());
    }
  }

  @Override
  public void evict(final UserId userId) {
    try {
      jedis.del(keyOf(userId.value()));
    } catch (final RuntimeException exception) {
      log.warn("[Valkey] no disponible al borrar ({}).", exception.getMessage());
    }
  }

  @Override
  public void close() {
    jedis.close();
  }
}
