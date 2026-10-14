package com.parcial.accounts.infrastructure.entrypoint.rest.controller;

import com.parcial.accounts.application.port.in.CreateAccountUseCase;
import com.parcial.accounts.application.port.in.GetAccountUseCase;
import com.parcial.accounts.application.service.dto.command.CreateAccountCommand;
import com.parcial.accounts.infrastructure.entrypoint.rest.dto.request.CreateAccountRestRequest;
import com.parcial.accounts.infrastructure.entrypoint.rest.dto.response.AccountRestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Entry point REST. users llama a POST /api/accounts en el paso 5 (sincrono). */
@Tag(name = "Accounts", description = "Cuentas de los usuarios")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountRestController {

  private final CreateAccountUseCase createAccountUseCase;
  private final GetAccountUseCase getAccountUseCase;

  @Operation(summary = "Crear la cuenta de un usuario (201; 409 si ya tiene una)")
  @PostMapping
  public ResponseEntity<AccountRestResponse> create(
      @Valid @RequestBody final CreateAccountRestRequest request) {
    final CreateAccountCommand command =
        new CreateAccountCommand(request.userId(), request.email(), request.nombre());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(AccountRestResponse.from(createAccountUseCase.execute(command)));
  }

  @Operation(summary = "Listar todas las cuentas")
  @GetMapping
  public List<AccountRestResponse> getAll() {
    return getAccountUseCase.getAll().stream().map(AccountRestResponse::from).toList();
  }

  @Operation(summary = "Consultar una cuenta por su id")
  @GetMapping("/{id}")
  public AccountRestResponse getById(@PathVariable final String id) {
    return AccountRestResponse.from(getAccountUseCase.getById(id));
  }

  @Operation(summary = "Consultar la cuenta de un usuario")
  @GetMapping("/user/{userId}")
  public AccountRestResponse getByUserId(@PathVariable final String userId) {
    return AccountRestResponse.from(getAccountUseCase.getByUserId(userId));
  }
}
