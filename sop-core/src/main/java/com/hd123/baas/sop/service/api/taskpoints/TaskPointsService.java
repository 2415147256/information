package com.hd123.baas.sop.service.api.taskpoints;

import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * 任务积分
 */
public interface TaskPointsService {
  /**
   * 任务积分列表查询
   *
   * @param tenant
   *          租户
   * @param qd
   *          查询条件
   * @return 任务积分列表
   */
  QueryResult<TaskPoints> query(String tenant, QueryDefinition qd);

  /**
   * 保存任务积分
   *
   * @param tenant
   *          租户
   * @param taskPoints
   *          任务积分对象
   * @return 任务积分ID
   */
  String saveNew(String tenant, TaskPoints taskPoints) throws BaasException;
}
