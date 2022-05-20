package com.hd123.baas.sop.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@ConfigurationProperties(prefix = "sop-service.cache")
public class CacheProperties {
  private int expireDuration = 60;
  private int maxSize = 4000;
}
