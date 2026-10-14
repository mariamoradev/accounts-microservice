package com.parcial.accounts.infrastructure.entrypoint.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.parcial.accounts.TestData;
import com.parcial.accounts.application.port.in.CreateAccountUseCase;
import com.parcial.accounts.application.port.in.GetAccountUseCase;
import com.parcial.accounts.domain.exception.AccountAlreadyExistsException;
import com.parcial.accounts.domain.exception.AccountNotFoundException;
import com.parcial.accounts.infrastructure.entrypoint.rest.controller.AccountRestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = AccountRestController.class,
    properties = "spring.main.web-application-type=servlet")
@DisplayName("AccountRestController")
class AccountRestControllerTest {

  private static final String BODY =
      "{\"userId\":\"1043960765\",\"email\":\"ana@gmail.com\",\"nombre\":\"Ana Perez\"}";

  @Autowired private MockMvc mockMvc;
  @MockBean private CreateAccountUseCase createAccountUseCase;
  @MockBean private GetAccountUseCase getAccountUseCase;

  @Test
  @DisplayName("POST /api/accounts responde 201 con la cuenta creada")
  void shouldCreateAccount() throws Exception {
    when(createAccountUseCase.execute(any())).thenReturn(TestData.account());

    mockMvc
        .perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(TestData.USER_ID))
        .andExpect(jsonPath("$.accountNumber").value(TestData.NUMBER))
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(jsonPath("$.balance").value(0.0));
  }

  @Test
  @DisplayName("POST /api/accounts responde 409 si el usuario ya tiene cuenta")
  void shouldReturnConflictWhenAccountExists() throws Exception {
    when(createAccountUseCase.execute(any()))
        .thenThrow(AccountAlreadyExistsException.becauseUserAlreadyHasAccount(TestData.USER_ID));

    mockMvc
        .perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409));
  }

  @Test
  @DisplayName("POST /api/accounts responde 400 si falta un campo")
  void shouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"\",\"email\":\"x\"}"))
        .andExpect(status().isBadRequest());
    verify(createAccountUseCase, never()).execute(any());
  }

  @Test
  @DisplayName("GET /api/accounts/user/{userId} responde 404 si no tiene cuenta")
  void shouldReturnNotFound() throws Exception {
    when(getAccountUseCase.getByUserId("nadie"))
        .thenThrow(AccountNotFoundException.becauseUserHasNoAccount("nadie"));

    mockMvc.perform(get("/api/accounts/user/nadie")).andExpect(status().isNotFound());
  }
}
