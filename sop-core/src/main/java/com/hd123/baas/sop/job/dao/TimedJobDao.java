package com.hd123.baas.sop.job.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.job.entity.PTimedJob;
import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.baas.sop.job.mapper.TimedJobMapper;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author zhengzewang
 */
@Repository
public class TimedJobDao {
  @Autowired
  protected JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TimedJob.class, PTimedJob.class).build();

  public String insert(TimedJob job) {
    Assert.notNull(job, "job");
    Assert.notNull(job.getUuid(), "job.uuid");
    Assert.notNull(job.getCallbackBeanName(), "job.callbackBeanName");
    Assert.notNull(job.getExpectedRunTime(), "job.expectedRunTime");
    Assert.notNull(job.getParams(), "job.params");

    InsertStatement insert = new InsertBuilder().table(PTimedJob.TABLE_NAME)
        .addValue(PTimedJob.UUID, job.getUuid())
        .addValue(PTimedJob.TRAN_ID, job.getTranId())
        .addValue(PTimedJob.PARAMS, job.getParams())
        .addValue(PTimedJob.RUN_TIMES, job.getRunTimes())
        .addValue(PTimedJob.CALLBACK_BEAN_NAME, job.getCallbackBeanName())
        .addValue(PTimedJob.EXPECTED_RUN_TIME, job.getExpectedRunTime())
        .addValue("`" + PTimedJob.INTERVAL + "`", job.getInterval())
        .build();
    jdbcTemplate.update(insert);
    return job.getUuid();
  }

  public void delete(String uuid) {
    DeleteStatement delete = new DeleteBuilder().table(PTimedJob.TABLE_NAME)
        .where(Predicates.equals(PTimedJob.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  public void deleteByTranId(String tranId) {
    DeleteStatement delete = new DeleteBuilder().table(PTimedJob.TABLE_NAME)
        .where(Predicates.equals(PTimedJob.TRAN_ID, tranId))
        .build();
    jdbcTemplate.update(delete);
  }

  public TimedJob get(String uuid) {
    SelectStatement select = new SelectBuilder().select()
        .from(PTimedJob.TABLE_NAME)
        .where(Predicates.equals(PTimedJob.UUID, uuid))
        .distinct()
        .build();
    List<TimedJob> list = jdbcTemplate.query(select, new TimedJobMapper());
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  public QueryResult<TimedJob> query(QueryDefinition qd) {
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new TimedJobMapper());
  }
}
