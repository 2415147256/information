package com.hd123.baas.sop.service.api.price.shopprice;

import java.util.Collection;

import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceGradeManager;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;

/**
 * @author zhengzewang on 2020/11/19.
 */
public interface ShopPriceGradeManagerService {

  /**
   * 批量插入
   * 
   * @param tenant
   *          租户
   * @param managers
   *          门店价格增量数据（默认保留两天）
   */
  void batchInsert(String tenant, Collection<ShopPriceGradeManager> managers);

  /**
   * 分页查询
   * 
   * @param tenant
   *          租户
   * @param qd
   *          查询条件
   * @return 查询结果
   */
  QueryResult<ShopPriceGradeManager> query(String tenant, QueryDefinition qd);

  /**
   * 批量删除
   * 
   * @param tenant
   *          租户
   * @param uuids
   *          uuids
   */
  void batchDelete(String tenant, Collection<String> uuids);

}
