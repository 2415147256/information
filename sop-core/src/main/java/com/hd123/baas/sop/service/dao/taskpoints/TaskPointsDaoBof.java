package com.hd123.baas.sop.service.dao.taskpoints;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.taskpoints.TaskPoints;
import com.hd123.baas.sop.service.api.taskpoints.TaskPointsOccurredType;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;

/**
 * @author liyan
 * @date 2021/6/3
 */
@Repository
public class TaskPointsDaoBof extends BofBaseDao {
  @Autowired
  protected JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  public static final TEMapper<TaskPoints> MAPPER = TEMapperBuilder.of(TaskPoints.class, PTaskPoints.class)
      .primaryKey(PTaskPoints.UUID, PTaskPoints.TENANT)
      // 增加枚举转换支持
      .map("occurredType", PTaskPoints.OCCURRED_TYPE, //
          EnumConverters.toString(TaskPointsOccurredType.class), EnumConverters.toEnum(TaskPointsOccurredType.class))
      .build();
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TaskPoints.class, PTaskPoints.class).build();

  public QueryResult<TaskPoints> query(String tenant, QueryDefinition qd) {
    Assert.notBlank(tenant, "租户");
    Assert.notNull(qd, "qd");
    qd.addByField(TaskPoints.Queries.TENANT, Cop.EQUALS, tenant);
    return executor.query(QUERY_PROCESSOR.process(qd), MAPPER);
  }

  public void insert(String tenant, TaskPoints taskPoints) {
    Assert.notBlank(tenant, "租户");
    Assert.notNull(taskPoints, "任务积分");
    taskPoints.setTenant(tenant);
    if (taskPoints.getUuid() == null) {
      taskPoints.setUuid(UUID.randomUUID().toString());
    }
    InsertBuilder insert = new InsertBuilder().table(PTaskPoints.TABLE_NAME).addValues(MAPPER.forInsert(taskPoints));
    jdbcTemplate.update(insert.build());
  }

  public TaskPoints getByUK(String tenant, String occurredType, String occurredUuid, boolean forUpdate) {
    Assert.notBlank(tenant, "租户");
    Assert.notNull(occurredType, "任务积分");
    Assert.notNull(occurredUuid, "发生对象ID");
    SelectBuilder selectBuilder = new SelectBuilder().from(PTaskPoints.TABLE_NAME)
        .where(Predicates.equals(PTaskPoints.TENANT, tenant))
        .where(Predicates.equals(PTaskPoints.OCCURRED_TYPE, occurredType))
        .where(Predicates.equals(PTaskPoints.OCCURRED_UUID, occurredUuid));
    SelectStatement selectStatement = forUpdate ? selectBuilder.forUpdate().build() : selectBuilder.build();
    return getFirst(jdbcTemplate.query(selectStatement, MAPPER));
  }
}
