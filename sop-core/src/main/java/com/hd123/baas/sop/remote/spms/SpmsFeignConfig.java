package com.hd123.baas.sop.remote.spms;

import com.auth0.jwt.Algorithm;
import com.auth0.jwt.JWTSigner;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.feign.DynamicFeignParams;
import feign.Request;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class SpmsFeignConfig {

  @Autowired
  private Environment env;

  @Bean
  public Request.Options options() {
    return new Request.Options(
            StringUtil.toInteger(env.getProperty("ribbon.ConnectTimeout"), Integer.valueOf(300000)),
            StringUtil.toInteger(env.getProperty("ribbon.ReadTimeout"), Integer.valueOf(300000)));
  }

  @Bean
  public DynamicFeignParams getDynamicParams() {
    return new DynamicFeignParams<TransferSyncConfig>() {

      @Override
      public String getUrl(TransferSyncConfig config) {
        return StringUtil.toString(config.getUrl(), env.getProperty("spms-sync-server.url", ""));
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(TransferSyncConfig config) {
        return template -> {
          Map<String, Collection<String>> headers = template.headers();
          if (StringUtil.isNullOrBlank(MDC.get("trace_id"))) {
            MDC.put("trace_id", UUID.randomUUID().toString().replace("-", ""));
          }
          template.header("trace_id", MDC.get("trace_id"));
          template.header("tenant", config.getTenantId());
          template.header("shop", "-");
          template.header("Authorization", buildToken(config.getTenantId(), config.getAccessKeyId(), config.getAccessKeySecret()));

          StringBuilder sb = new StringBuilder();
          sb.append("\r\n===========================================================================\r\n");
          sb.append("inbound Message: " + "\r\n");
          sb.append("url: ").append(config.getUrl()).append("/").append(template.request().url()).append("\r\n");
          sb.append("method: ").append(template.method()).append("\r\n");
          sb.append("headers: ").append(template.headers()).append("\r\n");
          sb.append("params: ").append(template.queryLine()).append("\r\n");
          sb.append("body: ").append(template.requestBody() == null ? "" : template.requestBody().asString()).append("\r\n");
          sb.append("===========================================================================");
          log.info(sb.toString());
        };
      }

      public String buildToken(String tenant, String key, String secret) {
        Map<String, Object> tokenValues = new HashMap<>();
        tokenValues.put("shop", "-");
        tokenValues.put("tenant", tenant);
        tokenValues.put("iss", key);

        // JWT签名设置
        JWTSigner.Options options = new JWTSigner.Options().setAlgorithm(Algorithm.HS256);
        options.setIssuedAt(true);

        JWTSigner jwtSigner = new JWTSigner(secret);
        return "Bearer " + jwtSigner.sign(tokenValues, options);
      }

    };
  }
}
