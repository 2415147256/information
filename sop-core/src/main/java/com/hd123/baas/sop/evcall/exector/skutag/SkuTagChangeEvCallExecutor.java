package com.hd123.baas.sop.evcall.exector.skutag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.skutag.SkuTagSummary;
import com.hd123.baas.sop.service.dao.skutag.ShopTagDaoBof;
import com.hd123.baas.sop.service.dao.skutag.SkuTagSummaryDaoBof;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.RedisDistributedLocker;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class SkuTagChangeEvCallExecutor extends AbstractEvCallExecutor<SkuTagChangeMsg> {

  public static final String SKU_TAG_CHANGE_EXECUTOR_ID = SkuTagChangeEvCallExecutor.class.getSimpleName();

  @Autowired
  private RedisDistributedLocker redisDistributedLocker;
  @Autowired
  private ShopTagDaoBof shopTagDao;
  @Autowired
  private SkuTagSummaryDaoBof skuTagSummaryDao;

  @Override
  @Tx
  protected void doExecute(SkuTagChangeMsg message, EvCallExecutionContext context) throws Exception {

    Assert.notNull(message.getTenant(), "tenant");
    Assert.notNull(message.getOrgId(), "orgId");
    Assert.notNull(message.getSkuId(), "skuId");
    String tenant = message.getTenant();
    String orgId = message.getOrgId();
    String skuId = message.getSkuId();
    String key = tenant + orgId + skuId;
    String lockId = redisDistributedLocker.lock(key, RedisDistributedLocker.LockPolicy.exception);
    try {
      int count = shopTagDao.countShopNum(tenant, orgId, skuId);
      SkuTagSummary ever = skuTagSummaryDao.get(tenant, orgId, skuId);
      if (ever == null) {
        SkuTagSummary summary = new SkuTagSummary();
        summary.setTenant(tenant);
        summary.setOrgId(orgId);
        summary.setSkuId(skuId);
        summary.setShopNum(count);
        skuTagSummaryDao.insert(tenant, summary);
      } else {
        ever.setShopNum(count);
        skuTagSummaryDao.update(tenant, ever);
      }
    } catch (Exception e) {
      log.error("SkuTagChangeEvCallExecutor错误", e);
      throw e;
    } finally {
      redisDistributedLocker.unlock(key, lockId);
    }
  }

  @Override
  protected SkuTagChangeMsg decodeMessage(String msg) throws BaasException {
    log.info("SkuTagChangeMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, SkuTagChangeMsg.class);
  }

}
