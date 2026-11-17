package com.parcial.accounts.infrastructure.adapter.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parcial.accounts.TestData;
import com.parcial.accounts.domain.model.AccountSummaryModel;
import com.parcial.accounts.domain.valueobject.UserId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisConnectionException;

@DisplayName("ValkeyAccountCacheAdapter (Guia 10)")
@ExtendWith(MockitoExtension.class)
class ValkeyAccountCacheAdapterTest {

  private static final String KEY = "account:user:" + TestData.USER_ID;

  @Mock private JedisPooled jedis;

  private ValkeyAccountCacheAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new ValkeyAccountCacheAdapter(jedis, 600);
  }

  @Test
  @DisplayName("put() guarda con SETEX, TTL y sin el saldo")
  void shouldPutWithTtlAndWithoutBalance() {
    adapter.put(AccountSummaryModel.from(TestData.account()));

    final ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
    verify(jedis).setex(eq(KEY), eq(600L), json.capture());
    assertTrue(json.getValue().contains(TestData.NUMBER));
    assertFalse(json.getValue().contains("balance"));
  }

  @Test
  @DisplayName("get() lee el JSON guardado (HIT)")
  void shouldReadWhatWasStored() {
    final AccountSummaryModel summary = AccountSummaryModel.from(TestData.account());
    adapter.put(summary);
    final ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
    verify(jedis).setex(eq(KEY), anyLong(), json.capture());
    when(jedis.get(KEY)).thenReturn(json.getValue());

    assertEquals(Optional.of(summary), adapter.get(new UserId(TestData.USER_ID)));
  }

  @Test
  @DisplayName("get() devuelve vacio si la clave no existe (MISS)")
  void shouldReturnEmptyOnMiss() {
    when(jedis.get(KEY)).thenReturn(null);

    assertTrue(adapter.get(new UserId(TestData.USER_ID)).isEmpty());
  }

  @Test
  @DisplayName("fail-open: si Valkey se cae, get() devuelve vacio y put() no lanza error")
  void shouldFailOpenWhenValkeyIsDown() {
    when(jedis.get(anyString())).thenThrow(new JedisConnectionException("caido"));
    doThrow(new JedisConnectionException("caido"))
        .when(jedis)
        .setex(anyString(), anyLong(), anyString());

    assertTrue(adapter.get(new UserId(TestData.USER_ID)).isEmpty());
    adapter.put(AccountSummaryModel.from(TestData.account()));
  }

  @Test
  @DisplayName("evict() borra la clave del usuario")
  void shouldEvict() {
    adapter.evict(new UserId(TestData.USER_ID));

    verify(jedis).del(KEY);
  }
}
