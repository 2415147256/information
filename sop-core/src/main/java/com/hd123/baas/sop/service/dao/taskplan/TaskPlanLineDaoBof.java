package com.hd123.baas.sop.service.dao.taskplan;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanLine;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
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
 * @date 2021/5/6 21:22
 */
@Repository
public class TaskPlanLineDaoBof extends BofBaseDao {

  @Autowired
  protected JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  public static final TEMapper<TaskPlanLine> MAPPER = TEMapperBuilder.of(TaskPlanLine.class, PTaskPlanLine.class)
      .primaryKey(PTaskPlanLine.UUID)
      .build();
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TaskPlanLine.class, PTaskPlanLine.class).build();

  public int batchInsert(String tenant, List<TaskPlanLine> lines) {
    Assert.hasText(tenant, "tanant");
    Assert.notEmpty(lines, "taskPlanLines");

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    lines.forEach(entity -> batchUpdater
        .add(new InsertBuilder().table(PTaskPlanLine.TABLE_NAME).addValues(MAPPER.forInsert(entity)).build()));
    return batchUpdater.update().stream().flatMapToInt(Arrays::stream).sum();
  }

  public List<TaskPlanLine> list(String tenant, String owner) {
    Assert.hasText(tenant, "tanant");
    Assert.hasText(owner, "owner");

    SelectStatement selectStatement = new SelectBuilder().from(PTaskPlanLine.TABLE_NAME)
        .where(Predicates.equals(PTaskPlanLine.OWNER, owner))
        .where(Predicates.equals(PTaskPlanLine.TENANT, tenant))
        .orderBy(PTaskPlanLine.OWNER, false)
        .build();

    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public void deleteByOwner(String tenant, String owner) {
    Assert.notNull(owner, "owner");
    Assert.notNull(tenant, "tenant");

    DeleteBuilder delete = new DeleteBuilder().table(PTaskPlanLine.TABLE_NAME)
        .where(Predicates.equals(PTaskPlanLine.TENANT, tenant))
        .where(Predicates.equals(PTaskPlanLine.OWNER, owner));
    jdbcTemplate.update(delete.build());
  }

  public QueryResult<TaskPlanLine> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "qd");
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    return executor.query(selectStatement, MAPPER);
  }

  public List<TaskPlanLine> listByOwners(String tenant, List<String> owners) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(owners, "owners");
    SelectStatement selectStatement = new SelectBuilder().from(PTaskPlanLine.TABLE_NAME)
        .where(Predicates.in2(PTaskPlanLine.OWNER, owners.toArray()))
        .where(Predicates.equals(PTaskPlanLine.TENANT, tenant))
        .orderBy(PTaskPlanLine.OWNER, false)
        .build();

    return jdbcTemplate.query(selectStatement, MAPPER);
  }
}
