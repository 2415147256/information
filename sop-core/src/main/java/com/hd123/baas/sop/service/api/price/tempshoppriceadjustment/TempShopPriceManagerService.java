package com.hd123.baas.sop.service.api.price.tempshoppriceadjustment;

import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;

import java.util.Collection;
import java.util.Date;

/**
 * @Author maodapeng
 * @Since
 */
public interface TempShopPriceManagerService {
  /**
   * 批量插入
   *
   * @param tenant
   *          租户
   * @param managers
   *          门店价格池
   */
  void batchSave(String tenant, String shop, Date effectiveDate, Collection<TempShopPriceManager> managers);

  /**
   *
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<TempShopPriceManager> query(String tenant, QueryDefinition qd);

  /**
   * 清理之前得数据
   *
   * @param tenant
   * @param executeDate
   */
  void deleteBefore(String tenant, String orgId, Date executeDate);
}
