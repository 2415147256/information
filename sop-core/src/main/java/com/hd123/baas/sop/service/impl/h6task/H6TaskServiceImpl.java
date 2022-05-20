package com.hd123.baas.sop.service.impl.h6task;

import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.annotation.NoTx;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.service.dao.h6task.H6TaskDaoBof;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.h6task.H6TaskDeliveredEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.h6task.H6TaskDeliveredMsg;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Service
public class H6TaskServiceImpl implements H6TaskService {

  @Autowired
  private H6TaskDaoBof dao;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private BillNumberMgr billNumberMgr;

  @Override
  @Tx
  public String init(String tenant, H6Task h6Task, OperateInfo operateInfo) throws BaasException {
    h6Task.setState(H6TaskState.INIT);
    h6Task.setFlowNo(billNumberMgr.generateH6TaskFlowNo(tenant));
    return dao.insert(tenant, h6Task, operateInfo);
  }

  @Override
  public String saveNew(String tenant, H6Task h6Task, OperateInfo operateInfo) throws BaasException {
    h6Task.setState(H6TaskState.CONFIRMED);
    h6Task.setFlowNo(billNumberMgr.generateH6TaskFlowNo(tenant));
    return dao.insert(tenant, h6Task, operateInfo);
  }

  @Override
  public H6Task get(String tenant, String uuid) {
    return dao.get(tenant, uuid);
  }

  @Override
  public H6Task getWithLock(String tenant, String uuid) {
    return dao.getWithLock(tenant, uuid);
  }

  @Override
  public List<H6Task> getByDate(String tenant,String orgId, H6TaskType type, Date executeDate) {
    return dao.getByDate(tenant,orgId, type, executeDate);
  }

  @Override
  @Tx
  public void updateState(String tenant, String uuid, H6TaskState state, OperateInfo operateInfo) throws BaasException {
    H6Task h6Task = this.get(tenant, uuid);
    if (h6Task == null) {
      throw new BaasException("任务不存在");
    }
    h6Task.setState(state);
    h6Task.setOccurredTime(operateInfo.getTime());
    if (H6TaskState.FINISHED.equals(state)) {
      h6Task.setErrMsg("");
    }
    dao.update(tenant, h6Task, operateInfo);
  }

  @Override
  @Tx
  public void fixUrl(String tenant, String uuid, String url, OperateInfo operateInfo) throws BaasException {
    H6Task h6Task = this.get(tenant, uuid);
    if (h6Task == null) {
      throw new BaasException("任务不存在");
    }
    h6Task.setState(H6TaskState.DELIVERED);
    h6Task.setFileUrl(url);
    h6Task.setOccurredTime(operateInfo.getTime());
    dao.update(tenant, h6Task, operateInfo);

    // 门店价格计算
    H6TaskDeliveredMsg msg = new H6TaskDeliveredMsg();
    msg.setTenant(tenant);
    msg.setPk(uuid);
    msg.setExecuteDate(h6Task.getExecuteDate());
    publisher.publishForNormal(H6TaskDeliveredEvCallExecutor.EXECUTOR_ID, msg);
  }

  @Override
  public QueryResult<H6Task> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户id");
    Assert.notNull(qd, "自定义查询条件");
    return dao.query(tenant, qd);
  }

  @Override
  @NoTx
  public void logError(String tenant, String uuid, String title, Exception ex, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "租户id");
    Assert.notNull(uuid, "taskId");

    title = title + "，trace_id=" + MDC.get("trace_id");
    if (ex != null) {
      title = title + "，异常信息：" + ex.getMessage();
    }
    dao.updateErrMsg(tenant, uuid, title, operateInfo);
  }
}
