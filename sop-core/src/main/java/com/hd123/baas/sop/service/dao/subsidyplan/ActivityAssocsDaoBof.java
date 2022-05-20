package com.hd123.baas.sop.service.dao.subsidyplan;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanActivityAssoc;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;

/**
 * @author liuhaoxin
 */
@Repository
public class ActivityAssocsDaoBof extends BofBaseDao {

  private static final TEMapper<SubsidyPlanActivityAssoc> ACTIVITY_ASSOCS_MAPPER = TEMapperBuilder
      .of(SubsidyPlanActivityAssoc.class, SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.class)
      .map("activityType", SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.ACTIVITY_TYPE,
          EnumConverters.toString(ActivityType.class), EnumConverters.toEnum(ActivityType.class))
      .primaryKey(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SubsidyPlanActivityAssoc.class,
      SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.class).build();

  public void saveNew(String tenant, SubsidyPlanActivityAssoc activityAssoc) {
    Assert.hasText(tenant, "租户");
    Assert.notNull(activityAssoc, "活动关联关系");

    activityAssoc.setTenant(tenant);
    InsertStatement insert = new InsertBuilder()
        .table(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TABLE_NAME)
        .values(ACTIVITY_ASSOCS_MAPPER.forInsert(activityAssoc))
        .build();
    jdbcTemplate.update(insert);
  }

  public void batchSave(String tenant, List<SubsidyPlanActivityAssoc> activityAssocs) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(activityAssocs, "活动关联列表");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SubsidyPlanActivityAssoc activityAssoc : activityAssocs) {
      activityAssoc.setTenant(tenant);
      InsertStatement insert = new InsertBuilder()
          .table(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TABLE_NAME)
          .values(ACTIVITY_ASSOCS_MAPPER.forInsert(activityAssoc))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public List<SubsidyPlanActivityAssoc> list(String tenant, String owner) {
    return list(tenant, owner, null);
  }

  public List<SubsidyPlanActivityAssoc> list(String tenant, String owner, Collection<ActivityType> activityTypes) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(owner, "owner");

    SelectStatement select = new SelectBuilder()
        .from(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TENANT, tenant),
            Predicates.equals(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.OWNER, owner))
        .build();
    if (activityTypes != null && activityTypes.size() > 0) {
      select.where(Predicates.in2(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.ACTIVITY_TYPE,
          activityTypes.stream().map(ActivityType::name).collect(Collectors.toList()).toArray()));
    }
    return jdbcTemplate.query(select, ACTIVITY_ASSOCS_MAPPER);
  }

  public List<SubsidyPlanActivityAssoc> listByOwners(String tenant, Collection<String> owners) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(owners, "owners");

    SelectStatement select = new SelectBuilder()
        .from(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TENANT, tenant),
            Predicates.in2(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.OWNER, owners.toArray()))
        .build();
    return jdbcTemplate.query(select, ACTIVITY_ASSOCS_MAPPER);
  }

  public void batchRemove(String tenant, Collection<String> uuids) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuids, "uuids");

    DeleteStatement del = new DeleteBuilder().table(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TABLE_NAME)
        .where(Predicates.in2(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.UUID, uuids.toArray()),
            Predicates.equals(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TENANT, tenant))
        .build();
    jdbcTemplate.update(del);
  }

  public QueryResult<SubsidyPlanActivityAssoc> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(SubsidyPlanActivityAssoc.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, ACTIVITY_ASSOCS_MAPPER);
  }

  public List<SubsidyPlanActivityAssoc> listByOwnerAndActivitys(String tenant, String owner,
      Collection<String> activityIds) {

    Assert.hasText(tenant, "tenant");
    Assert.notNull(owner, "owner");

    SelectStatement select = new SelectBuilder()
        .from(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.TENANT, tenant),
            Predicates.equals(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.OWNER, owner),
            Predicates.in2(SubsidyPlanActivityAssoc.SubsidyPlanActivityAssocSchema.ACTIVITY_ID, activityIds.toArray()))
        .build();
    return jdbcTemplate.query(select, ACTIVITY_ASSOCS_MAPPER);
  }
}
