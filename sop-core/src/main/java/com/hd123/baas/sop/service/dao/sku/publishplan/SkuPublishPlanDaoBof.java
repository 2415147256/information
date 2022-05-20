package com.hd123.baas.sop.service.dao.sku.publishplan;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.and;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlan;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanScope;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanState;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.Expr;
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
import com.hd123.rumba.commons.util.converter.EnumConverters;

/**
 * 商品上下架方案(SkuPublishPlan)表数据库访问层
 *
 * @author liuhaoxin
 * @since 2021-11-24 16:06:31
 */
@Repository
public class SkuPublishPlanDaoBof extends BofBaseDao {

  private static final TEMapper<SkuPublishPlan> MAPPER = TEMapperBuilder
      .of(SkuPublishPlan.class, SkuPublishPlan.Schema.class)
      .map("state", SkuPublishPlan.Schema.STATE, EnumConverters.toString(SkuPublishPlanState.class), EnumConverters.toEnum(SkuPublishPlanState.class))
      .map("ext", SkuPublishPlan.Schema.EXT, new ObjectNodeToExtConver(), new ExtToObjectNodeConver())
      .primaryKey(SkuPublishPlan.Schema.UUID)
      .build();

  private QueryConditionProcessor getScopeIdConditionProcessor() {
    return (condition, context) -> {
      if (condition == null) {
        return null;
      }
      if (!StringUtils.equalsIgnoreCase(SkuPublishPlan.Queries.SCOPE_ID, condition.getOperation())) {
        return null;
      }
      String alias = context.getPerzAlias();
      String scopeId = (String) condition.getParameter();
      SelectStatement selectStatement = new SelectBuilder().select(SkuPublishPlanScope.Schema.OWNER)
          .from(context.getDatabase(), SkuPublishPlanScope.Schema.TABLE_NAME, SkuPublishPlanScope.Schema.TABLE_ALIAS)
          .where(
              Predicates.equals(getTableField(SkuPublishPlanScope.Schema.TABLE_ALIAS, SkuPublishPlanScope.Schema.OWNER),
                  Expr.valueOf(getTableField(alias, SkuPublishPlan.Schema.UUID))))
          .where(Predicates.or(
              Predicates.equals(
                  getTableField(SkuPublishPlanScope.Schema.TABLE_ALIAS, SkuPublishPlanScope.Schema.OPTION_UUID), "*"),
              Predicates.equals(
                  getTableField(SkuPublishPlanScope.Schema.TABLE_ALIAS, SkuPublishPlanScope.Schema.OPTION_UUID),
                  scopeId)))
          .build();
      return and(Predicates.in(alias, SkuPublishPlan.Schema.UUID, selectStatement));
    };
  }

  private QueryConditionProcessor getKeywordConditionProcessor() {
    return (condition, context) -> {
      if (condition == null) {
        return null;
      }
      if (!StringUtils.equalsIgnoreCase(SkuPublishPlan.Queries.KEYWORD, condition.getOperation())) {
        return null;
      }
      String alias = context.getPerzAlias();
      String keyword = (String) condition.getParameter();

      SelectStatement selectStatement = new SelectBuilder().select(SkuPublishPlan.Schema.UUID)
          .from(context.getDatabase(), SkuPublishPlan.Schema.TABLE_NAME, SkuPublishPlan.Schema.TABLE_ALIAS)
          .where(Predicates.equals(getTableField(SkuPublishPlan.Schema.TABLE_ALIAS, SkuPublishPlan.Schema.UUID),
              Expr.valueOf(getTableField(alias, SkuPublishPlan.Schema.UUID))))
          .where(Predicates.or(Predicates.like(SkuPublishPlan.Schema.TABLE_ALIAS, SkuPublishPlan.Schema.NAME, keyword),
              Predicates.like(SkuPublishPlan.Schema.TABLE_ALIAS, SkuPublishPlan.Schema.FLOW_NO, keyword)))
          .build();
      return and(Predicates.in(alias, SkuPublishPlan.Schema.UUID, selectStatement));
    };
  }

