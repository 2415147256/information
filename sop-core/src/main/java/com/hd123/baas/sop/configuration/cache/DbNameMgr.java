package com.hd123.baas.sop.configuration.cache;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("nameMgr")
public class DbNameMgr {

  @Autowired
  private BaasConfigClient client;

  public String getName(String tenant) {
    String stackDbName = null;
    try {
      stackDbName = client.get("sop-service", tenant, "dbPosition", true);
    } catch (Exception e) {
      throw new RuntimeException(MessageFormat.format("租户:{0}获取资源栈出错", tenant));
    }
    if (StringUtils.isBlank(stackDbName)) {
      throw new RuntimeException(MessageFormat.format("租户:{0}还未分配资源栈", tenant));
    }
    return stackDbName.split(":")[1];
  }

}
