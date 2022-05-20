package com.hd123.baas.sop.remote.jwt;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.hd123.baas.sop.configuration.BaseConfiguration;

import feign.auth.BasicAuthRequestInterceptor;

/**
 * @author zzc
 * @description
 */
public class TlspAuthConfiguration extends BaseConfiguration {

  @Value("${sop-service.tlsp.username}")
  private String username;
  @Value("${sop-service.tlsp.password}")
  private String password;

  @Bean
  public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    return new BasicAuthRequestInterceptor(username, password, StandardCharsets.UTF_8);
  }

}
