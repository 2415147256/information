package com.hd123.baas.sop.configuration.cache.listener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.event.ConfigChangeEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanghaixiao
 **/
@Slf4j
@Component
public class ClearCacheListener implements ApplicationListener<ConfigChangeEvent> {
  @Autowired
  private CacheManager cacheManager;
  @Value("${spring.application.name:}")
  protected String stack;

  @Override
  public void onApplicationEvent(ConfigChangeEvent configRefreshEvent) {
    log.info("{}收到配置中心刷新事件,开始刷新本地应用缓存", stack);
    cacheManager.getCacheNames().forEach(r -> cacheManager.getCache(r).clear());
    log.info("{}完成刷新本地应用缓存{}", stack, StringUtils.join(cacheManager.getCacheNames(), "、"));
  }
}
