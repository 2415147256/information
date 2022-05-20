package com.hd123.baas.sop.service.impl.userappconfig;

import com.hd123.baas.sop.service.api.userappconfig.UserAppConfig;
import com.hd123.baas.sop.service.api.userappconfig.UserAppConfigService;
import com.hd123.baas.sop.service.dao.userappconfig.UserAppConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.hd123.rumba.commons.lang.Assert;

@Service
public class UserAppConfigServiceImpl implements UserAppConfigService {

  @Autowired
  private UserAppConfigDao userAppConfigDao;

  @Override
  public void put(String tenant, UserAppConfig userAppConfig) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(userAppConfig, "userAppConfig");
    UserAppConfig history = get(tenant, userAppConfig.getAppId(), userAppConfig.getId());
    if (history == null) {
      userAppConfigDao.insert(tenant, userAppConfig);
    } else {
      history.setExt(userAppConfig.getExt());
      userAppConfigDao.updateExt(tenant, history);
    }
  }

  @Override
  public UserAppConfig get(String tenant, String appId, String id) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(appId, "appId");
    Assert.hasText(id, "id");
    return userAppConfigDao.getById(tenant, appId, id);
  }
}
