package com.hd123.baas.sop.service.api.price.config;

import java.util.Collection;
import java.util.List;

import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseRule;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/10.
 */
public interface PriceSkuConfigService {

  /**
   * 批量保存。有则修改，无责新增。
   *
   * @param tenant
   *     租户
   * @param configs
   *     商品基本设置
   * @param operateInfo
   *     操作人信息
   */
  void batchSave(String tenant, String orgId, Collection<PriceSkuConfig> configs, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 批量修改商品定位
   *
   * @param tenant
   *     租户
   * @param skuIds
   *     商品id集合
   * @param skuPosition
   *     商品定位
   * @param skuPositionGradeId
   *     商品定位
   * @param operateInfo
   *     操作人信息
   */
  void batchEditPosition(String tenant, String orgId, Collection<String> skuIds, String skuPosition, String skuPositionGradeId, OperateInfo operateInfo) throws BaasException;

  /**
   * 批量修改基础参数
   *
   * @param tenant
   *     租户
   * @param skuIds
   *     商品id集合
   * @param param
   *     参数 param中的参数有值则更新，空值则表示不更新
   * @param operateInfo
   *     操作人
   */
  void batchEditBaseParam(String tenant, String orgId, Collection<String> skuIds, BaseConfigParam param,
      OperateInfo operateInfo)
      throws BaasException;

  /**
   * 新建或更新。根据商品id
   *
   * @param tenant
   *     租户
   * @param config
   *     商品基本设置
   * @param operateInfo
   *     操作人信息
   */
  void save(String tenant, PriceSkuConfig config, OperateInfo operateInfo) throws BaasException;

  /**
   * 列表分页查询
   *
   * @param tenant
   *     租户
   * @param qd
   *     查询条件
   */
  QueryResult<PriceSkuConfig> query(String tenant, String orgId, SkuFilter qd, String... fetchParts) throws BaasException;

  /**
   * 列表分页查询
   *
   * @param tenant
   *     租户
   * @param qd
   *     查询条件
   */
  QueryResult<PriceSkuConfig> query(String tenant, SkuFilter qd, String... fetchParts)
      throws BaasException;

  /**
   * 商品列表分页查询
   *
   * @param tenant
   *     租户
   * @param qd
   *     查询条件
   */
  QueryResult<PriceSku> querySku(String tenant, QueryDefinition qd);

  /**
   * 根据商品id查询
   *
   * @param tenant
   *     租户
   * @param skuId
   *     商品id
   * @return 商品基本配置
   */
  PriceSkuConfig getBySkuId(String tenant, String orgId, String skuId);

  /**
   * 查询商品配置
   */
  QueryResult<PriceSkuConfig> querySkuConfig(String tenant, QueryDefinition qd);

  /**
   * 批量修改规则
   */
  void batchModifyRule(String tenant, String orgId, Collection<String> skuIds, PriceIncreaseType increaseType,
      List<PriceIncreaseRule> increaseRules, OperateInfo operateInfo) throws BaasException;

  /**
   * 查询商品集合
   */
  List<PriceSku> getPriceSkusByCategoryIds(String tenant, String orgId, List<String> categoryIds) throws BaasException;

}