  private String getTableField(String tableAlias, String field) {
    return tableAlias + "." + field;
  }

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuPublishPlan.class,
      SkuPublishPlan.Schema.class).addConditionProcessor(getScopeIdConditionProcessor())
          .addConditionProcessor(getKeywordConditionProcessor())
          .build();

  public void insert(String tenant, SkuPublishPlan skuPublishPlan, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(skuPublishPlan, "skuPublishPlan");

    skuPublishPlan.setTenant(tenant);
    skuPublishPlan.setCreateInfo(operateInfo);
    skuPublishPlan.setLastModifyInfo(operateInfo);
    InsertStatement insert = new InsertBuilder().table(SkuPublishPlan.Schema.TABLE_NAME)
        .values(MAPPER.forInsert(skuPublishPlan, true))
        .build();
    jdbcTemplate.update(insert);
  }

  public void delete(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    DeleteStatement deleteStatement = new DeleteBuilder().table(SkuPublishPlan.Schema.TABLE_NAME)
        .where(Predicates.equals(SkuPublishPlan.Schema.TENANT, tenant))
        .where(Predicates.equals(SkuPublishPlan.Schema.UUID, uuid))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public void update(String tenant, SkuPublishPlan skuPublishPlan, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(skuPublishPlan, "skuPublishPlan");
    Assert.hasText(skuPublishPlan.getUuid());

    skuPublishPlan.setLastModifyInfo(operateInfo);
    skuPublishPlan.setTenant(tenant);
    UpdateStatement update = new UpdateBuilder().table(SkuPublishPlan.Schema.TABLE_NAME)
        .setValues(MAPPER.forUpdate(skuPublishPlan, true))
        .where(Predicates.equals(SkuPublishPlan.Schema.TENANT, tenant))
        .where(Predicates.equals(SkuPublishPlan.Schema.UUID, skuPublishPlan.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void updateState(String tenant, String uuid, String state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid);

    UpdateStatement update = new UpdateBuilder().table(SkuPublishPlan.Schema.TABLE_NAME)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .addValue(SkuPublishPlan.Schema.STATE, state)
        .where(Predicates.equals(SkuPublishPlan.Schema.TENANT, tenant))
        .where(Predicates.equals(SkuPublishPlan.Schema.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }
  public SkuPublishPlan get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().from(SkuPublishPlan.Schema.TABLE_NAME)
        .where(Predicates.equals(SkuPublishPlan.Schema.TENANT, tenant),
            Predicates.equals(SkuPublishPlan.Schema.UUID, uuid))
        .build();
    return getFirst(select, MAPPER);
  }

  public QueryResult<SkuPublishPlan> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(SkuPublishPlan.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, MAPPER);
  }

  public List<SkuPublishPlan> listByState(String tenant, String ordId, String state) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(ordId, "ordId");
    Assert.hasText(state, "state");

    SelectStatement select = new SelectBuilder().from(SkuPublishPlan.Schema.TABLE_NAME)
        .where(Predicates.equals(SkuPublishPlan.Schema.TENANT, tenant),
            Predicates.equals(SkuPublishPlan.Schema.ORG_ID, ordId),
            Predicates.equals(SkuPublishPlan.Schema.STATE, state))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public List<SkuPublishPlan> listByStates(String tenant, String... state) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(state, "state");

    SelectStatement select = new SelectBuilder().from(SkuPublishPlan.Schema.TABLE_NAME)
        .where(Predicates.equals(SkuPublishPlan.Schema.TENANT, tenant),
            Predicates.in2(SkuPublishPlan.Schema.STATE, state))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public void updateStateByUuid(String tenant, List<String> uuids, String state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuids, "uuids");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (String uuid : uuids) {
      UpdateStatement update = new UpdateBuilder().table(SkuPublishPlan.Schema.TABLE_NAME)
          .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
          .addValue(SkuPublishPlan.Schema.STATE, state)
          .where(Predicates.equals(SkuPublishPlan.Schema.TENANT, tenant))
          .where(Predicates.equals(SkuPublishPlan.Schema.UUID, uuid))
          .build();
      updater.add(update);
    }
    updater.update();
  }
}
