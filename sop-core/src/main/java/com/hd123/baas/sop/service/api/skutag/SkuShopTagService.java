package com.hd123.baas.sop.service.api.skutag;

import java.util.List;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @Author maodapeng
 * @Since
 */
public interface SkuShopTagService {
  /**
   * 商品标签统计
   * 
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<SkuTagSummary> summary(String tenant, List<String> orgIds, QueryDefinition qd);

  /**
   * 获取标签统计详情
   * 
   * @param tenant
   * @param orgId
   * @param skuId
   * @return
   */
  SkuTagSummary get(String tenant, String orgId, String skuId) throws BaasException;

  /**
   * 新增门店标签
   * 
   * @param tenant
   * @param tag
   * @throws BaasException
   */
  void saveNew(String tenant, SkuShopTag tag, OperateInfo operateInfo) throws BaasException;

  /**
   * 批量新增门店标签
   *
   * @param tenant
   * @param tags
   * @throws BaasException
   */
  void batchSaveNew(String tenant, List<SkuShopTag> tags, OperateInfo operateInfo) throws BaasException;

  /**
   * 更新门店标签
   * 
   * @param tenant
   * @param tag
   * @throws BaasException
   */
  void update(String tenant, SkuShopTag tag, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除
   * 
   * @param tenant
   */
  void delete(String tenant, String orgId, String skuId, String shop) throws BaasException;

  /**
   * 查询商品标签
   * 
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<SkuShopTag> query(String tenant, String orgId, String SkuId, QueryDefinition qd);
}
