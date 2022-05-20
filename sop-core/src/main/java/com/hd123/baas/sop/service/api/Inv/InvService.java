package com.hd123.baas.sop.service.api.Inv;

import com.hd123.baas.sop.remote.rsh6sop.inv.AvailableInv;
import com.hd123.baas.sop.remote.rsh6sop.store.SimpleStore;
import com.hd123.baas.sop.remote.rsh6sop.wrh.SimpleWarehouse;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.QueryRequest;

/**
 * @author liuhaoxin
 * @date on 2021/11/29
 */
public interface InvService {
  /**
   * 库存查询
   *
   * @param tenant
   *          租户
   * @param orgId
   *          组织id
   * @param req
   *          请求信息
   * @return QueryResult<AvailableInv> 库存信息列表
   * @throws BaasException
   *           库存查询异常
   */
  QueryResult<AvailableInv> invQuery(String tenant, String orgId, QueryRequest req) throws BaasException;

  /**
   * 仓位信息
   *
   * @param tenant
   *          租户
   * @param orgId
   *          组织id
   * @param req
   *          仓位请求信息
   * @return QueryResult<SimpleWarehouse> 仓位信息
   */
  QueryResult<SimpleWarehouse> wrhQuery(String tenant, String orgId, QueryRequest req) throws BaasException;

  /**
   * 组织/门店查询
   *
   * @param tenant
   *          租户
   * @param orgId
   *          组织id
   * @param type
   *          查询类型
   * @return QueryResult<SimpleWarehouse> 仓位信息
   */
  QueryResult<SimpleStore> storeQuery(String tenant, String orgId, StoreType type)
      throws BaasException;
}
