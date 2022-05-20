package com.hd123.baas.sop.jmzs.workflowrequest.dao;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkflowRequest;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkFlowRequestDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(WorkflowRequest.class,
      PWorkFlowRequest.class).build();

  public QueryResult<WorkflowRequest> query (String tenant, QueryDefinition qd) {
    Assert.hasText(tenant);
    Assert.notNull(qd);

    if (!StringUtil.isNullOrBlank(tenant)) {
      qd.addByField(WorkflowRequest.Queries.TENANT, Cop.EQUALS, tenant);
    }
    SelectStatement statement = QUERY_PROCESSOR.process(qd);
    return executor.query(statement, new WorkFlowRequestMapper());
  }

  public WorkflowRequest get(String tenant, String uuid) {
    return get(tenant, uuid, false);
  }

  public WorkflowRequest get(String tenant, String uuid,boolean forUpdate) {

    Assert.hasText(tenant);
    Assert.hasText(uuid);

    SelectBuilder select = new SelectBuilder()
        .from(PWorkFlowRequest.TABLE_NAME, PWorkFlowRequest.TABLE_ALIAS)
        .where(Predicates.equals(PWorkFlowRequest.TENANT, tenant))
        .where(Predicates.equals(PWorkFlowRequest.UUID, uuid));

    if (forUpdate) {
      select.forUpdate();
    }

    List<WorkflowRequest> list = jdbcTemplate.query(select.build(), new WorkFlowRequestMapper());

    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);

  }

  @Tx
  public void saveNew(WorkflowRequest entity) {
    Assert.notNull(entity);

    if (StringUtils.isEmpty(entity.getUuid())) {
      entity.setUuid(IdGenUtils.buildRdUuid());
    }

    InsertStatement insertStatement = new InsertBuilder().table(PWorkFlowRequest.TABLE_NAME)
        .addValues(PWorkFlowRequest.toFieldValues(entity))
        .build();

    jdbcTemplate.update(insertStatement);

  }

  @Tx
  public void update(WorkflowRequest entity) {
    Assert.notNull(entity);
    Assert.hasText(entity.getUuid());

    UpdateStatement updateStatement = new UpdateBuilder().table(PWorkFlowRequest.TABLE_NAME)
        .setValues(PWorkFlowRequest.toFieldValues(entity))
        .where(Predicates.equals(PWorkFlowRequest.UUID, entity.getUuid()))
        .where(Predicates.equals(PWorkFlowRequest.TENANT, entity.getTenant()))
        .build();

    jdbcTemplate.update(updateStatement);
  }

  public void delete(String tenant, String uuid) {
    Assert.hasText(tenant);
    Assert.hasText(uuid);

    DeleteStatement delete = new DeleteBuilder()
        .table(PWorkFlowRequest.TABLE_NAME)
        .where(Predicates.equals(PWorkFlowRequest.TENANT, tenant))
        .where(Predicates.equals(PWorkFlowRequest.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

}
