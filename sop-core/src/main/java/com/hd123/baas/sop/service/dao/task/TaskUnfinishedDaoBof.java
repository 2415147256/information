package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.task.ShopTaskState;
import com.hd123.baas.sop.service.api.task.TaskUnfinished;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanType;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author guyahui
 * @Since
 */
@Repository
public class TaskUnfinishedDaoBof extends BofBaseDao {

  public static final TEMapper<TaskUnfinished> MAPPER = TEMapperBuilder
      .of(TaskUnfinished.class, PTaskUnfinished.class)
      .build();


  public List<TaskUnfinished> query(String tenant, String operatorId, Integer pageStart, Integer pageSize) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(operatorId, "operatorId");
    Assert.notNull(pageStart, "pageStart");
    Assert.notNull(pageSize, "pageSize");

    /*
    SELECT a.plan,a.plan_name,a.plan_period,a.plan_start_time,a.plan_end_time FROM shop_task a WHERE a.type = 'INSPECTION' a.tenant = 'mkhtest' and  a.uuid IN (
    SELECT b.OWNER FROM shop_task_log b WHERE b.operator_id = 'hdposadmin' and b.state = 'UNFINISHED' and tenant = 'mkhtest')
    GROUP BY a.plan,a.plan_name,a.plan_period,a.plan_start_time,a.plan_end_time,a.tenant ,a.plan_type ORDER BY plan_start_time LIMIT 1000 ;
     */
    SelectStatement selectShopTaskId = new SelectBuilder().select(PShopTaskLog.OWNER)
        .distinct()
        .from(PShopTaskLog.TABLE_NAME, PShopTaskLog.TABLE_ALIAS)
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OPERATOR_ID, operatorId))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.STATE, ShopTaskState.UNFINISHED.name())).build();
    SelectStatement select = new SelectBuilder().select(PTaskUnfinished.TENANT, PTaskUnfinished.PLAN,
        PTaskUnfinished.PLAN_NAME, PTaskUnfinished.PLAN_PERIOD,
        PTaskUnfinished.PLAN_START_TIME, PTaskUnfinished.PLAN_END_TIME, PTaskUnfinished.PLAN_TYPE).from(PTaskUnfinished.TABLE_NAME, PTaskUnfinished.TABLE_ALIAS)
        .where(Predicates.equals(PTaskUnfinished.TABLE_ALIAS, PTaskUnfinished.TENANT, tenant))
        .where(Predicates.equals(PTaskUnfinished.PLAN_TYPE, TaskPlanType.INSPECTION.name()))
        .where(Predicates.in(PTaskUnfinished.TABLE_ALIAS, PTaskUnfinished.UUID, selectShopTaskId))
        .groupBy(PTaskUnfinished.TENANT, PTaskUnfinished.PLAN,
            PTaskUnfinished.PLAN_NAME, PTaskUnfinished.PLAN_PERIOD,
            PTaskUnfinished.PLAN_START_TIME, PTaskUnfinished.PLAN_END_TIME, PTaskUnfinished.PLAN_TYPE)
        .orderBy(PTaskUnfinished.PLAN_START_TIME)
        .limit(pageStart, pageSize).build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public long count(String tenant, String operatorId, String type) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(operatorId, "operatorId");
    Assert.hasText(type, "planType");

    /*
    SELECT count(1) as cnt from
    (SELECT a.plan,a.plan_name,a.plan_period,a.plan_start_time,a.plan_end_time FROM shop_task a WHERE a.tenant = 'mkhtest'
    and  a.uuid IN (
    SELECT b.OWNER FROM shop_task_log b WHERE b.operator_id = 'hdposadmin' and b.state = 'UNFINISHED' and tenant = 'mkhtest')
    GROUP BY a.plan,a.plan_name,a.plan_period,a.plan_start_time,a.plan_end_time,a.tenant,a.plan_type ORDER BY plan_start_time ) d;
     */

    SelectStatement selectShopTaskId = new SelectBuilder().select(PShopTaskLog.OWNER)
        .distinct()
        .from(PShopTaskLog.TABLE_NAME, PShopTaskLog.TABLE_ALIAS)
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OPERATOR_ID, operatorId))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.STATE, ShopTaskState.UNFINISHED.name())).build();
    SelectStatement select = new SelectBuilder().select(PTaskUnfinished.TENANT, PTaskUnfinished.PLAN,
        PTaskUnfinished.PLAN_NAME, PTaskUnfinished.PLAN_PERIOD,
        PTaskUnfinished.PLAN_START_TIME, PTaskUnfinished.PLAN_END_TIME, PTaskUnfinished.PLAN_TYPE).from(PTaskUnfinished.TABLE_NAME, PTaskUnfinished.TABLE_ALIAS)
        .where(Predicates.equals(PTaskUnfinished.TABLE_ALIAS, PTaskUnfinished.TENANT, tenant))
        .where(Predicates.equals(PTaskUnfinished.PLAN_TYPE, type))
        .where(Predicates.in(PTaskUnfinished.TABLE_ALIAS, PTaskUnfinished.UUID, selectShopTaskId))
        .groupBy(PTaskUnfinished.TENANT, PTaskUnfinished.PLAN,
            PTaskUnfinished.PLAN_NAME, PTaskUnfinished.PLAN_PERIOD,
            PTaskUnfinished.PLAN_START_TIME, PTaskUnfinished.PLAN_END_TIME, PTaskUnfinished.PLAN_TYPE)
        .orderBy(PTaskUnfinished.PLAN_START_TIME)
        .build();
    SelectStatement selectCount = new SelectBuilder().select("count(1) as cnt")
        .from(select, "c").build();
    List<Long> count = jdbcTemplate.query(selectCount, new SingleColumnRowMapper<>());
    return count.get(0);
  }
}
