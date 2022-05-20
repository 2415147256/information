package com.hd123.baas.sop.remote.rsdemo;

import java.nio.charset.Charset;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import org.springframework.context.annotation.Bean;

import com.qianfan123.baas.common.feign.DynamicFeignParams;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;

/**
 * @author zhengzewang on 2020/10/28.
 * 
 *         basic
 */
public class RsDemoConfiguration extends BaseConfiguration {

  @Bean
  public DynamicFeignParams getDynamicParams() {
    return new DynamicFeignParams<RsDemoConfig>() {

      @Override
      public String getUrl(RsDemoConfig config) {
        return config.getServerUrl();
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(RsDemoConfig config) {
        return new BasicAuthRequestInterceptor(config.getUsername(), config.getPassword(), Charset.forName("UTF-8"));
      }
    };
  }

}
