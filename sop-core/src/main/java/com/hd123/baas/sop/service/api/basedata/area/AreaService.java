package com.hd123.baas.sop.service.api.basedata.area;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface AreaService {

  QueryResult<Area> query(String tenant, AreaFilter filter) throws BaasException;

  /**
   * 查询通过mas服务查询区域信息
   *
   * @param tenant
   *         租户
   * @param filter
   *         查询条件
   * @return 区域信息
   * @throws BaasException
   */
  QueryResult<Area> queryByMas(String tenant, AreaFilter filter) throws BaasException;
}
