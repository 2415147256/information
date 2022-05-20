package com.hd123.baas.sop.configuration;

import com.hd123.baas.config.core.BaasConfigClient;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.feign.DynamicFeignMgr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yanghaixiao
 */
@Component
@Slf4j
public class FeignClientMgr {
  @Autowired
  private BaasConfigClient client;
  @Autowired
  private DynamicFeignMgr dynamicFeignMgr;

  public <T> T getClient(String tenant, String shop, Class<T> clazz) throws BaasException {
    Class<?> configClass = dynamicFeignMgr.getClientConfigClass(clazz);
    Object config = client.getConfig(tenant, configClass, shop);
    try {
      return dynamicFeignMgr.getClient(clazz, config);
    } catch (BaasException e) {
      throw new BaasException(e);
    }
  }

}
