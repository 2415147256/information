package com.hd123.baas.sop.service.impl.price.shopprice;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.price.shopprice.ShopPriceManagerService;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceManager;
import com.hd123.baas.sop.service.dao.price.shopprice.ShopPriceManagerDaoBof;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.spms.commons.util.CollectionUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/19.
 */
@Slf4j
@Service
public class ShopPriceManagerServiceImpl implements ShopPriceManagerService {

  @Autowired
  private ShopPriceManagerDaoBof dao;

  @Override
  public QueryResult<ShopPriceManager> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    return dao.query(tenant, qd);
  }

  @Override
  public List<ShopPriceManager> list(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    return dao.list(tenant, qd);
  }

  @Override
  public List<ShopPriceManager> queryLatest(String tenant, String shop, Date executeDate, List<String> skuIds) {

    if (CollectionUtils.isEmpty(skuIds)) {
      return new ArrayList();
    }
    // 查询需要更新的
    // 先删后加
    List<ShopPriceManager> priceManagerList = dao.list(tenant, shop, executeDate, skuIds);
    if (CollectionUtils.isEmpty(priceManagerList)) {
      executeDate = DateUtils.addDays(executeDate, -1);
      priceManagerList = dao.list(tenant, shop, executeDate, skuIds);
    }
    return priceManagerList;
  }

  @Override
  public boolean isExecute(String tenant, String shop, Date executeDate) {
    QueryDefinition qd = new QueryDefinition();
    qd.setPage(0);
    qd.setPageSize(1);
    qd.addByField(ShopPriceManager.Queries.SHOP, Cop.EQUALS, shop);
    qd.addByField(ShopPriceManager.Queries.EFFECTIVE_DATE, Cop.EQUALS, executeDate);
    return dao.queryCount(tenant, qd) > 0;
  }

  @Override
  @Tx
  public void batchSave(String tenant, String shop, Date effectiveDate, Collection<ShopPriceManager> managers) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(managers)) {
      return;
    }
    log.info("batchSave start，size={}", managers.size());
    List<String> skuIds = new ArrayList<>();
    managers.forEach(i -> {
      skuIds.add(i.getSku().getId());
    });
    // 查询需要更新的
    // 先删后加
    dao.remove(tenant, shop, effectiveDate, skuIds);
    log.info("batchSave remove end");
    dao.batchInsert(tenant, managers);
    log.info("batchSave end");
  }

  @Override
  public void clearBeforeDate(String tenant, String orgId, Date date) {
    dao.clearBeforeDate(tenant, orgId, date);
  }

  @Override
  public List<ShopPriceManager> listByGoodsIds(String tenant, String shop, Date executeDate, List<String> goodsIds)
      throws BaasException {
    if (CollectionUtils.isEmpty(goodsIds)) {
      return new ArrayList();
    }
    if (goodsIds.size() > 1000) {
      throw new BaasException("查询条目过多，最大条目=1000");
    }

    List<ShopPriceManager> result = new ArrayList<>();
    for (List<String> list : CollectionUtil.sizeBy(goodsIds, 1000)) {
      result.addAll(dao.listByGoodsIds(tenant, shop, executeDate, goodsIds));
    }
    // 查询需要更新的
    return result;
  }
}
