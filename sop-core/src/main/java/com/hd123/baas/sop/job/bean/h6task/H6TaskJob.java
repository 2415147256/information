package com.hd123.baas.sop.job.bean.h6task;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.evcall.exector.goodsprm.GoodsPrmPriceGeneralEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.goodsprm.GoodsPrmPriceGeneralEvent;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceTaskMsg;
import com.hd123.baas.sop.evcall.exector.skumgr.ShopSkuEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skumgr.ShopSkuTaskMsg;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.evcall.EvCallManager;
import com.hd123.spms.commons.json.JsonUtil;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@DisallowConcurrentExecution
public class H6TaskJob implements Job {

  @Autowired
  private EvCallManager evCallManager;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private BaasConfigClient client;
  @Value("${baas-config.currentAppId:${spring.application.name}}")
  private String currentAppId;

  @Override
  @Tx
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("计算门店售价的job");
    Set<ConfigItem> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    for (ConfigItem tenantItem : tenants) {
      log.info("准备执行计算门店售价的job，租户ID={}，组织ID={}", tenantItem.getTenant(), tenantItem.getSpec());
      publishShopPriceEvCall(tenantItem);
      publishPrmPriceEvCall(tenantItem);
      publishShopSkuEvCall(tenantItem);
    }
  }

  /**
   * 门店商品价格计算
   *
   * @param tenantItem
   *     租户
   */
  private void publishShopPriceEvCall(ConfigItem tenantItem) {
    ShopPriceTaskMsg msg = new ShopPriceTaskMsg();
    msg.setTenant(tenantItem.getTenant());
    msg.setOrgId(tenantItem.getSpec());
    msg.setExecuteDate(DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE));
    publisher.publishForNormal(ShopPriceEvCallExecutor.SHOP_PRICE_CREATE_EXECUTOR_ID, msg);
  }

  private void publishPrmPriceEvCall(ConfigItem tenant) {
    GoodsPrmPriceGeneralEvent event = new GoodsPrmPriceGeneralEvent();
    event.setTenant(tenant.getTenant());
    event.setOrgId(tenant.getSpec());
    event.setExecuteDate(DateUtils.truncate(DateUtils.addDays(new Date(), 1), Calendar.DATE));
    evCallManager.submit(GoodsPrmPriceGeneralEvCallExecutor.BEAN_ID, JsonUtil.objectToJson(event));
  }

  private void publishShopSkuEvCall(ConfigItem tenant) {
    ShopSkuTaskMsg msg = new ShopSkuTaskMsg();
    msg.setTenant(tenant.getTenant());
    msg.setOrgId(tenant.getSpec());
    msg.setExecuteDate(new Date());
    evCallManager.submit(ShopSkuEvCallExecutor.SHOP_SKU_TASK_EXECUTOR_ID, JsonUtil.objectToJson(msg));
  }

  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  private Set<ConfigItem> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    /**
     * {@link com.hd123.baas.sop.config.H6TaskConfig}
     */
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, "h6Task.enabled");
    qd.addByField(ConfigItem.Queries.VALUE, Cop.EQUALS, "true");
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, currentAppId);
    QueryResult<ConfigItem> result = client.query(qd);
    if (CollectionUtils.isEmpty(result.getRecords())) {
      return new HashSet<>();
    }
    return new HashSet<>(result.getRecords());
  }

}
