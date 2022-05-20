package com.hd123.baas.sop.service.impl.price.tempshoppriceAdjustment;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceManager;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceManagerService;
import com.hd123.baas.sop.service.dao.price.tempshopadjustment.TempShopPriceManagerDaoBof;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@Service
public class TempShopPriceManagerServiceImpl implements TempShopPriceManagerService {
  @Autowired
  private TempShopPriceManagerDaoBof tempShopPriceManagerDao;

  @Override
  @Tx
  public void batchSave(String tenant, String shop, Date effectiveDate, Collection<TempShopPriceManager> managers) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(managers)) {
      return;
    }
    List<String> skuIds = new ArrayList<>();
    managers.forEach(i -> {
      skuIds.add(i.getSkuId());
    });
    // 查询需要更新的
    // 先删后加
    tempShopPriceManagerDao.delete(tenant, shop, effectiveDate, skuIds);
    tempShopPriceManagerDao.batchInsert(tenant, managers);
  }

  @Override
  public QueryResult<TempShopPriceManager> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    return tempShopPriceManagerDao.query(tenant, qd);
  }

  @Override
  public void deleteBefore(String tenant, String orgId, Date executeDate) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(executeDate, "executeDate");
    tempShopPriceManagerDao.deleteBeforeDate(tenant, orgId, executeDate);
  }
}
