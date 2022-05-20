package com.hd123.baas.sop.utils;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.OrgConfig;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OrgUtils {

  @Autowired
  private BaasConfigClient configClient;

  public static BaasConfigClient client;

  @PostConstruct
  public void init() {
    client = configClient;
  }

  public static boolean isMasterId(String tenant, String orgId) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    OrgConfig config = client.getConfig(tenant, OrgConfig.class);
    // 判断是否是总部
    return orgId.equals(config.getMasterId());
  }

  /**
   * 判断数据权限是否为全部 全部数据权限需满足：组织id为总部，且总部数据配置为 all
   *
   * @param tenant
   *     租户
   * @param orgId
   *     组织id
   */
  public static boolean isAllScope(String tenant, String orgId) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    return false;
  }

  /**
   * 判断数据权限是否不是为全部 全部数据权限需满足：组织id为总部，且总部数据配置为 all
   *
   * @param tenant
   *          租户
   * @param orgId
   *          组织id
   * @return
   */
  public static boolean isNotAllScope(String tenant, String orgId) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    return !isAllScope(tenant, orgId);
  }
}
