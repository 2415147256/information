package com.hd123.baas.sop.service.api.basedata.pos;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author Silent
 **/
public interface PosService {

  QueryResult<Pos> query(String tenant, PosFilter filter) throws
    BaasException;

  Pos get(String tenantId,String orgType, String orgId, String id) throws BaasException;
}
