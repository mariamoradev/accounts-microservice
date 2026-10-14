package com.parcial.accounts.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class DataSourceSpringConfig {

  @Bean
  public DataSource dataSource(
      @Value("${db.host}") final String host,
      @Value("${db.port}") final int port,
      @Value("${db.name}") final String name,
      @Value("${db.username}") final String username,
      @Value("${db.password}") final String password,
      @Value("${db.ssl-mode}") final String sslMode) {
    final DatabaseConfig config = new DatabaseConfig(host, port, name, username, password, sslMode);
    final HikariConfig hikari = new HikariConfig();
    hikari.setJdbcUrl(config.buildJdbcUrl());
    hikari.setUsername(config.username());
    hikari.setPassword(config.password());
    hikari.setMaximumPoolSize(5);
    hikari.setMinimumIdle(1);
    hikari.setConnectionTimeout(30_000);
    log.info("[DataSourceSpringConfig] DataSource inicializado. host={} port={}", host, port);
    return new HikariDataSource(hikari);
  }

  @Bean
  public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
