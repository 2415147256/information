package com.hd123.baas.sop.remote.rsIwms;


import org.springframework.context.annotation.Bean;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import com.qianfan123.baas.common.feign.DynamicFeignParams;

import feign.RequestInterceptor;

/**
 * @author W.J.H.7
 * 
 *         basic
 */
public class RsIwmsConfiguration extends BaseConfiguration {

  @Bean
  public DynamicFeignParams getDynamicParams() {
    return new DynamicFeignParams<BassIwmsConfig>() {

      @Override
      public String getUrl(BassIwmsConfig config) {
        return config.getServerUrl();
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(BassIwmsConfig rsBassIwmsConfig) {
        return null;
      }
    };
  }

}
