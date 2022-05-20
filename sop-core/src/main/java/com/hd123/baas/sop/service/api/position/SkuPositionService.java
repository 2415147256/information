package com.hd123.baas.sop.service.api.position;

import java.util.List;

import com.hd123.baas.sop.service.api.entity.SkuPosition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface SkuPositionService {

  void saveNew(String tenant, SkuPosition skuPosition) throws BaasException;

  void batchSaveNew(String tenant, List<SkuPosition> skuPositions) throws BaasException;

  void saveModify(String tenant, SkuPosition skuPosition) throws BaasException;

  void batchDelete(String tenant, List<Integer> uuids) throws BaasException;

  List<SkuPosition> list(String tenant);

  List<SkuPosition> list(String tenant, String orgId);

  QueryResult<SkuPosition> query(String tenant, QueryDefinition qd);
}
