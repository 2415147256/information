package com.hd123.baas.sop.service.api.basedata.sku;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @author lina
 */
public interface SkuService {

  String insert(String tenant,String orgType, String orgId, Sku sku, String operator) throws BaasException;

  List<String> batchInit(String tenant,String orgType, String orgId, List<Sku> sku, String operator) throws BaasException;

  void batchInitBom(String tenant, String orgType, String orgId,SkuBomCreationList creationList, String operator)
      throws BaasException;

  String update(String tenant,String orgType, String orgId, Sku sku, String operator) throws BaasException;

  void delete(String tenant,String orgType, String orgId, String uuid, String operator) throws BaasException;

  void deleteByGoodsIdList(String tenant,String orgType, String orgId, GoodsIdList goodsIdList, String operator)
      throws BaasException;

  void deleteByIds(String tenant,String orgType, String orgId, List<String> ids, String operator)
          throws BaasException;

  Sku get(String tenant,String orgType, String orgId, String id) throws BaasException;

  QueryResult<Sku> query(String tenant, SkuFilter filter) throws BaasException;

}
