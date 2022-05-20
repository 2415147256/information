package com.hd123.baas.sop.service.api.advertorial;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface AdvertorialService {
  Advertorial saveNew(String tenant, Advertorial advertorial, OperateInfo operateInfo);

  Advertorial saveModify(Advertorial advertorial, OperateInfo operateInfo) throws BaasException;

  void remove(String tenant, String uuid);

  QueryResult<Advertorial> query(String tenant, QueryDefinition qd);

  Advertorial get(String tenant, String uuid);

}
