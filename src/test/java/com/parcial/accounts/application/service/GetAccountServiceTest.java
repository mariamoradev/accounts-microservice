package com.parcial.accounts.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parcial.accounts.TestData;
import com.parcial.accounts.application.port.out.AccountCachePort;
import com.parcial.accounts.application.port.out.GetAccountPort;
import com.parcial.accounts.domain.exception.AccountNotFoundException;
import com.parcial.accounts.domain.model.AccountSummaryModel;
import com.parcial.accounts.domain.valueobject.AccountId;
import com.parcial.accounts.domain.valueobject.UserId;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("GetAccountService")
@ExtendWith(MockitoExtension.class)
class GetAccountServiceTest {

  @Mock private GetAccountPort getAccountPort;
  @Mock private AccountCachePort accountCachePort;
  @InjectMocks private GetAccountService service;

  @Test
  @DisplayName("getByUserId devuelve la cuenta del usuario")
  void shouldReturnAccountOfUser() {
    when(getAccountPort.getByUserId(new UserId(TestData.USER_ID)))
        .thenReturn(Optional.of(TestData.account()));

    assertEquals(TestData.NUMBER, service.getByUserId(TestData.USER_ID).getAccountNumber().value());
  }

  @Test
  @DisplayName("getById lanza AccountNotFoundException (404) si no existe")
  void shouldThrowWhenAccountDoesNotExist() {
    when(getAccountPort.getById(new AccountId("no-existe"))).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class, () -> service.getById("no-existe"));
  }

  @Test
  @DisplayName("getSummaryByUserId con CACHE HIT no consulta la base de datos")
  void shouldReturnSummaryFromCache() {
    final AccountSummaryModel cached = AccountSummaryModel.from(TestData.account());
    when(accountCachePort.get(new UserId(TestData.USER_ID))).thenReturn(Optional.of(cached));

    assertEquals(cached, service.getSummaryByUserId(TestData.USER_ID));
    verify(getAccountPort, never()).getByUserId(any());
    verify(accountCachePort, never()).put(any());
  }

  @Test
  @DisplayName("getSummaryByUserId con CACHE MISS consulta la base de datos y guarda en cache")
  void shouldLoadSummaryFromDatabaseAndCacheIt() {
    final UserId userId = new UserId(TestData.USER_ID);
    when(accountCachePort.get(userId)).thenReturn(Optional.empty());
    when(getAccountPort.getByUserId(userId)).thenReturn(Optional.of(TestData.account()));

    final AccountSummaryModel summary = service.getSummaryByUserId(TestData.USER_ID);

    assertEquals(TestData.NUMBER, summary.accountNumber());
    verify(accountCachePort).put(summary);
  }

  @Test
  @DisplayName("getSummaryByUserId lanza 404 si el usuario no tiene cuenta y no guarda nada")
  void shouldThrowWhenSummaryNotFound() {
    final UserId userId = new UserId(TestData.USER_ID);
    when(accountCachePort.get(userId)).thenReturn(Optional.empty());
    when(getAccountPort.getByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(
        AccountNotFoundException.class, () -> service.getSummaryByUserId(TestData.USER_ID));
    verify(accountCachePort, never()).put(any());
  }
}
