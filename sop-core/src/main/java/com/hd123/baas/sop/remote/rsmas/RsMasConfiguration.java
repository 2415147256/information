package com.hd123.baas.sop.remote.rsmas;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import com.hd123.rumba.commons.lang.StringUtil;
import feign.RequestInterceptor;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.UUID;

/**
 * @author Silent
 **/
public class RsMasConfiguration extends BaseConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public RequestInterceptor masInterceptor() {
    return template -> {
      if (StringUtil.isNullOrBlank(MDC.get("trace_id"))) {
        MDC.put("trace_id", UUID.randomUUID().toString().replace("-", ""));
      }

      String username = env.getProperty("mas-openapi-service.username", String.class, "guest");
      String password = env.getProperty("mas-openapi-service.password", String.class, "guest");

      template.header("trace_id", MDC.get("trace_id"));
      template.header("Authorization",
          "Basic " + Base64.encodeBase64String((username + ":" + password).getBytes()));
    };
  }

}
