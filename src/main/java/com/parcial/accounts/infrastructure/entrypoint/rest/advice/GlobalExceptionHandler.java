package com.parcial.accounts.infrastructure.entrypoint.rest.advice;

import com.parcial.accounts.domain.exception.AccountAlreadyExistsException;
import com.parcial.accounts.domain.exception.AccountNotFoundException;
import com.parcial.accounts.domain.exception.DomainException;
import com.parcial.accounts.infrastructure.adapter.persistence.PersistenceException;
import com.parcial.accounts.infrastructure.entrypoint.rest.dto.response.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** 409: users lo interpreta como "la cuenta ya existia" (ver su AccountsRestClientAdapter). */
  @ExceptionHandler(AccountAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiErrorResponse handleAlreadyExists(final AccountAlreadyExistsException exception) {
    return new ApiErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
  }

  @ExceptionHandler(AccountNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiErrorResponse handleNotFound(final AccountNotFoundException exception) {
    return new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrorResponse handleInvalidBody(final MethodArgumentNotValidException exception) {
    final String message =
        exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrorResponse handleConstraintViolation(final ConstraintViolationException exception) {
    final String message =
        exception.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
    return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
  }

  @ExceptionHandler(DomainException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrorResponse handleDomain(final DomainException exception) {
    return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
  }

  /** 503: users lo trata como fallo temporal y reintenta. */
  @ExceptionHandler(PersistenceException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public ApiErrorResponse handlePersistence(final PersistenceException exception) {
    log.error("[GlobalExceptionHandler] error de base de datos", exception);
    return new ApiErrorResponse(
        HttpStatus.SERVICE_UNAVAILABLE.value(), "Base de datos no disponible, intente de nuevo.");
  }
}
