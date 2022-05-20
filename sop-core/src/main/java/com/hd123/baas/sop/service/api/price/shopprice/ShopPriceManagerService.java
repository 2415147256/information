package com.hd123.baas.sop.service.api.price.shopprice;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceManager;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/19.
 */
public interface ShopPriceManagerService {

  /**
   * 分页查询
   * 
   * @param tenant
   *          租户
   * @param qd
   *          查询条件
   * @return 查询结果
   */
  QueryResult<ShopPriceManager> query(String tenant, QueryDefinition qd);

  /**
   * 分页查询
   *
   * @param tenant
   *          租户
   * @param qd
   *          查询条件
   * @return 查询结果
   */
  List<ShopPriceManager> list(String tenant, QueryDefinition qd);

  /**
   * 分页查询
   *
   * @param tenant
   *          租户
   * @return 查询结果
   */
  List<ShopPriceManager> queryLatest(String tenant, String shop, Date executeDate, List<String> skuIds);

  /**
   * 是否计算过
   * 
   * @param tenant
   *          租户
   * @param shop
   *          门店
   * @param executeDate
   *          日期
   * @return 结果
   */
  boolean isExecute(String tenant, String shop, Date executeDate);

  /**
   * 批量插入
   * 
   * @param tenant
   *          租户
   * @param managers
   *          门店价格池
   */
  void batchSave(String tenant, String shop, Date effectiveDate, Collection<ShopPriceManager> managers);

  /**
   * 删除过期数据
   * 
   * @param tenant
   *          租户
   * @param date
   *          日期
   */
  void clearBeforeDate(String tenant,String orgId, Date date);

  /**
   * 分页查询
   *
   * @param tenant
   *          租户
   * @return 查询结果
   */
  List<ShopPriceManager> listByGoodsIds(String tenant, String shop, Date executeDate, List<String> goodsIds)
      throws BaasException;

}
