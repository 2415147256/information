package com.hd123.baas.sop.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
@Slf4j
public class CacheConfiguration {

  @Autowired
  private CacheProperties config;

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    Caffeine caffeine = Caffeine.newBuilder()
        .expireAfterWrite(config.getExpireDuration(), TimeUnit.MINUTES)
        .maximumSize(config.getMaxSize());
    cacheManager.setCaffeine(caffeine);
    cacheManager.setAllowNullValues(true);
    return cacheManager;
  }

}
