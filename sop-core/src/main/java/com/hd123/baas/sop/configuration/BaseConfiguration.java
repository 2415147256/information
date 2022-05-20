package com.hd123.baas.sop.configuration;

import feign.Logger;
import feign.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author zhengzewang on 2020/10/28.
 */
public class BaseConfiguration {
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
