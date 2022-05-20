package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.task.TaskReadHistory;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author guyahui
 * @Since
 */
@Repository
public class TaskReadHistoryDaoBof extends BofBaseDao {

  public static final TEMapper<TaskReadHistory> MAPPER = TEMapperBuilder
      .of(TaskReadHistory.class, PTaskReadHistory.class)
      .primaryKey(PTaskReadHistory.UUID)
      .build();

  /**
   * 根据计划列表、操作者ID查询已读列表
   *
   * @param tenant     租户
   * @param planList   计划列表
   * @param operatorId 操作者Id
   * @return 已读列表
   */
  public List<TaskReadHistory> listByPlanAndOperatorId(String tenant, List<String> planList, String operatorId, String type) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(planList, "planList");
    Assert.hasText(operatorId, "operatorId");
    Assert.hasText(type, "type");

    SelectStatement selectStatement = new SelectBuilder().from(PTaskReadHistory.TABLE_NAME)
        .where(Predicates.equals(PTaskReadHistory.TENANT, tenant))
        .where(Predicates.in2(PTaskReadHistory.PLAN, planList.toArray()))
        .where(Predicates.equals(PTaskReadHistory.OPERATOR_ID, operatorId))
        .where(Predicates.equals(PTaskReadHistory.TYPE, type)).build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  /**
   * 保存已读信息
   *
   * @param tenant          租户
   * @param taskReadHistory 已读任务对象
   * @param operateInfo     操作者信息
   * @return
   */
  public void saveNew(String tenant, TaskReadHistory taskReadHistory, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(taskReadHistory, "taskReadHistory");

    taskReadHistory.setCreateInfo(operateInfo);
    InsertBuilder insert = new InsertBuilder().table(PTaskReadHistory.TABLE_NAME).addValues(MAPPER.forInsert(taskReadHistory));
    jdbcTemplate.update(insert.build());
  }

  /**
   * 根据UK条件删除已读信息
   *
   * @param tenant     租户
   * @param plan       计划ID
   * @param planPeriod 计划批次
   * @param operatorId 操作者ID
   * @param type       类型
   */
  public void deleteByUk(String tenant, String plan, String planPeriod, String operatorId, String type) {
    Assert.hasText(tenant, "租户");
    Assert.hasText(plan, "plan");
    Assert.hasText(planPeriod, "planPeriod");
    Assert.hasText(operatorId, "operatorId");
    Assert.hasText(type, "type");
    DeleteBuilder delete = new DeleteBuilder().table(PTaskReadHistory.TABLE_NAME)
        .where(Predicates.equals(PTaskReadHistory.TENANT, tenant))
        .where(Predicates.equals(PTaskReadHistory.PLAN, plan))
        .where(Predicates.equals(PTaskReadHistory.PLAN_PERIOD, planPeriod))
        .where(Predicates.equals(PTaskReadHistory.OPERATOR_ID, operatorId))
        .where(Predicates.equals(PTaskReadHistory.TYPE, type));
    jdbcTemplate.update(delete.build());
  }
}
