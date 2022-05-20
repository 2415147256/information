package com.hd123.baas.sop.remote.workwx;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.WorkWxConfig;
import com.hd123.baas.sop.configuration.BaseConfiguration;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.redis.RedisService;
import com.hd123.baas.sop.remote.workwx.response.WorkWxTokenResponse;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.feign.DynamicFeignParams;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;


/**
 * @author W.J.H.7
 */
@Slf4j
public class WorkWxConfiguration extends BaseConfiguration {

  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private RedisService redisService;
  @Autowired
  private BaasConfigClient configClient;

  @Bean
  public DynamicFeignParams getDynamicParams() {
    return new DynamicFeignParams<WorkWxConfig>() {

      @Override
      public String getUrl(WorkWxConfig config) {
        return config.getServerUrl();
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(WorkWxConfig config) {
        return new WxRequestInterceptor();
      }
    };
  }

  public class WxRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
      if (!(template.url().contains(WorkWxClient.TOKEN_URL))) {
        try {
          String tenant = template.headers().get("tenant").iterator().next();
          WorkWxConfig config = configClient.getConfig(tenant, WorkWxConfig.class);
          String corpId = config.getCorpId();
          String corpSecret = config.getCorpSecret();
          String key = buildKey(tenant, corpId, corpSecret);
          String rdToken = redisService.get(key, String.class);
          if (StringUtil.isNullOrBlank(rdToken)) {
            // 过期或是不存在 重新获取
            WorkWxClient client = feignClientMgr.getClient(tenant, null, WorkWxClient.class);
            WorkWxTokenResponse tokenReq = client.token(corpId, corpSecret);
            if (!tokenReq.success()) {
              throw new RuntimeException(tokenReq.getErrMsg());
            }
            int expiresIn = tokenReq.getExpiresIn();
            if (expiresIn > 60) {
              expiresIn = expiresIn - 60;
              redisService.set(key, tokenReq.getAccessToken(), expiresIn, TimeUnit.SECONDS);
            }
            rdToken = tokenReq.getAccessToken();
          }
          template.query("access_token", rdToken);
        } catch (Exception e) {
          log.error("发送企业微信数据失败", e);
          throw new RuntimeException(e.getMessage());
        }
      }
    }
  }

  private String buildKey(String tenant, String corpId, String corpSecret) {
    return tenant + "_" + corpId + "_" + corpSecret.substring(0, 8);
  }

}
