package com.hd123.baas.sop.service.dao.price.gradeadjustment;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.gradeadjustment.PriceGradeAdjustment;
import com.hd123.baas.sop.service.api.price.gradeadjustment.PriceGradeAdjustmentState;
import com.hd123.baas.sop.service.dao.price.shopprice.PShopPriceGrade;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author zhengzewang on 2020/11/12.
 */
@Repository
public class PriceGradeAdjustmentDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceGradeAdjustment.class,
      PPriceGradeAdjustment.class).addConditionProcessor((condition, context) -> {
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(PriceGradeAdjustment.Queries.SHOP_EQUALS, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PPriceGradeAdjustmentShop.TABLE_NAME, PPriceGradeAdjustmentShop.TABLE_ALIAS) //
              .where(Predicates.equals(PPriceGradeAdjustmentShop.TABLE_ALIAS, PPriceGradeAdjustmentShop.SHOP, value)) //
              .where(Predicates.equals(PPriceGradeAdjustmentShop.TABLE_ALIAS, PPriceGradeAdjustmentShop.OWNER, alias,
                  PPriceGradeAdjustment.UUID))
              .where(Predicates.equals(PPriceGradeAdjustmentShop.TABLE_ALIAS, PPriceGradeAdjustmentShop.TENANT, alias,
                  PPriceGradeAdjustment.TENANT))
              .build();
          return Predicates.exists(select);
        }
        if (StringUtils.equalsIgnoreCase(PriceGradeAdjustment.Queries.SHOP_IN, condition.getOperation())) {
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PPriceGradeAdjustmentShop.TABLE_NAME, PPriceGradeAdjustmentShop.TABLE_ALIAS) //
              .where(Predicates.in(PPriceGradeAdjustmentShop.TABLE_ALIAS, PPriceGradeAdjustmentShop.SHOP,
                  condition.getParameters().toArray())) //
              .where(Predicates.equals(PPriceGradeAdjustmentShop.TABLE_ALIAS, PPriceGradeAdjustmentShop.OWNER, alias,
                  PPriceGradeAdjustment.UUID))
              .where(Predicates.equals(PPriceGradeAdjustmentShop.TABLE_ALIAS, PPriceGradeAdjustmentShop.TENANT, alias,
                  PPriceGradeAdjustment.TENANT))
              .build();
          return Predicates.exists(select);
        }
        if (StringUtils.equalsIgnoreCase(PriceGradeAdjustment.Queries.NOT_INT_SHOP_PRICE_GRADE,
            condition.getOperation())) {
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PShopPriceGrade.TABLE_NAME, PShopPriceGrade.TABLE_ALIAS) //
              .where(Predicates.equals(PShopPriceGrade.TABLE_ALIAS, PShopPriceGrade.SOURCE, alias,
                  PPriceGradeAdjustment.UUID))
              .where(Predicates.equals(PShopPriceGrade.TABLE_ALIAS, PShopPriceGrade.TENANT, alias,
                  PPriceGradeAdjustment.TENANT))
              .build();
          return Predicates.notExists(select);
        }
        if (StringUtils.equalsIgnoreCase(PriceGradeAdjustment.Queries.FLOW_NO, condition.getOperation())) {
          return Predicates.equals(PPriceGradeAdjustment.FLOW_NO, condition.getParameter());
        }
        return null;
      }).build();

  public QueryResult<PriceGradeAdjustment> query(String tenant, QueryDefinition qd) {
    qd.addByField(PriceGradeAdjustment.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PriceGradeAdjustmentMapper());
  }

  public PriceGradeAdjustment get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectStatement select = new SelectBuilder().from(PPriceGradeAdjustment.TABLE_NAME)
        .select(PPriceGradeAdjustment.allColumns())
        .where(Predicates.equals(PPriceGradeAdjustment.TENANT, tenant))
        .where(Predicates.equals(PPriceGradeAdjustment.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceGradeAdjustmentMapper()));
  }

  public void insert(String tenant, PriceGradeAdjustment adjustment, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(adjustment, "adjustment");
    if (StringUtils.isBlank(adjustment.getUuid())) {
      adjustment.setUuid(UUID.randomUUID().toString());
    }
    adjustment.setCreateInfo(operateInfo);
    adjustment.setLastModifyInfo(operateInfo);
    InsertStatement insert = new InsertBuilder().table(PPriceGradeAdjustment.TABLE_NAME)
        .addValues(PPriceGradeAdjustment.forSaveNew(adjustment))
        .addValue(PPriceGradeAdjustment.FLOW_NO, adjustment.getFlowNo())
        .addValue(PPriceGradeAdjustment.TENANT, adjustment.getTenant())
        .addValue(PPriceGradeAdjustment.ORG_ID, adjustment.getOrgId())
        .addValue(PPriceGradeAdjustment.EFFECTIVE_START_DATE, adjustment.getEffectiveStartDate())
        .addValue(PPriceGradeAdjustment.STATE, adjustment.getState().name())
        .build();
    jdbcTemplate.update(insert);
  }

  public void update(String tenant, PriceGradeAdjustment adjustment, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(adjustment, "adjustment");
    adjustment.setLastModifyInfo(operateInfo);
    UpdateStatement update = new UpdateBuilder().table(PPriceGradeAdjustment.TABLE_NAME)
        .addValues(PPriceGradeAdjustment.forSaveModify(adjustment))
        .addValue(PPriceGradeAdjustment.FLOW_NO, adjustment.getFlowNo())
        .addValue(PPriceGradeAdjustment.EFFECTIVE_START_DATE, adjustment.getEffectiveStartDate())
        .addValue(PPriceGradeAdjustment.STATE, adjustment.getState().name())
        .where(Predicates.equals(PPriceGradeAdjustment.TENANT, tenant))
        .where(Predicates.equals(PPriceGradeAdjustment.UUID, adjustment.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void changeState(String tenant, String uuid, PriceGradeAdjustmentState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(state, "state");
    UpdateStatement update = new UpdateBuilder().table(PPriceGradeAdjustment.TABLE_NAME)
        .addValue(PPriceGradeAdjustment.STATE, state.name())
        .addValues(PPriceGradeAdjustment.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PPriceGradeAdjustment.TENANT, tenant))
        .where(Predicates.equals(PPriceGradeAdjustment.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  public void cancel(String tenant, String uuid, String reason, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(reason, "取消原因");
    UpdateStatement update = new UpdateBuilder().table(PPriceGradeAdjustment.TABLE_NAME)
        .addValue(PPriceGradeAdjustment.STATE, PriceGradeAdjustmentState.CANCELED.name())
        .addValue(PPriceGradeAdjustment.REASON, reason)
        .addValues(PPriceGradeAdjustment.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PPriceGradeAdjustment.TENANT, tenant))
        .where(Predicates.equals(PPriceGradeAdjustment.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }
}
