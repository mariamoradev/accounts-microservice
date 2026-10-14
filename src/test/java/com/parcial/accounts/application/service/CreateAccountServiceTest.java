package com.parcial.accounts.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.parcial.accounts.TestData;
import com.parcial.accounts.application.port.out.AccountCreatedNotifierPort;
import com.parcial.accounts.application.port.out.AccountNumberGeneratorPort;
import com.parcial.accounts.application.port.out.GetAccountPort;
import com.parcial.accounts.application.port.out.SaveAccountPort;
import com.parcial.accounts.application.service.dto.command.CreateAccountCommand;
import com.parcial.accounts.domain.enums.AccountStatus;
import com.parcial.accounts.domain.exception.AccountAlreadyExistsException;
import com.parcial.accounts.domain.model.AccountModel;
import com.parcial.accounts.domain.valueobject.AccountNumber;
import com.parcial.accounts.domain.valueobject.UserId;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("CreateAccountService")
@ExtendWith(MockitoExtension.class)
class CreateAccountServiceTest {

  @Mock private SaveAccountPort saveAccountPort;
  @Mock private GetAccountPort getAccountPort;
  @Mock private AccountNumberGeneratorPort numberGenerator;
  @Mock private AccountCreatedNotifierPort notifier;

  private CreateAccountService service;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      service =
          new CreateAccountService(
              saveAccountPort, getAccountPort, numberGenerator, notifier, factory.getValidator());
    }
  }

  @Test
  @DisplayName("Paso 6 y 7: guarda la cuenta ACTIVE con saldo 0 y publica la notificacion")
  void shouldCreateAccountAndNotify() {
    when(getAccountPort.getByUserId(new UserId(TestData.USER_ID))).thenReturn(Optional.empty());
    when(numberGenerator.next()).thenReturn(new AccountNumber(TestData.NUMBER));
    when(saveAccountPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    final AccountModel result =
        service.execute(new CreateAccountCommand(TestData.USER_ID, "Ana@Gmail.com", " Ana Perez "));

    final ArgumentCaptor<AccountModel> saved = ArgumentCaptor.forClass(AccountModel.class);
    verify(saveAccountPort).save(saved.capture());
    assertEquals(TestData.USER_ID, saved.getValue().getUserId().value());
    assertEquals("ana@gmail.com", saved.getValue().getEmail().value());
    assertEquals(TestData.NUMBER, saved.getValue().getAccountNumber().value());
    assertEquals(AccountStatus.ACTIVE, saved.getValue().getStatus());
    assertEquals(new BigDecimal("0.00"), saved.getValue().getBalance());
    verify(notifier).notifyAccountCreated(eq(result), eq("Ana Perez"));
  }

  @Test
  @DisplayName("Si el usuario ya tiene cuenta lanza AccountAlreadyExistsException (409) sin guardar")
  void shouldRejectSecondAccountForSameUser() {
    when(getAccountPort.getByUserId(new UserId(TestData.USER_ID)))
        .thenReturn(Optional.of(TestData.account()));

    final CreateAccountCommand command =
        new CreateAccountCommand(TestData.USER_ID, TestData.EMAIL, "Ana");
    assertThrows(AccountAlreadyExistsException.class, () -> service.execute(command));
    verify(saveAccountPort, never()).save(any());
    verify(notifier, never()).notifyAccountCreated(any(), any());
  }

  @Test
  @DisplayName("Un comando invalido no toca la base de datos ni Kafka")
  void shouldRejectInvalidCommand() {
    final CreateAccountCommand command = new CreateAccountCommand("", "no-es-email", "");
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(saveAccountPort, getAccountPort, numberGenerator, notifier);
  }
}
