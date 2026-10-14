package com.parcial.accounts.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.parcial.accounts.TestData;
import com.parcial.accounts.application.port.out.GetAccountPort;
import com.parcial.accounts.domain.exception.AccountNotFoundException;
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
}
