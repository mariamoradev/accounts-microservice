package com.parcial.accounts.application.service;

import com.parcial.accounts.application.port.in.CreateAccountUseCase;
import com.parcial.accounts.application.port.out.AccountCreatedNotifierPort;
import com.parcial.accounts.application.port.out.AccountNumberGeneratorPort;
import com.parcial.accounts.application.port.out.GetAccountPort;
import com.parcial.accounts.application.port.out.SaveAccountPort;
import com.parcial.accounts.application.service.dto.command.CreateAccountCommand;
import com.parcial.accounts.domain.exception.AccountAlreadyExistsException;
import com.parcial.accounts.domain.model.AccountModel;
import com.parcial.accounts.domain.valueobject.AccountEmail;
import com.parcial.accounts.domain.valueobject.AccountId;
import com.parcial.accounts.domain.valueobject.UserId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Paso 6: guarda la cuenta del usuario nuevo. Paso 7: avisa por Kafka que la cuenta se creo.
 *
 * <p>Un usuario solo puede tener una cuenta: si users reintenta (por ejemplo tras un timeout), la
 * segunda peticion responde 409 y users lo toma como "la cuenta ya existe".
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAccountService implements CreateAccountUseCase {

  private final SaveAccountPort saveAccountPort;
  private final GetAccountPort getAccountPort;
  private final AccountNumberGeneratorPort accountNumberGenerator;
  private final AccountCreatedNotifierPort accountCreatedNotifier;
  private final Validator validator;

  @Override
  public AccountModel execute(final CreateAccountCommand command) {
    validateCommand(command);

    final UserId userId = new UserId(command.userId());
    ensureUserHasNoAccount(userId);

    final AccountModel account =
        AccountModel.open(
            new AccountId(UUID.randomUUID().toString()),
            userId,
            accountNumberGenerator.next(),
            new AccountEmail(command.email()));
    final AccountModel saved = saveAccountPort.save(account);
    log.info(
        "[CreateAccountService] cuenta {} creada para el usuario {}.",
        saved.getAccountNumber().value(),
        userId.value());

    // Paso 7 (async): deja en Kafka el mensaje de "cuenta creada" para notifiers.
    accountCreatedNotifier.notifyAccountCreated(saved, command.nombre().trim());
    return saved;
  }

  private void validateCommand(final CreateAccountCommand command) {
    final Set<ConstraintViolation<CreateAccountCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private void ensureUserHasNoAccount(final UserId userId) {
    getAccountPort
        .getByUserId(userId)
        .ifPresent(
            existing -> {
              throw AccountAlreadyExistsException.becauseUserAlreadyHasAccount(userId.value());
            });
  }
}
