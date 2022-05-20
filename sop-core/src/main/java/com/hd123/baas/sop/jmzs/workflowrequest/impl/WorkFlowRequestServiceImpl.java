package com.hd123.baas.sop.jmzs.workflowrequest.impl;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkFlowRequestService;
import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkFlowRequestState;
import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkflowRequest;
import com.hd123.baas.sop.jmzs.workflowrequest.dao.WorkFlowRequestDao;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkFlowRequestServiceImpl implements WorkFlowRequestService {

  @Autowired
  private WorkFlowRequestDao dao;

  @Override
  public QueryResult<WorkflowRequest> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant);
    Assert.notNull(qd);

    return dao.query(tenant, qd);
  }

  @Override
  public WorkflowRequest get(String tenant, String uuid) {
    Assert.hasText(tenant);
    Assert.hasText(uuid);

    return dao.get(tenant, uuid);
  }

  @Tx
  @Override
  public String saveNew(String tenant, WorkflowRequest workFlow, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.notNull(workFlow);
    Assert.notNull(operateInfo);

    workFlow.setTenant(tenant);
    workFlow.setState(WorkFlowRequestState.INIT);
    buildForSaveNew(workFlow, operateInfo);

    dao.saveNew(workFlow);
    return workFlow.getUuid();
  }

  @Tx
  @Override
  public void saveModify(String tenant, WorkflowRequest workFlow, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.notNull(workFlow);
    Assert.notNull(operateInfo);

    WorkflowRequest record = dao.get(tenant, workFlow.getUuid(), true);

    if (record == null) {
      throw new BaasException("编辑失败,找不到数据{}", workFlow.getUuid());
    }

    if (!record.getState().equals(WorkFlowRequestState.INIT)) {
      throw new BaasException("当前状态不允许编辑");
    }

    convert(workFlow, record);
    buildForUpdate(record, operateInfo);
    dao.update(record);
  }

  private void convert(WorkflowRequest workFlow, WorkflowRequest record) {
    record.setAttachment(workFlow.getAttachment());
    record.setContent(workFlow.getContent());
    record.setTitle(workFlow.getTitle());
    record.setType(workFlow.getType());
    record.setTarget(workFlow.getTarget());
    record.setTargetName(workFlow.getTargetName());
    record.setTargetId(workFlow.getTargetId());
    record.setTargetCode(workFlow.getTargetCode());
    record.setHolder(workFlow.getHolder());
    record.setHolderId(workFlow.getHolderId());
    record.setHolderCode(workFlow.getHolderCode());
    record.setHolderName(workFlow.getHolderName());
  }

  @Override
  @Tx
  public void deleteByUuid(String tenant, String uuid) throws BaasException {
    Assert.hasText(tenant);
    Assert.hasText(uuid);

    WorkflowRequest flowRequest = dao.get(tenant, uuid, true);
    if (flowRequest == null) {
      throw new BaasException("删除失败,找不到{}数据", uuid);
    }
    if (flowRequest.getState() != WorkFlowRequestState.INIT) {
      throw new BaasException("该申请数据已提交,不允许删除");
    }

    dao.delete(tenant, uuid);
  }

  @Override
  @Tx
  public void submit(String tenant, String uuid, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.hasText(uuid);
    Assert.notNull(operateInfo);

    WorkflowRequest workFlowRequest = dao.get(tenant, uuid, true);
    if (workFlowRequest == null) {
      throw new BaasException("提交申请失败,找不到{}申请数据", uuid);
    }

    if (workFlowRequest.getState() != WorkFlowRequestState.INIT) {
      throw new BaasException("提交申请错误,状态{}", workFlowRequest.getState());
    }

    workFlowRequest.setState(WorkFlowRequestState.SUBMITTED);
    buildForUpdate(workFlowRequest, operateInfo);
    dao.update(workFlowRequest);
  }

  private void buildForUpdate(StandardEntity entity, OperateInfo operateInfo) {
    entity.setVersion(entity.getVersion() + 1);
    entity.setLastModifyInfo(operateInfo);
  }

  private void buildForSaveNew(StandardEntity entity, OperateInfo operateInfo) {
    entity.setVersion(0);
    entity.setCreateInfo(operateInfo);
    entity.setLastModifyInfo(operateInfo);
  }
}
