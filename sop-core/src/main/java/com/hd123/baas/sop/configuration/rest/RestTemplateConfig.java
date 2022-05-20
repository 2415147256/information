package com.hd123.baas.sop.configuration.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Autowired
  private RestTemplateBuilder builder;

  @Bean
  @Primary
  public RestTemplate restTemplate() {
    return builder.build();
  }
}
