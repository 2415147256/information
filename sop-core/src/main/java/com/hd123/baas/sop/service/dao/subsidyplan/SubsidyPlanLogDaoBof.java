package com.hd123.baas.sop.service.dao.subsidyplan;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanLog;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author liuhaoxin
 */
@Repository
public class SubsidyPlanLogDaoBof extends BofBaseDao {

  private static final TEMapper<SubsidyPlanLog> SUBSIDY_PLAN_LOG_MAPPER = TEMapperBuilder
      .of(SubsidyPlanLog.class, SubsidyPlanLog.SubsidyPlanLogSchema.class)
      .primaryKey(SubsidyPlanLog.SubsidyPlanLogSchema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SubsidyPlanLog.class,
      SubsidyPlanLog.SubsidyPlanLogSchema.class).build();

  public void save(String tenant, SubsidyPlanLog subsidyPlanLog, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(subsidyPlanLog, "subsidyPlanLog");

    subsidyPlanLog.setCreated(operateInfo.getTime());
    subsidyPlanLog.setCreatorId(operateInfo.getOperator().getId());
    subsidyPlanLog.setCreatorName(operateInfo.getOperator().getFullName());
    subsidyPlanLog.setCreatorNS(operateInfo.getOperator().getNamespace());

    InsertStatement insert = new InsertBuilder().table(SubsidyPlanLog.SubsidyPlanLogSchema.TABLE_NAME)
        .values(SUBSIDY_PLAN_LOG_MAPPER.forInsert(subsidyPlanLog, true))
        .build();
    jdbcTemplate.update(insert);
  }

  public SubsidyPlanLog get(String tenant, String uuid) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().from(SubsidyPlanLog.SubsidyPlanLogSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlanLog.SubsidyPlanLogSchema.TENANT, tenant),
            Predicates.equals(SubsidyPlanLog.SubsidyPlanLogSchema.UUID, uuid))
        .build();
    return getFirst(select, SUBSIDY_PLAN_LOG_MAPPER);
  }

  public QueryResult<SubsidyPlanLog> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(SubsidyPlanLog.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, SUBSIDY_PLAN_LOG_MAPPER);
  }
}
