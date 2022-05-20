package com.hd123.baas.sop.service.dao.taskplan;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanItem;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author guyahui
 * @date 2021/5/24 14:32
 */
@Repository
public class TaskPlanItemDaoBof extends BofBaseDao {
  @Autowired
  protected JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  public static final TEMapper<TaskPlanItem> MAPPER = TEMapperBuilder.of(TaskPlanItem.class, PTaskPlanItem.class)
      .primaryKey(PTaskPlanItem.UUID, PTaskPlanItem.TENANT)
      .build();
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TaskPlanItem.class, PTaskPlanItem.class).build();

  public int batchInsert(String tenant, List<TaskPlanItem> items) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(items, "taskPlanItem");

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    items.forEach(entity -> batchUpdater
        .add(new InsertBuilder().table(PTaskPlanItem.TABLE_NAME).addValues(MAPPER.forInsert(entity)).build()));
    return batchUpdater.update().stream().flatMapToInt(Arrays::stream).sum();
  }

  public int insert(String tenant, TaskPlanItem item) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(item, "普通任务明细");
    if (item.getUuid() == null) {
      item.setUuid(UUID.randomUUID().toString());
    }
    InsertBuilder insert = new InsertBuilder().table(PTaskPlanItem.TABLE_NAME).addValues(MAPPER.forInsert(item));
    return jdbcTemplate.update(insert.build());
  }

  public void deleteByOwner(String tenant, String owner) {
    Assert.notNull(owner, "owner");
    Assert.notNull(tenant, "tenant");

    DeleteBuilder delete = new DeleteBuilder().table(PTaskPlanItem.TABLE_NAME)
        .where(Predicates.equals(PTaskPlanItem.TENANT, tenant))
        .where(Predicates.equals(PTaskPlanItem.OWNER, owner));
    jdbcTemplate.update(delete.build());
  }

  public List<TaskPlanItem> listByOwners(String tenant, List<String> taskPlanIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(taskPlanIds, "taskPlanIds");

    SelectStatement selectStatement = new SelectBuilder().from(PTaskPlanItem.TABLE_NAME)
        .where(Predicates.in2(PTaskPlanItem.OWNER, taskPlanIds.toArray()))
        .where(Predicates.equals(PTaskPlanItem.TENANT, tenant))
        .orderBy(PTaskPlanItem.POINT, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }
}
