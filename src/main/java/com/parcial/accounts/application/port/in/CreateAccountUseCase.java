package com.parcial.accounts.application.port.in;

import com.parcial.accounts.application.service.dto.command.CreateAccountCommand;
import com.parcial.accounts.domain.model.AccountModel;

/** Puerto de entrada del paso 6: crear la cuenta de un usuario nuevo. */
public interface CreateAccountUseCase {
  AccountModel execute(CreateAccountCommand command);
}
