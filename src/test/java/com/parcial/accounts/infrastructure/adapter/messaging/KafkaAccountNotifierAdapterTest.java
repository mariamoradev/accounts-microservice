package com.parcial.accounts.infrastructure.adapter.messaging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parcial.accounts.TestData;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@DisplayName("KafkaAccountNotifierAdapter")
@ExtendWith(MockitoExtension.class)
class KafkaAccountNotifierAdapterTest {

  private static final String TOPIC = "notificaciones";

  @Mock private KafkaTemplate<String, String> kafkaTemplate;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private KafkaAccountNotifierAdapter adapter;

  @BeforeEach
  void setUp() {
    final KafkaConfig config =
        new KafkaConfig(
            "localhost:9092", TOPIC, "PLAINTEXT", "SCRAM-SHA-256", "", "", "", Duration.ofSeconds(1));
    adapter = new KafkaAccountNotifierAdapter(kafkaTemplate, objectMapper, config);
  }

  @Test
  @DisplayName("Paso 7: publica el JSON acordado { email, nombre, asunto, mensaje }")
  void shouldPublishAgreedFormat() throws Exception {
    final CompletableFuture<SendResult<String, String>> ok = CompletableFuture.completedFuture(null);
    when(kafkaTemplate.send(eq(TOPIC), eq(TestData.EMAIL), anyString())).thenReturn(ok);

    adapter.notifyAccountCreated(TestData.account(), "Ana Perez");

    final ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
    verify(kafkaTemplate).send(eq(TOPIC), eq(TestData.EMAIL), json.capture());
    final JsonNode node = objectMapper.readTree(json.getValue());
    assertEquals(4, node.size(), "solo los 4 campos acordados");
    assertEquals(TestData.EMAIL, node.get("email").asText());
    assertEquals("Ana Perez", node.get("nombre").asText());
    assertEquals(KafkaAccountNotifierAdapter.SUBJECT, node.get("asunto").asText());
    assertTrue(node.get("mensaje").asText().contains(TestData.NUMBER), "el correo trae el numero");
  }

  @Test
  @DisplayName("Si Kafka falla no lanza excepcion: la cuenta ya quedo creada")
  void shouldNotThrowWhenKafkaFails() {
    when(kafkaTemplate.send(eq(TOPIC), anyString(), anyString()))
        .thenThrow(new IllegalStateException("broker caido"));

    assertDoesNotThrow(() -> adapter.notifyAccountCreated(TestData.account(), "Ana"));
  }

  @Test
  @DisplayName("Escapa el HTML del nombre para que no rompa el correo")
  void shouldEscapeHtmlInName() {
    final String body = adapter.renderBody(TestData.account(), "<b>Ana</b>");
    assertTrue(body.contains("&lt;b&gt;Ana&lt;/b&gt;"));
  }
}
