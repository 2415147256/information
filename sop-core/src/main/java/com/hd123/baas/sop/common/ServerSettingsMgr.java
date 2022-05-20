package com.hd123.baas.sop.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ServerSettingsMgr implements ApplicationContextAware {
  @Autowired
  private BaasConfigClient proxy;
  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /**
   * 配置: tenant, key=IScoreMgr, value=lhhsScoreMgr beanName的大小写敏感
   */
  public <T> T getCustomBean(String tenant, Class<T> clazz) {
    List<T> list = getCustomBeanList(tenant, clazz);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  /**
   * 配置: tenant, key=ISaleUploader,
   * value=defaultSaleScoreUploader,defaultSaleScoreUploader beanName的大小写敏感
   */
  public <T> List<T> getCustomBeanList(String tenant, Class<T> clazz) {
    return this.getCustomBeanList(tenant, clazz, ConfigItem.SPEC_DEF);
  }

  /**
   * 配置: tenant, key=IScoreMgr, value=lhhsScoreMgr beanName的大小写敏感
   */
  public <T> T getCustomBean(String tenant, Class<T> clazz, String spec) {
    List<T> list = getCustomBeanList(tenant, clazz, spec);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  /**
   * 配置: tenant, key=ISaleUploader,
   * value=defaultSaleScoreUploader,defaultSaleScoreUploader beanName的大小写敏感
   */
  public <T> List<T> getCustomBeanList(String tenant, Class<T> clazz, String spec) {
    String configBeanName = null;
    try {
      configBeanName = proxy.get("oas-pt", tenant, clazz.getSimpleName(), spec);
    } catch (Exception e) {
      log.error("getCustomBeanList error", e);
      throw new RuntimeException(e);
    }
    if (StringUtils.isBlank(configBeanName)) {
      log.warn("租户tenant:{} spec: {} 无配置clazz为{}的类", tenant, spec, clazz.getSimpleName());
      return new ArrayList<>();
    }
    List<T> beanList = new ArrayList<>();
    String[] configBeanNames = configBeanName.split(",");
    for (String configBean : configBeanNames) {
      beanList.add(applicationContext.getBean(configBean, clazz));
    }
    return beanList;
  }

  public <T> T getConfig(String tenant, String shop, Class<T> clazz) {
    if (shop != null) {
      return proxy.getConfig(tenant, clazz, shop);
    } else {
      return proxy.getConfig(tenant, clazz);
    }
  }

  public <T> T getConfig(String appId, String tenant, String shop, Class<T> clazz) {
    if (shop != null) {
      return proxy.getConfig(appId, tenant, clazz, shop);
    } else {
      return proxy.getConfig(appId, tenant, clazz);
    }
  }

  public String get(String appId, String tenant, String shop, String key) {
    try {
      if (shop != null) {
        return proxy.get(appId, tenant, key, shop);
      } else {
        return proxy.get(appId, tenant, key);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String get(String appId, String tenant, String shop, String key, boolean refreshIfNull) {
    try {
      if (shop != null) {
        return proxy.get(appId, tenant, key, shop, refreshIfNull);
      } else {
        return proxy.get(appId, tenant, key, refreshIfNull);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<ConfigItem> query(QueryDefinition qd) throws Exception {
    return proxy.query(qd).getRecords();
  }
}
