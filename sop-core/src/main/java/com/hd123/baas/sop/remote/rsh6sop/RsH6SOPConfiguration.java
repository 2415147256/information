package com.hd123.baas.sop.remote.rsh6sop;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.feign.DynamicFeignParams;
import feign.RequestInterceptor;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

/**
 * @author lilong on 2020/10/28.
 * <p>
 * basic
 */
public class RsH6SOPConfiguration extends BaseConfiguration {

  @Bean("H6SOP-DynamicFeignParams")
  public DynamicFeignParams getDynamicParams() {
    return new DynamicFeignParams<RsH6SOPConfig>() {

      @Override
      public String getUrl(RsH6SOPConfig config) {
        return config.getServerUrl();
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(RsH6SOPConfig config) {
        return template -> {
          if (StringUtil.isNullOrBlank(MDC.get("trace_id"))) {
            MDC.put("trace_id", UUID.randomUUID().toString().replace("-", ""));
          }
          template.header("trace_id", MDC.get("trace_id"));
          template.header("Authorization",
            "Basic " + Base64.encodeBase64String((config.getUsername() + ":" + config.getPassword())
              .getBytes()));
        };
      }
    };
  }

}
