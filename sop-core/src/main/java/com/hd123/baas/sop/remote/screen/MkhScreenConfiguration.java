package com.hd123.baas.sop.remote.screen;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPConfig;
import com.qianfan123.baas.common.feign.DynamicFeignParams;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import com.hd123.rumba.commons.lang.StringUtil;

import feign.RequestInterceptor;

/**
 * @author lilong on 2020/10/28.
 *         <p>
 *         basic
 */
public class MkhScreenConfiguration extends BaseConfiguration {

  @Autowired
  private Environment env;

  @Bean("MKHSCREEN-DynamicFeignParams")
  public DynamicFeignParams getDynamicParams() {
    return new DynamicFeignParams<MkhScreenConfig>() {

      @Override
      public String getUrl(MkhScreenConfig config) {
        return config.getServerUrl();
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(MkhScreenConfig config) {
        return template -> {
          // 获取头部信息
          String username = env.getProperty("sos-service.username", String.class);
          String password = env.getProperty("sos-service.password", String.class);

          Map<String, Collection<String>> headers = template.headers();
          if (StringUtil.isNullOrBlank(MDC.get("trace_id"))) {
            MDC.put("trace_id", UUID.randomUUID().toString().replace("-", ""));
          }
          if (StringUtils.isBlank(username)) {
            username = config.getUsername();
          }
          if (StringUtils.isBlank(password)) {
            password = config.getPassword();
          }
          template.header("trace_id", MDC.get("trace_id"));
          template.header("Authorization",
              "Basic " + Base64.encodeBase64String((username + ":" + password).getBytes()));
        };
      }
    };
  }
}
