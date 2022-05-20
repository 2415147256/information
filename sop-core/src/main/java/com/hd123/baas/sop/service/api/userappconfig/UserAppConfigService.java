package com.hd123.baas.sop.service.api.userappconfig;

public interface UserAppConfigService {
  /**
   * 保存首页应用
   *
   * @param tenant
   * @param userAppConfig
   */
  void put(String tenant, UserAppConfig userAppConfig);

  /**
   * 获取首页应用
   *
   * @param tenant
   * @param appId
   * @param id
   * @return
   */
  UserAppConfig get(String tenant, String appId, String id);
}
