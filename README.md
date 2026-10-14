# accounts — Microservicio de cuentas (Spring Boot, arquitectura hexagonal)

Parte del parcial de microservicios (users · accounts · notifiers). Este servicio hace los
**pasos 6 y 7** del diagrama:

- **Paso 6 (sync):** `users` llama a `POST /api/accounts` cuando se crea un usuario. accounts
  guarda la cuenta en la tabla `accounts` de TiDB (base `crud_usuarios`): estado `ACTIVE`,
  saldo `0.00` y un numero de cuenta de 10 digitos.
- **Paso 7 (async):** despues de guardar, publica en el topico de Kafka `notificaciones` el
  mensaje `{ "email", "nombre", "asunto", "mensaje" }` con el correo de "cuenta creada";
  notifiers lo lee y lo envia con Brevo.

## Endpoints

| Metodo | Ruta | Respuesta |
|---|---|---|
| POST | `/api/accounts` | 201 cuenta creada · 409 el usuario ya tiene cuenta · 400 datos invalidos |
| GET | `/api/accounts` | lista de cuentas |
| GET | `/api/accounts/{id}` | la cuenta · 404 |
| GET | `/api/accounts/user/{userId}` | la cuenta del usuario · 404 |
| GET | `/health` | `{"status":"UP"}` (health check de Render) |

Swagger UI: `/swagger-ui.html`.

Cuerpo de `POST /api/accounts` (contrato acordado con users):

```json
{ "userId": "1043960765", "email": "ana@gmail.com", "nombre": "Ana Perez" }
```

El 409 es importante: users reintenta si accounts tarda (Render dormido) y toma el 409 como
"la cuenta ya existia".

## Arquitectura

```
domain/          AccountModel, value objects (AccountId, UserId, AccountEmail, AccountNumber),
                 AccountStatus, excepciones de negocio
application/     puertos de entrada (CreateAccountUseCase, GetAccountUseCase),
                 puertos de salida (SaveAccountPort, GetAccountPort, AccountNumberGeneratorPort,
                 AccountCreatedNotifierPort) y servicios
infrastructure/  entrypoint REST, adaptador MySQL (JdbcTemplate), adaptador Kafka,
                 generador de numero de cuenta, configuracion
```

## Base de datos

`src/main/resources/schema.sql` crea la tabla. Si ya existia la version corta (sin
`account_number` ni `balance`), ejecutar las tres instrucciones `ALTER`/`CREATE INDEX`
comentadas al final del archivo, una por una.

## Desarrollo local

```bash
./mvnw clean test
./mvnw spring-boot:run      # puerto 8081
```

Secretos locales: copiar `application-secrets.properties.example` como
`.environments/application-secrets.properties` (ignorado por Git).

## Despliegue en Render

`render.yaml` define el servicio `accounts-api` (Docker, plan free). Completar en el panel:
`DB_HOST`, `DB_USERNAME`, `DB_PASSWORD`, `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_USERNAME`,
`KAFKA_PASSWORD` y `KAFKA_CA_CERT`. Luego poner la URL publica de accounts en la variable
`ACCOUNTS_URL` del servicio users.
