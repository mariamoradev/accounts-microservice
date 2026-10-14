package com.parcial.accounts.infrastructure.entrypoint.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpo acordado con users (paso 5): { "userId", "email", "nombre" }. */
public record CreateAccountRestRequest(
    @Schema(example = "1043960765") @NotBlank(message = "userId es obligatorio")
        @Size(max = 36, message = "userId admite maximo 36 caracteres")
        String userId,
    @Schema(example = "ana@gmail.com") @NotBlank(message = "email es obligatorio")
        @Email(message = "email no es valido")
        String email,
    @Schema(example = "Ana Perez") @NotBlank(message = "nombre es obligatorio") String nombre) {}
