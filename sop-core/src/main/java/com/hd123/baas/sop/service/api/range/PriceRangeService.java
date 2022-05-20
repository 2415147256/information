package com.hd123.baas.sop.service.api.range;

import java.util.List;

import com.hd123.baas.sop.service.api.entity.PriceRange;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface PriceRangeService {

  void saveNew(String tenant, PriceRange priceRange) throws BaasException;

  void batchSaveNew(String tenant, List<PriceRange> priceRanges) throws BaasException;

  void saveModify(String tenant, PriceRange priceRange) throws BaasException;

  void batchDelete(String tenant, List<Integer> uuids);

  /**
   * dq 查询
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<PriceRange> query(String tenant, QueryDefinition qd);

  List<PriceRange> list(String tenant,String orgId) throws BaasException;

  List<PriceRange> list(String tenant) throws BaasException;
}
