package com.hd123.baas.sop.remote.rsaccount;

import java.nio.charset.Charset;

import com.hd123.baas.sop.config.AccountConfig;
import org.springframework.context.annotation.Bean;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import com.qianfan123.baas.common.feign.DynamicFeignParams;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;


public class RsAccountConfiguration extends BaseConfiguration {

  @Bean("Account-DynamicFeignParams")
  public DynamicFeignParams getDynamicParams() {
    return new DynamicFeignParams<AccountConfig>() {

      @Override
      public String getUrl(AccountConfig config) {
        return config.getServerUrl();
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(AccountConfig config) {
        return new BasicAuthRequestInterceptor(config.getUsername(), config.getPassword(),
          Charset.forName("UTF-8"));
      }
    };
  }

}
