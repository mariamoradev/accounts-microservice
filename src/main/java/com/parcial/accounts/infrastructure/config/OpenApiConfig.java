package com.parcial.accounts.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(
    info =
        @Info(
            title = "Accounts API",
            version = "1.0.0",
            description = "Microservicio accounts: crea y consulta la cuenta de cada usuario."))
public class OpenApiConfig {}
