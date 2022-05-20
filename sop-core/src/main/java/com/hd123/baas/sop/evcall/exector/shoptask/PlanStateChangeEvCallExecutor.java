package com.hd123.baas.sop.evcall.exector.shoptask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.task.*;
import com.hd123.baas.sop.service.dao.task.ShopTaskDaoBof;
import com.hd123.baas.sop.service.dao.task.ShopTaskLogDaoBof;
import com.hd123.baas.sop.service.dao.task.ShopTaskSummaryDaoBof;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Component
public class PlanStateChangeEvCallExecutor extends AbstractEvCallExecutor<PlanStateChangeMsg> {

  public static final String PLAN_STATE_CHANGE_EXECUTOR_ID = PlanStateChangeEvCallExecutor.class.getSimpleName();

  @Autowired
  private ShopTaskSummaryService shopTaskSummaryService;
  @Autowired
  private ShopTaskService shopTaskService;
  @Autowired
  private ShopTaskDaoBof shopTaskDao;
  @Autowired
  private ShopTaskLogDaoBof shopTaskLogDao;
  @Autowired
  private ShopTaskSummaryDaoBof shopTaskSummaryDao;

  @Override
  @Tx
  protected void doExecute(PlanStateChangeMsg msg, EvCallExecutionContext context) throws Exception {
    String plan = msg.getPlan();
    String tenant = msg.getTenant();
    PlanAction action = msg.getAction();
    String planPeriodCode = msg.getPlanPeriodCode();
    Assert.notNull(tenant, "tenant");
    Assert.notNull(plan, "plan");
    Assert.notNull(action, "action");
    Assert.notNull(msg.getOperateInfo(), "operatorInfo");
    log.info("正在处理的过期/终止的计划ID：{}",plan);
    ShopTaskState state = ShopTaskState.EXPIRED;
    if (action == PlanAction.EXPIRE) {
      state = ShopTaskState.EXPIRED;
    } else if (action == PlanAction.TERMINATE) {
      state = ShopTaskState.TERMINATE;
    } else {
      log.error("不支持改操作：{}", JsonUtil.objectToJson(msg));
      return;
    }
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ShopTaskSummary.Queries.PLAN, Cop.EQUALS, plan);
    qd.addByField(ShopTaskSummary.Queries.STATE, Cop.EQUALS, ShopTaskState.UNFINISHED.name());
    if (StringUtils.isNotBlank(planPeriodCode)){
      qd.addByField(ShopTaskSummary.Queries.PLAN_PERIOD_CODE,Cop.EQUALS,planPeriodCode);
    }
    QueryResult<ShopTaskSummary> shopSummaryResult = shopTaskSummaryService.query(tenant, qd);
    if (CollectionUtils.isEmpty(shopSummaryResult.getRecords())) {
      log.info("不存在未完成任务更新,plan:{}",plan);
      return;
    }
    for (ShopTaskSummary record : shopSummaryResult.getRecords()) {
      record.setState(state);
      record.setFinishTime(msg.getOperateInfo().getTime());
      List<ShopTask> shopTasks = shopTaskService.list(tenant, record.getUuid(), ShopTaskService.SHOP_TASK_LOG);
      if (CollectionUtils.isNotEmpty(shopTasks)) {
        List<ShopTask> unfinishedTasks = shopTasks.stream()
            .filter(s -> s.getState() == ShopTaskState.UNFINISHED)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(unfinishedTasks)) {
          continue;
        }

        List<ShopTaskLog> logs = new ArrayList<>();
        for (ShopTask unfinishedTask : unfinishedTasks) {
          unfinishedTask.setState(state);
          unfinishedTask.setFinishInfo(msg.getOperateInfo());
          List<ShopTaskLog> unfinishedTaskLogs = unfinishedTask.getLogs()
              .stream()
              .filter(s -> s.getState().equals(ShopTaskState.UNFINISHED.name()))
              .collect(Collectors.toList());
          if (CollectionUtils.isEmpty(unfinishedTaskLogs)) {
            continue;
          }
          ShopTaskState finalState = state;
          unfinishedTaskLogs.stream().forEach(s -> {
            s.setState(finalState.name());
            s.setFinishInfo(msg.getOperateInfo());
          });
          logs.addAll(unfinishedTaskLogs);
        }
        shopTaskDao.batchUpdate(tenant, shopTasks, msg.getOperateInfo());
        shopTaskLogDao.batchUpdate(tenant, logs, msg.getOperateInfo());
        //取消过期任务的交接状态
        List<String> shopTaskIds = shopTasks.stream().map(ShopTask::getUuid).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(shopTaskIds)){
          log.info("取消交接任务的shopTaskIds：{}",shopTaskIds);
          shopTaskService.cancelByShopTaskId(tenant,shopTaskIds);
        }
      }
      shopTaskSummaryDao.update(tenant, record);
    }

  }

  @Override
  protected PlanStateChangeMsg decodeMessage(String msg) throws BaasException {
    log.info("收到PlanStateChangeMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, PlanStateChangeMsg.class);
  }
}
