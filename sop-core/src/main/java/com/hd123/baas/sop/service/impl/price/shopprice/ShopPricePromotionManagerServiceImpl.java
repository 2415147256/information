package com.hd123.baas.sop.service.impl.price.shopprice;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.api.price.shopprice.ShopPricePromotionManagerService;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPricePromotionManager;
import com.hd123.baas.sop.service.dao.price.shopprice.ShopPricePromotionManagerDaoBof;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author zhengzewang on 2020/11/19.
 */
@Service
public class ShopPricePromotionManagerServiceImpl implements ShopPricePromotionManagerService {

  @Autowired
  private ShopPricePromotionManagerDaoBof dao;

  @Override
  public void batchInsert(String tenant, Collection<ShopPricePromotionManager> managers) {
    Assert.hasText(tenant, "tenant");
    dao.batchInsert(tenant, managers);
  }

  @Override
  public QueryResult<ShopPricePromotionManager> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    return dao.query(tenant, qd);
  }

  @Override
  public void batchDelete(String tenant, Collection<String> uuids) {
    Assert.hasText(tenant, "tenant");
    dao.batchDelete(tenant, uuids);
  }

  @Override
  public void deleteBeforeDate(String tenant,String orgId, Date date) {
    Assert.hasText(tenant, "tenant");
    dao.deleteBeforeDate(tenant,orgId, date);
  }

  @Override
  public void deleteBySource(String tenant, String source) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(source, "source");
    dao.deleteBySource(tenant, source);
  }
}
