package com.parcial.accounts.infrastructure.config;

import com.parcial.accounts.application.port.out.AccountCachePort;
import com.parcial.accounts.infrastructure.adapter.cache.NoOpAccountCacheAdapter;
import com.parcial.accounts.infrastructure.adapter.cache.ValkeyAccountCacheAdapter;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

/**
 * Guia 10: conexion a Valkey (Aiven). VALKEY_URI es el "Service URI" de Aiven y empieza por
 * {@code rediss://} (con TLS).
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CacheSpringConfig {

  @Bean
  public AccountCachePort accountCachePort(
      @Value("${cache.valkey.uri:}") final String valkeyUri,
      @Value("${cache.account.ttl-seconds:600}") final long ttlSeconds,
      @Value("${cache.valkey.timeout-ms:2000}") final int timeoutMs) {
    if (valkeyUri == null || valkeyUri.isBlank()) {
      log.warn("[CacheSpringConfig] VALKEY_URI vacio: la cache esta desactivada.");
      return new NoOpAccountCacheAdapter();
    }
    final URI uri = URI.create(valkeyUri.trim());
    log.info(
        "[CacheSpringConfig] cache Valkey activa. host={} port={} ttl={}s",
        uri.getHost(),
        uri.getPort(),
        ttlSeconds);
    return new ValkeyAccountCacheAdapter(new JedisPooled(uri, timeoutMs), ttlSeconds);
  }
}
