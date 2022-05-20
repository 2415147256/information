package com.hd123.baas.sop.remote.isvmkh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import feign.Logger;
import feign.Request;

public class BaseRemoteConfiguration {
  @Autowired
  private Environment env;

  @Bean
  public Request.Options options() {
    return new Request.Options(Integer.parseInt(env.getProperty("ribbon.ConnectTimeout")),
        Integer.parseInt(env.getProperty("ribbon.ReadTimeout")));
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }
}
