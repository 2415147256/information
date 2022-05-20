package com.hd123.baas.sop.service.dao.subsidyplan;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlan;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanState;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
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
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author liuhaoxin
 */
@Repository
public class SubsidyPlanDaoBof extends BofBaseDao {

  private static final TEMapper<SubsidyPlan> SUBSIDY_PLAN_TE_MAPPER = TEMapperBuilder
      .of(SubsidyPlan.class, SubsidyPlan.SubsidyPlanSchema.class)
      .primaryKey(SubsidyPlan.SubsidyPlanSchema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SubsidyPlan.class,
      SubsidyPlan.SubsidyPlanSchema.class).addConditionProcessor((condition, context) -> {
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(SubsidyPlan.Queries.SHOP_KEYWORD_LIKE, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1")
              .from(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME, SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS) //
              .where(Predicates.or(
                  // 门店名称
                  Predicates.like(SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS, SubsidyPlan.SubsidyPlanSchema.SHOP_NAME,
                      value),
                  // 门店代码
                  Predicates.like(SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS, SubsidyPlan.SubsidyPlanSchema.SHOP_CODE,
                      value)))
              .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS, SubsidyPlan.SubsidyPlanSchema.UUID,
                  alias, SubsidyPlan.SubsidyPlanSchema.UUID))
              .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS, SubsidyPlan.SubsidyPlanSchema.TENANT,
                  alias, SubsidyPlan.SubsidyPlanSchema.TENANT))
              .build();
          return Predicates.exists(select);
        }
        if (StringUtils.equalsIgnoreCase(SubsidyPlan.Queries.XCX_KEYWORD_LIKE, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1")
              .from(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME, SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS) //
              .where(Predicates.or(
                  // 门店名称
                  Predicates.like(SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS, SubsidyPlan.SubsidyPlanSchema.SHOP_NAME,
                      value),
                  // 门店代码
                  Predicates.like(SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS, SubsidyPlan.SubsidyPlanSchema.SHOP_CODE,
                      value),
                  // 计划名称
                  Predicates.like(SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS, SubsidyPlan.SubsidyPlanSchema.PLAN_NAME,
                      value)))
              .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS, SubsidyPlan.SubsidyPlanSchema.UUID,
                  alias, SubsidyPlan.SubsidyPlanSchema.UUID))
              .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TABLE_ALIAS, SubsidyPlan.SubsidyPlanSchema.TENANT,
                  alias, SubsidyPlan.SubsidyPlanSchema.TENANT))
              .build();
          return Predicates.exists(select);
        }
        return null;
      }).build();

  public void saveNew(String tenant, SubsidyPlan subsidyPlan, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(subsidyPlan, "补贴计划");

    subsidyPlan.setCreateInfo(operateInfo);
    subsidyPlan.setLastModifyInfo(operateInfo);
    InsertStatement insert = new InsertBuilder().table(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .values(SUBSIDY_PLAN_TE_MAPPER.forInsert(subsidyPlan, true))
        .build();
    jdbcTemplate.update(insert);
  }

  public void batchSave(String tenant, List<SubsidyPlan> subsidyPlans, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(subsidyPlans, "补贴计划列表");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SubsidyPlan subsidyPlan : subsidyPlans) {
      subsidyPlan.setCreateInfo(operateInfo);
      subsidyPlan.setLastModifyInfo(operateInfo);
      InsertStatement insert = new InsertBuilder().table(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
          .values(SUBSIDY_PLAN_TE_MAPPER.forInsert(subsidyPlan, true))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public void batchUpdate(String tenant, List<SubsidyPlan> subsidyPlans) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(subsidyPlans, "补贴计划列表");
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SubsidyPlan subsidyPlan : subsidyPlans) {
      UpdateStatement update = new UpdateBuilder().table(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
          .addValues(SUBSIDY_PLAN_TE_MAPPER.forUpdate(subsidyPlan, true))
          .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.UUID, subsidyPlan.getUuid()),
              Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant))
          .build();
      updater.add(update);
    }
    updater.update();
  }

  public void update(String tenant, SubsidyPlan subsidyPlan, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(subsidyPlan, "补贴计划");

    subsidyPlan.setLastModifyInfo(operateInfo);
    UpdateStatement update = new UpdateBuilder().table(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .addValues(SUBSIDY_PLAN_TE_MAPPER.forUpdate(subsidyPlan, true))
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.UUID, subsidyPlan.getUuid()),
            Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant))
        .build();
    jdbcTemplate.update(update);
  }

  public void expireSubsidyPlanByDate(String tenant, Date date, OperateInfo operateInfo, String... state) {
    Assert.notNull(tenant, "tenant");

    UpdateBuilder updateBuilder = new UpdateBuilder().table(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .setValue(SubsidyPlan.SubsidyPlanSchema.STATE, SubsidyPlanState.EXPIRED.name())
        .setValue(SubsidyPlan.SubsidyPlanSchema.LAST_MODIFY_INFO_TIME, operateInfo.getTime())
        .setValue(SubsidyPlan.SubsidyPlanSchema.LAST_MODIFY_INFO_OPERATOR_FULL_NAME,
            operateInfo.getOperator().getFullName())
        .setValue(SubsidyPlan.SubsidyPlanSchema.LAST_MODIFY_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(SubsidyPlan.SubsidyPlanSchema.LAST_MODIFY_INFO_OPERATOR_NAMESPACE,
            operateInfo.getOperator().getNamespace())
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant))
        .where(Predicates.lessOrEquals(SubsidyPlan.SubsidyPlanSchema.EFFECTIVE_END_TIME, date));

    if (!Objects.isNull(state)) {
      updateBuilder.where(Predicates.in2(SubsidyPlan.SubsidyPlanSchema.STATE, state));
    }
    jdbcTemplate.update(updateBuilder.build());
  }

  public void effectSubsidyPlanByDate(String tenant, Date date, String... state) {
    Assert.notNull(tenant, "tenant");

    UpdateBuilder updateBuilder = new UpdateBuilder().table(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .setValue(SubsidyPlan.SubsidyPlanSchema.STATE, SubsidyPlanState.PUBLISHED.name())
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant))
        .where(Predicates.lessOrEquals(SubsidyPlan.SubsidyPlanSchema.EFFECTIVE_START_TIME, date))
        .where(Predicates.greaterOrEquals(SubsidyPlan.SubsidyPlanSchema.EFFECTIVE_END_TIME, date));

    if (!Objects.isNull(state)) {
      updateBuilder.where(Predicates.in2(SubsidyPlan.SubsidyPlanSchema.STATE, state));
    }
    jdbcTemplate.update(updateBuilder.build());
  }

  public void delete(String tenant, String uuid) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "计划id");

    DeleteStatement delete = new DeleteBuilder().table(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.UUID, uuid),
            Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant))
        .build();
    jdbcTemplate.update(delete);
  }

  public void modify(String tenant, SubsidyPlan subsidyPlan, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(subsidyPlan, "subsidyPlan");

    subsidyPlan.setLastModifyInfo(operateInfo);
    UpdateStatement update = new UpdateBuilder().table(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .setValues(SUBSIDY_PLAN_TE_MAPPER.forInsert(subsidyPlan, true))
        .build();
    jdbcTemplate.update(update);
  }

  public SubsidyPlan get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().from(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant),
            Predicates.equals(SubsidyPlan.SubsidyPlanSchema.UUID, uuid))
        .build();
    return getFirst(select, SUBSIDY_PLAN_TE_MAPPER);
  }

  public List<SubsidyPlan> listByShop(String tenant, String orgId, String shop, String... state) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shop, "shop");

    SelectStatement select = new SelectBuilder().from(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant),
            Predicates.equals(SubsidyPlan.SubsidyPlanSchema.ORG_ID, orgId),
            Predicates.equals(SubsidyPlan.SubsidyPlanSchema.SHOP, shop))
        .build();
    if (!ArrayUtils.isNotEmpty(state)) {
      return jdbcTemplate.query(select, SUBSIDY_PLAN_TE_MAPPER);
    }
    select.where(Predicates.in2(SubsidyPlan.SubsidyPlanSchema.STATE, state));
    return jdbcTemplate.query(select, SUBSIDY_PLAN_TE_MAPPER);
  }

  public List<SubsidyPlan> listByShops(String tenant, String orgId, Collection<String> shops, String... state) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shops, "shops");

    SelectStatement select = new SelectBuilder().from(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant),
            Predicates.in2(SubsidyPlan.SubsidyPlanSchema.SHOP, shops.toArray()))
        .build();
    if (StringUtils.isNotBlank(orgId)) {
      select.where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.ORG_ID, orgId));
    }
    if (!Objects.isNull(state)) {
      select.where(Predicates.in2(SubsidyPlan.SubsidyPlanSchema.STATE, state));
    }
    List<SubsidyPlan> list = jdbcTemplate.query(select, SUBSIDY_PLAN_TE_MAPPER);
    return list;
  }

  public SubsidyPlan getByShop(String tenant, String shop, String... state) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shop, "shop");

    SelectStatement select = new SelectBuilder().from(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant),
            Predicates.equals(SubsidyPlan.SubsidyPlanSchema.SHOP, shop))
        .build();
    if (ArrayUtils.isNotEmpty(state)) {
      select.where(Predicates.in2(SubsidyPlan.SubsidyPlanSchema.STATE, state));
    }
    return getFirst(select, SUBSIDY_PLAN_TE_MAPPER);
  }

  public QueryResult<SubsidyPlan> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(SubsidyPlan.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, SUBSIDY_PLAN_TE_MAPPER);
  }

  public List<SubsidyPlan> listByUuids(String tenant, List<String> uuids, String... state) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuids, "uuids");

    SelectStatement select = new SelectBuilder().from(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant),
            Predicates.in2(SubsidyPlan.SubsidyPlanSchema.UUID, uuids.toArray()))
        .build();
    if (ArrayUtils.isNotEmpty(state)) {
      select.where(Predicates.in2(SubsidyPlan.SubsidyPlanSchema.STATE, state));
    }
    List<SubsidyPlan> list = jdbcTemplate.query(select, SUBSIDY_PLAN_TE_MAPPER);
    return list;
  }

  public List<SubsidyPlan> listByEffectiveDateScope(String tenant, Date effectiveDate, String... state) {
    Assert.hasText(tenant, "tenant");

    SelectStatement select = new SelectBuilder().from(SubsidyPlan.SubsidyPlanSchema.TABLE_NAME)
        .where(Predicates.equals(SubsidyPlan.SubsidyPlanSchema.TENANT, tenant))
        .where(Predicates.lessOrEquals(SubsidyPlan.SubsidyPlanSchema.EFFECTIVE_START_TIME, effectiveDate))
        .where(Predicates.greaterOrEquals(SubsidyPlan.SubsidyPlanSchema.EFFECTIVE_END_TIME, effectiveDate))
        .build();

    if (!Objects.isNull(state)) {
      select.where(Predicates.in2(SubsidyPlan.SubsidyPlanSchema.STATE, state));
    }
    return jdbcTemplate.query(select, SUBSIDY_PLAN_TE_MAPPER);
  }
}
