package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.access.AccessService;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.remote.rsmas.RsMasResponse;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Silent
 **/
@Service
public class AccessServiceImpl extends BaseServiceImpl implements AccessService {

  @Override
  public QueryResult<Store> query(String tenant, String orgType, String orgId, String userId) throws BaasException {
    Assert.hasText(tenant);
    Assert.notNull(userId);


    RsMasResponse<List<RsStore>> rsMasResponse = getClient().accessQuery(tenant, orgType, orgId, userId);
    BaasResponse<List<RsStore>> response = covertBaasResponse(rsMasResponse);
    response.setData(rsMasResponse.getData());
    List<Store> storeList = convertStore(tenant, response.getData());

    QueryResult<Store> result = new QueryResult<Store>();
    result.setRecords(storeList);
    result.setRecordCount(storeList.size());
    return result;
  }
}
