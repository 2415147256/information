package com.hd123.baas.sop.service.dao.taskpoints;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.taskpoints.TaskPoints;
import com.hd123.baas.sop.service.api.taskpoints.TaskPointsScoreboard;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author liyan
 * @date 2021/6/3
 */
@Repository
public class TaskPointsScoreboardDaoBof extends BofBaseDao {
  @Autowired
  protected JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  public static final TEMapper<TaskPointsScoreboard> MAPPER = TEMapperBuilder
      .of(TaskPointsScoreboard.class, PTaskPointsScoreboard.class)
      .build();
  public static final TaskPointsRankMapper TASK_POINTS_RANK_MAPPER = new TaskPointsRankMapper();
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TaskPoints.class, PTaskPoints.class).build();

  public Long getRankByUserId(String tenant, String userId) {
    Assert.notBlank(tenant, "租户");
    Assert.notNull(userId, "用户代码");
    SelectStatement selectIn = new SelectBuilder().select(PTaskPointsScoreboard.POINTS)
        .from(PTaskPointsScoreboard.TABLE_NAME)
        .where(Predicates.equals(PTaskPoints.TENANT, tenant))
        .where(Predicates.equals(PTaskPoints.USER_ID, userId))
        .build();
    SelectStatement selectStatement = new SelectBuilder() //
        .select("case when count(1)>0 then count(1)+1 else 1 end as `rank`")
        .from(PTaskPointsScoreboard.TABLE_NAME)
        .where(Predicates.equals(PTaskPoints.TENANT, tenant))
        .where(Predicates.greater(PTaskPoints.POINTS, selectIn))
        .build();
    TaskPointsScoreboard taskPointsScoreboard = getFirst(jdbcTemplate.query(selectStatement, TASK_POINTS_RANK_MAPPER));
    return null != taskPointsScoreboard ? taskPointsScoreboard.getRank() : 0L;
  }

  public TaskPointsScoreboard getByUserId(String tenant, String userId) {
    Assert.notBlank(tenant, "租户");
    Assert.notNull(userId, "用户代码");
    SelectStatement selectStatement = new SelectBuilder().from(PTaskPointsScoreboard.TABLE_NAME)
        .where(Predicates.equals(PTaskPoints.TENANT, tenant))
        .where(Predicates.equals(PTaskPoints.USER_ID, userId))
        .build();
    return getFirst(jdbcTemplate.query(selectStatement, MAPPER));
  }

  public List<TaskPointsScoreboard> scoreboard(String tenant) {
    Assert.notBlank(tenant, "租户");
    SelectStatement selectStatement = new SelectBuilder().from(PTaskPointsScoreboard.TABLE_NAME)
        .where(Predicates.equals(PTaskPoints.TENANT, tenant))
        .orderBy(PTaskPoints.POINTS, false)
        .orderBy(PTaskPoints.USER_ID, true)
        .limit(10)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }
}
