package com.hd123.baas.sop.remote.bigdata;

import com.hd123.baas.sop.config.BaasBigDataConfig;
import com.hd123.baas.sop.configuration.BaseConfiguration;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.feign.DynamicFeignParams;
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

/**
 * @author shenmin
 */
public class BigDataConfiguration extends BaseConfiguration {
  @Bean("BigData-DynamicFeignParams")
  public DynamicFeignParams getDynamicParams() {
    return new DynamicFeignParams<BaasBigDataConfig>() {

      @Override
      public String getUrl(BaasBigDataConfig config) {
        return config.getServerUrl();
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(BaasBigDataConfig config) {
        return template -> {
          if (StringUtil.isNullOrBlank(MDC.get("trace_id"))) {
            MDC.put("trace_id", UUID.randomUUID().toString().replace("-", ""));
          }
          template.header("trace_id", MDC.get("trace_id"));
          template.header("Authorization", config.getToken());
        };
      }
    };
  }
}
