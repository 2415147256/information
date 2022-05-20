package com.hd123.baas.sop.service.impl.price.shopprice;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.api.price.shopprice.ShopPriceGradeManagerService;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceGradeManager;
import com.hd123.baas.sop.service.dao.price.shopprice.ShopPriceGradeManagerDaoBof;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author zhengzewang on 2020/11/19.
 */
@Service
public class ShopPriceGradeManagerServiceImpl implements ShopPriceGradeManagerService {

  @Autowired
  private ShopPriceGradeManagerDaoBof dao;

  @Override
  public void batchInsert(String tenant, Collection<ShopPriceGradeManager> managers) {
    Assert.hasText(tenant, "tenant");
    dao.batchInsert(tenant, managers);
  }

  @Override
  public QueryResult<ShopPriceGradeManager> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    return dao.query(tenant, qd);
  }

  @Override
  public void batchDelete(String tenant, Collection<String> uuids) {
    Assert.hasText(tenant, "tenant");
    dao.batchDelete(tenant, uuids);
  }
}
