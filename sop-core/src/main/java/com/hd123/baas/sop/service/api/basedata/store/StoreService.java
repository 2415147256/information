package com.hd123.baas.sop.service.api.basedata.store;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author Silent
 **/
public interface StoreService {

  QueryResult<Store> query(String tenant, StoreFilter filter) throws BaasException;

  Store get(String tenant, String orgType, String orgId, String id, String userId) throws BaasException;

  String update(String tenant, String orgType, String orgId, String id, YcShopUpdate update, String operator) throws BaasException;
}
