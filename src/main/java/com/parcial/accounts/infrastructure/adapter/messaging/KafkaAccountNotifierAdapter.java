package com.parcial.accounts.infrastructure.adapter.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parcial.accounts.application.port.out.AccountCreatedNotifierPort;
import com.parcial.accounts.domain.model.AccountModel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Paso 7 (async): publica en el topico 'notificaciones' el correo de "cuenta creada". notifiers lo
 * lee y lo envia con Brevo.
 *
 * <p>No espera la confirmacion de Kafka y nunca lanza excepcion: si Kafka falla, la cuenta ya esta
 * creada y solo se registra el error en el log.
 */
@Slf4j
@Component
public class KafkaAccountNotifierAdapter implements AccountCreatedNotifierPort {

  static final String SUBJECT = "Tu cuenta fue creada — Gestion de Usuarios";
  private static final String TEMPLATE_PATH = "/templates/account-created.html";
  private static final String FALLBACK_TEMPLATE =
      "<p>Hola {{name}}, tu cuenta {{accountNumber}} fue creada. Saldo: ${{balance}}.</p>";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final KafkaConfig config;
  private final String template;

  public KafkaAccountNotifierAdapter(
      final KafkaTemplate<String, String> kafkaTemplate,
      final ObjectMapper objectMapper,
      final KafkaConfig config) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.config = config;
    this.template = loadTemplate();
  }

  @Override
  public void notifyAccountCreated(final AccountModel account, final String holderName) {
    final String email = account.getEmail().value();
    try {
      final NotificationMessage message =
          new NotificationMessage(email, holderName, SUBJECT, renderBody(account, holderName));
      final String json = objectMapper.writeValueAsString(message);
      kafkaTemplate
          .send(config.topic(), email, json)
          .whenComplete(
              (result, error) -> {
                if (error == null) {
                  log.info(
                      "[KafkaAccountNotifierAdapter] notificacion de cuenta creada para {} publicada en el topico '{}'.",
                      email,
                      config.topic());
                } else {
                  log.warn(
                      "[KafkaAccountNotifierAdapter] no se pudo publicar la notificacion para {}: {}",
                      email,
                      error.getMessage());
                }
              });
    } catch (final JsonProcessingException | RuntimeException exception) {
      log.warn(
          "[KafkaAccountNotifierAdapter] no se pudo publicar la notificacion para {}: {}",
          email,
          exception.getMessage());
    }
  }

  String renderBody(final AccountModel account, final String holderName) {
    String body = template;
    for (final Map.Entry<String, String> token :
        Map.of(
                "name", escapeHtml(holderName),
                "accountNumber", account.getAccountNumber().value(),
                "balance", account.getBalance().toPlainString(),
                "status", account.getStatus().name())
            .entrySet()) {
      body = body.replace("{{" + token.getKey() + "}}", token.getValue());
    }
    return body;
  }

  private String loadTemplate() {
    try (InputStream input = getClass().getResourceAsStream(TEMPLATE_PATH)) {
      if (input == null) {
        log.warn("[KafkaAccountNotifierAdapter] plantilla {} no encontrada.", TEMPLATE_PATH);
        return FALLBACK_TEMPLATE;
      }
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    } catch (final IOException exception) {
      return FALLBACK_TEMPLATE;
    }
  }

  private static String escapeHtml(final String value) {
    return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }
}
