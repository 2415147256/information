package com.hd123.baas.sop.configuration.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import feign.Feign;

/**
 * @Author maodapeng
 * @Since
 */

@Configuration
@ConditionalOnClass({
    Feign.class })
public class FeignConfiguration implements WebMvcRegistrations {

  private RequestMappingHandlerMapping requestMappingHandlerMapping = new FeignRequestMappingHandlerMapping();

  @Override
  @Nullable
  public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
    return requestMappingHandlerMapping;
  }

  private static class FeignRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected boolean isHandler(Class<?> beanType) {
      return super.isHandler(beanType) && beanType.getAnnotation(FeignClient.class) == null;
    }
  }

}
