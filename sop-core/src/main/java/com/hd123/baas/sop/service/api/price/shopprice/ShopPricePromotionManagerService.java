package com.hd123.baas.sop.service.api.price.shopprice;

import java.util.Collection;
import java.util.Date;

import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPricePromotionManager;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;

/**
 * @author zhengzewang on 2020/11/19.
 */
public interface ShopPricePromotionManagerService {

  /**
   * 批量插入
   * 
   * @param tenant
   *          租户
   * @param managers
   *          数据
   */
  void batchInsert(String tenant, Collection<ShopPricePromotionManager> managers);

  /**
   * 分页查询
   *
   * @param tenant
   *          租户
   * @param qd
   *          查询条件
   * @return 查询结果
   */
  QueryResult<ShopPricePromotionManager> query(String tenant, QueryDefinition qd);

  /**
   * 批量删除
   * 
   * @param tenant
   *          租户
   * @param uuids
   *          uuids
   */
  void batchDelete(String tenant, Collection<String> uuids);

  /**
   * 根据生效结束时间删除
   * 
   * @param tenant
   *          租户
   * @param date
   *          日期
   */
  void deleteBeforeDate(String tenant,String orgId, Date date);

  /**
   * 按照原单来删除
   * @param tenant
   * @param source
   */
  void deleteBySource(String tenant,String source);

}
