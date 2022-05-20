package com.hd123.baas.sop.jmzs.workflowrequest.api;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface WorkFlowRequestService {

  /**
   * 查询
   * @param tenant
   *        租户
   * @param qd
   *        查询条件
   * @return
   */
  QueryResult<WorkflowRequest> query(String tenant, QueryDefinition qd);

  /**
   * 获取
   *
   * @param tenant
   *        租户
   * @param uuid
   *        标识
   * @return
   */
  WorkflowRequest get(String tenant, String uuid);

  /**
   * 新建
   *
   * @param tenant
   *        租户
   * @param workFlow
   *        申请数据
   * @param operateInfo
   *        操作上下问
   * @return
   * @throws Exception
   */
  String saveNew(String tenant, WorkflowRequest workFlow, OperateInfo operateInfo) throws Exception;

  /**
   * 保存修改
   *
   * @param tenant
   *        租户
   * @param workFlow
   *        申请数据
   * @param operateInfo
   *        修改上下文
   * @return
   * @throws Exception
   */
  void saveModify(String tenant, WorkflowRequest workFlow, OperateInfo operateInfo) throws Exception;

  /**
   * 根据uuid 物理删除
   * @param tenant
   *        租户
   * @param uuid
   *        标识
   */
  void deleteByUuid(String tenant, String uuid) throws BaasException;

  /**
   * 申请提交
   *
   * @param tenant
   *        租户
   * @param uuid
   *        标识
   * @return
   */
  void submit(String tenant, String uuid, OperateInfo operateInfo) throws Exception;
}
