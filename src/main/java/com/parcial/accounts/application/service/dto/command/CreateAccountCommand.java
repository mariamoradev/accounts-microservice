package com.parcial.accounts.application.service.dto.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos que envia users en el paso 5: { userId, email, nombre }. */
public record CreateAccountCommand(
    @NotBlank @Size(max = 36) String userId,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank String nombre) {}
