package com.hd123.baas.sop.job.bean;

import com.hd123.baas.sop.service.api.taskplan.TaskPlan;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanNewService;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanState;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryOrderDirection;
import com.hd123.rumba.commons.biz.query.QueryResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author guyahui
 */
@Slf4j
@DisallowConcurrentExecution
@Component
public class TaskPlanFinishJob implements Job {

  @Autowired
  private TaskPlanNewService taskPlanNewService;

  public static final int PAGE_SIZE = 100;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

    MDC.put("trace_id", UUID.randomUUID().toString());
    log.info("任务计划终止job开始执行");
    List<String> tenants = getTenants();
    if (CollectionUtils.isEmpty(tenants)) {
      log.info("当前无租户，忽略");
      return;
    }
    for (String tenant : tenants) {
      finishTaskPlan(tenant);
    }
    log.info("任务计划终止job终止执行");
  }

  private void finishTaskPlan(String tenant) {
    // 分页遍历所有的任务计划，将处于已结束的任务状态更改为已结束
    QueryDefinition queryDefinition = new QueryDefinition();
    queryDefinition.setPageSize(PAGE_SIZE);
    queryDefinition.addByField(TaskPlan.Queries.END_DATE, Cop.LESS, new Date());
    queryDefinition.addByField(TaskPlan.Queries.STATE, Cop.EQUALS, TaskPlanState.EFFECTIVE.name());
    queryDefinition.addByField(TaskPlan.Queries.TYPE, Cop.EQUALS, TaskPlanType.INSPECTION.name());
    QueryResult<TaskPlan> query = taskPlanNewService.query(tenant, queryDefinition);
    if (CollectionUtils.isEmpty(query.getRecords())) {
      return;
    }
    for (int page = 0; page < query.getPageCount(); page++) {
      QueryDefinition qd = new QueryDefinition();
      qd.setPageSize(PAGE_SIZE);
      qd.setPage(page);
      qd.addByField(TaskPlan.Queries.END_DATE, Cop.LESS, new Date());
      qd.addByField(TaskPlan.Queries.STATE, Cop.EQUALS, TaskPlanState.EFFECTIVE.name());
      qd.addByField(TaskPlan.Queries.TYPE, Cop.EQUALS, TaskPlanType.INSPECTION.name());
      qd.addOrder(TaskPlan.Queries.CREATED, QueryOrderDirection.desc);
      QueryResult<TaskPlan> queryResult = taskPlanNewService.query(tenant, qd);
      if (CollectionUtils.isNotEmpty(queryResult.getRecords())) {
        for (TaskPlan taskPlan : queryResult.getRecords()) {
          taskPlanNewService.updateState(tenant, taskPlan.getUuid(), TaskPlanState.FINISHED.name(),
              getSysOperateInfo());
        }
      }
    }
  }

  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  @SneakyThrows
  private List<String> getTenants() {
    return taskPlanNewService.listTenant().stream().map(TaskPlan::getTenant).collect(Collectors.toList());
  }

  protected OperateInfo getSysOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }
}
