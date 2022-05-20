package com.hd123.baas.sop.service.api.basedata.access;

import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface AccessService {

  QueryResult<Store> query(String tenant, String orgType, String orgId, String userId) throws BaasException;

}
