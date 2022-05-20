package com.hd123.baas.sop.service.dao.price.priceadjustment;

import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.utils.OrgUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustment;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustmentState;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author zhengzewang on 2020/11/11.
 */
@Repository
public class PriceAdjustmentDaoBof extends BofBaseDao {

  private static QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceAdjustment.class,
      PPriceAdjustment.class).addConditionProcessor((condition, context) -> {
        if (condition == null) {
          return null;
        }
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(PriceAdjustment.Queries.SKU_KEYWORD, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PPriceAdjustmentLine.TABLE_NAME, PPriceAdjustmentLine.TABLE_ALIAS) //
              .where(
                  Predicates.or(Predicates.like(PPriceAdjustmentLine.TABLE_ALIAS, PPriceAdjustmentLine.SKU_CODE, value),
                      Predicates.like(PPriceAdjustmentLine.TABLE_ALIAS, PPriceAdjustmentLine.SKU_NAME, value))) //
              .where(Predicates.equals(PPriceAdjustmentLine.TABLE_ALIAS, PPriceAdjustmentLine.OWNER, alias,
                  PPriceAdjustment.UUID))
              .where(Predicates.equals(PPriceAdjustmentLine.TABLE_ALIAS, PPriceAdjustmentLine.TENANT, alias,
                  PPriceAdjustment.TENANT))
              .build();
          return Predicates.exists(select);
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustment.Queries.SKU_BASE_PRICE_IS_NULL, condition.getOperation())) {
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PPriceAdjustmentLine.TABLE_NAME, PPriceAdjustmentLine.TABLE_ALIAS) //
              .where(Predicates.isNull(PPriceAdjustmentLine.TABLE_ALIAS, PPriceAdjustmentLine.SKU_BASE_PRICE)) //
              .where(Predicates.equals(PPriceAdjustmentLine.TABLE_ALIAS, PPriceAdjustmentLine.OWNER, alias,
                  PPriceAdjustment.UUID))
              .where(Predicates.equals(PPriceAdjustmentLine.TABLE_ALIAS, PPriceAdjustmentLine.TENANT, alias,
                  PPriceAdjustment.TENANT))
              .build();
          return Predicates.exists(select);
        }
        return null;
      }).build();

  private String getTableField(String tableAlias, String field) {
    return tableAlias + "." + field;
  }

  public QueryResult<PriceAdjustment> query(String tenant, QueryDefinition qd) {
    qd.addByField(PriceAdjustment.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PriceAdjustmentMapper());
  }

  public List<PriceAdjustment> list(String tenant, String orgId, PriceAdjustmentState state, Date effectiveStartDate) {

    SelectBuilder select = new SelectBuilder().from(PPriceAdjustment.TABLE_NAME, PPriceAdjustment.TABLE_ALIAS);
    select.where(Predicates.equals(PPriceAdjustment.TENANT, tenant));
    select.where(Predicates.equals(PPriceAdjustment.STATE, state.name()));
    select.where(Predicates.greaterOrEquals(PPriceAdjustment.EFFECTIVE_START_DATE, effectiveStartDate));

    if (OrgUtils.isNotAllScope(tenant, orgId)) {
      select.where(Predicates.equals(PPriceAdjustment.ORG_ID, orgId));
    }

    return jdbcTemplate.query(select.build(), new PriceAdjustmentMapper());
  }

  public void insert(String tenant, PriceAdjustment adjustment, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(adjustment, "adjustment");
    adjustment.setCreateInfo(operateInfo);
    adjustment.setLastModifyInfo(operateInfo);
    InsertStatement insert = new InsertBuilder().table(PPriceAdjustment.TABLE_NAME)
        .addValues(PPriceAdjustment.forSaveNew(adjustment))
        .addValue(PPriceAdjustment.FLOW_NO, adjustment.getFlowNo())
        .addValue(PPriceAdjustment.ORG_ID, adjustment.getOrgId())
        .addValue(PPriceAdjustment.TENANT, adjustment.getTenant())
        .addValue(PPriceAdjustment.EFFECTIVE_START_DATE, adjustment.getEffectiveStartDate())
        .addValue(PPriceAdjustment.STATE, adjustment.getState().name())
        .build();
    jdbcTemplate.update(insert);
  }

  public void update(String tenant, PriceAdjustment adjustment, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(adjustment, "adjustment");
    adjustment.setLastModifyInfo(operateInfo);
    UpdateStatement update = new UpdateBuilder().table(PPriceAdjustment.TABLE_NAME)
        .addValues(PPriceAdjustment.forSaveModify(adjustment))
        .addValue(PPriceAdjustment.FLOW_NO, adjustment.getFlowNo())
        .addValue(PPriceAdjustment.EFFECTIVE_START_DATE, adjustment.getEffectiveStartDate())
        .addValue(PPriceAdjustment.STATE, adjustment.getState().name())
        .where(Predicates.equals(PPriceAdjustment.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustment.UUID, adjustment.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void changeState(String tenant, String uuid, PriceAdjustmentState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(state, "state");
    UpdateStatement update = new UpdateBuilder().table(PPriceAdjustment.TABLE_NAME)
        .addValue(PPriceAdjustment.STATE, state.name())
        .addValues(PPriceAdjustment.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PPriceAdjustment.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustment.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  public PriceAdjustment get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectStatement select = new SelectBuilder().from(PPriceAdjustment.TABLE_NAME)
        .select(PPriceAdjustment.allColumns())
        .where(Predicates.equals(PPriceAdjustment.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustment.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceAdjustmentMapper()));
  }

  public void cancel(String tenant, String uuid, String reason, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(reason, "取消原因");
    Assert.hasText(tenant, "tenant");

    UpdateStatement update = new UpdateBuilder().table(PPriceAdjustment.TABLE_NAME)
        .addValue(PPriceAdjustment.STATE, PriceAdjustmentState.CANCELED.name())
        .addValue(PPriceAdjustment.REASON, reason)
        .addValues(PPriceAdjustment.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PPriceAdjustment.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustment.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  public PriceAdjustment getLastEffective(String tenant, String orgId, Date effectiveStartDate) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(effectiveStartDate, "effectiveStartDate");
    SelectStatement select = new SelectBuilder().from(PPriceAdjustment.TABLE_NAME)
        .select(PPriceAdjustment.allColumns())
        .where(Predicates.equals(PPriceAdjustment.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustment.ORG_ID, orgId))
        .where(Predicates.in2(PPriceAdjustment.STATE, PriceAdjustmentState.PUBLISHED.name(),PriceAdjustmentState.EXPIRED.name()))
        .where(Predicates.less(PPriceAdjustment.EFFECTIVE_START_DATE, effectiveStartDate))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceAdjustmentMapper()));
  }

  public PriceAdjustment getEffective(String tenant, String orgId, Date effectiveStartDate) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(effectiveStartDate, "effectiveStartDate");
    SelectStatement select = new SelectBuilder().from(PPriceAdjustment.TABLE_NAME)
            .select(PPriceAdjustment.allColumns())
            .where(Predicates.equals(PPriceAdjustment.TENANT, tenant))
            .where(Predicates.equals(PPriceAdjustment.ORG_ID, orgId))
            .where(Predicates.in2(PPriceAdjustment.STATE, PriceAdjustmentState.PUBLISHED.name(),PriceAdjustmentState.EXPIRED.name()))
            .where(Predicates.lessOrEquals(PPriceAdjustment.EFFECTIVE_START_DATE, effectiveStartDate))
            .orderBy(PPriceAdjustment.FLOW_NO,false)
            .build();
    return getFirst(jdbcTemplate.query(select, new PriceAdjustmentMapper()));
  }
}
