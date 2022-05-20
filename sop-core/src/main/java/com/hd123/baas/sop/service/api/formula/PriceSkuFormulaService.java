package com.hd123.baas.sop.service.api.formula;

import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
public interface PriceSkuFormulaService {
  /**
   * 批量新增商品到店价公式 先删除后新增
   * 
   * @param tenant
   *          zu
   * @param priceSkuFormulaList
   */
  void batchSaveNew(String tenant, String orgId, List<PriceSkuFormula> priceSkuFormulaList);

  /**
   * 根据skuId获取公式
   * 
   * @param tenant
   * @param skuId
   * @return
   */
  PriceSkuFormula getBySkuId(String tenant, String orgId, String skuId);

  /**
   * 根据公式右边的商品查询公式
   * @param tenant
   * @param dependOnSkuId
   * @return
   */
  List<PriceSkuFormula> getByDependOnSkuId(String tenant, String orgId, String dependOnSkuId);

  /**
   * 删除公式
   * 
   * @param tenant
   * @param uuid
   *          公式主键
   */
  void delete(String tenant, String uuid);

  /**
   * 分页查询公式
   * 
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<PriceSkuFormula> query(String tenant, QueryDefinition qd);
}
