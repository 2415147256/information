package com.hd123.baas.sop.service.api.skutag;


import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

public interface ShopSkuPriceRangeService {

  /**
   * 保存价格带
   *
   * @param tenant
   *     租户
   * @param priceRange
   *     价格带
   * @param operateInfo
   *     操作信息
   * @return 价格带Id
   */
  String saveNew(String tenant, ShopSkuPriceRange priceRange, OperateInfo operateInfo);

  /**
   * 修改价格带
   *
   * @param tenant
   *     租户
   * @param priceRange
   *     价格带
   * @param operateInfo
   *     操作信息
   */
  void saveModify(String tenant, ShopSkuPriceRange priceRange, OperateInfo operateInfo) throws BaasException;

  /**
   * 逻辑删除价格
   *
   * @param tenant
   *     租户
   * @param uuid
   *     价格带Id
   * @param operateInfo
   *     操作信息
   */
  void delete(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 查询价格带详情
   *
   * @param tenant
   *     租户
   * @param uuid
   *     价格带Id
   * @return 价格带详情
   */
  ShopSkuPriceRange get(String tenant, String uuid) throws BaasException;

  /**
   * 查询价格带列表
   *
   * @return 价格带列表
   */
  List<ShopSkuPriceRange> listBySkuIds(String tenant, String shopId, List<String> skuIds);

  /**
   * 查询价格带列表
   *
   * @param tenant
   *     租户
   * @param qd
   *     查询条件
   * @param fetchParts
   *     级联查询
   * @return 价格带列表
   */
  QueryResult<ShopSkuPriceRange> query(String tenant, QueryDefinition qd, String... fetchParts);

  /**
   * 批量新增价格带
   *
   * @param tenant
   *     租户
   * @param priceRanges
   *     价格带集合
   * @param operateInfo
   *     操作信息
   */
  void batchSave(String tenant, List<ShopSkuPriceRange> priceRanges, OperateInfo operateInfo);
}
