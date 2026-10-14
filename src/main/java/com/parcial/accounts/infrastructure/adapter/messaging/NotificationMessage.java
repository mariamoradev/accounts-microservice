package com.parcial.accounts.infrastructure.adapter.messaging;

/**
 * Mensaje del topico 'notificaciones'. Contrato acordado por el grupo (igual en users, accounts y
 * notifiers): { "email", "nombre", "asunto", "mensaje" }.
 */
public record NotificationMessage(String email, String nombre, String asunto, String mensaje) {}
