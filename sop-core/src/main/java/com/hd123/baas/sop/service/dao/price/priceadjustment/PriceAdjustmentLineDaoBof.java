package com.hd123.baas.sop.service.dao.price.priceadjustment;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.SkuDefine;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustmentLine;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/11.
 */
@Repository
public class PriceAdjustmentLineDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceAdjustmentLine.class,
      PPriceAdjustmentLine.class).addConditionProcessor((condition, context) -> {
        if (condition == null) {
          return null;
        }
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.SKU_KEYWORD, condition.getOperation())) {
          String value = (String) condition.getParameter();
          return Predicates.or(Predicates.like(alias, PPriceAdjustmentLine.SKU_CODE, value),
              Predicates.like(alias, PPriceAdjustmentLine.SKU_NAME, value));
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.SKU_DEFINE, condition.getOperation())) {
          return Predicates.equals(alias, PPriceAdjustmentLine.SKU_DEFINE, condition.getParameter());
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.SKU_GROUP, condition.getOperation())) {
          return Predicates.equals(alias, PPriceAdjustmentLine.SKU_GROUP, condition.getParameter());
        }

        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.SKU_POSITION, condition.getOperation())) {
          return Predicates.equals(alias, PPriceAdjustmentLine.SKU_POSITION, condition.getParameter());
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.SKU_IN_PRICE_IS_NULL, condition.getOperation())) {
          return Predicates.equals(alias, PPriceAdjustmentLine.SKU_IN_PRICE, "0.0000");
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.SKU_BASE_PRICE_IS_NULL,
            condition.getOperation())) {
          return Predicates.or(Predicates.isNull(alias, PPriceAdjustmentLine.SKU_BASE_PRICE),
              Predicates.equals(alias, PPriceAdjustmentLine.SKU_BASE_PRICE, BigDecimal.ZERO));
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.INCREASE_TYPE_RULE_IS_NULL,
            condition.getOperation())) {
          return Predicates.or(Predicates.isNull(alias, PPriceAdjustmentLine.INCREASE_TYPE),
              Predicates.isNull(alias, PPriceAdjustmentLine.INCREASE_RULES));
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.SKU_GROUP_IS_NULL, condition.getOperation())) {
          return Predicates.isNull(alias, PPriceAdjustmentLine.SKU_GROUP);
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.SKU_POSITION_IS_NULL, condition.getOperation())) {
          return Predicates.isNull(alias, PPriceAdjustmentLine.SKU_POSITION);
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.INCREASE_TYPE, condition.getOperation())) {
          return Predicates.equals(alias, PPriceAdjustmentLine.INCREASE_TYPE, condition.getParameter());
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.INCREASE_TYPE_IS_NULL, condition.getOperation())) {
          return Predicates.isNull(alias, PPriceAdjustmentLine.INCREASE_RULES);
        }
        if (StringUtils.equalsIgnoreCase(PriceAdjustmentLine.Queries.EXSIT_COMPETIOR, condition.getOperation())) {
          SelectStatement statement = new SelectBuilder()
              .from(PPriceCompetitorLine.TABLE_NAME, PPriceCompetitorLine.TABLE_ALIAS)
              .where(Predicates.equals(PPriceCompetitorLine.TABLE_ALIAS, PPriceCompetitorLine.SKU_ID,
                  context.getPerzAlias(), PPriceAdjustmentLine.SKU_ID))
              .where(Predicates.equals(PPriceCompetitorLine.TABLE_ALIAS, PPriceCompetitorLine.OWNER,
                  context.getPerzAlias(), PPriceAdjustmentLine.OWNER))
              .build();
          if (Boolean.parseBoolean(Objects.toString(condition.getParameter()))) {
            return Predicates.exists(statement);
          }
          return Predicates.notExists(statement);
        }
        return null;
      }).build();

  private String getTableField(String tableAlias, String field) {
    return tableAlias + "." + field;
  }

  public QueryResult<PriceAdjustmentLine> query(String tenant, String owner, QueryDefinition qd) {
    qd.addByField(PriceAdjustmentLine.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PriceAdjustmentLine.Queries.OWNER, Cop.EQUALS, owner);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PriceAdjustmentLineMapper());
  }

  public PriceAdjustmentLine getLine(String tenant, String lineId) {
    SelectStatement select = new SelectBuilder().from(PPriceAdjustmentLine.TABLE_NAME)
        .select(PPriceAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustmentLine.UUID, lineId))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceAdjustmentLineMapper()));
  }

  public PriceAdjustmentLine get(String tenant, String owner, String uuid) {
    SelectStatement select = new SelectBuilder().from(PPriceAdjustmentLine.TABLE_NAME)
        .select(PPriceAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustmentLine.OWNER, owner))
        .where(Predicates.equals(PPriceAdjustmentLine.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceAdjustmentLineMapper()));
  }

  public PriceAdjustmentLine get(String tenant, String uuid) {
    SelectStatement select = new SelectBuilder().from(PPriceAdjustmentLine.TABLE_NAME)
        .select(PPriceAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustmentLine.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceAdjustmentLineMapper()));
  }

  public List<PriceAdjustmentLine> list(String tenant, String owner, Collection<String> uuids) {
    if (CollectionUtils.isEmpty(uuids)) {
      return new ArrayList<>();
    }
    SelectStatement select = new SelectBuilder().from(PPriceAdjustmentLine.TABLE_NAME)
        .select(PPriceAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustmentLine.OWNER, owner))
        .where(Predicates.in2(PPriceAdjustmentLine.UUID, uuids.toArray()))
        .build();
    return jdbcTemplate.query(select, new PriceAdjustmentLineMapper());
  }

  public List<PriceAdjustmentLine> listByGid(String tenant, String owner, String gid) {
    SelectStatement select = new SelectBuilder().from(PPriceAdjustmentLine.TABLE_NAME)
        .select(PPriceAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustmentLine.OWNER, owner))
        .where(Predicates.in2(PPriceAdjustmentLine.SKU_GID, gid))
        .build();
    return jdbcTemplate.query(select, new PriceAdjustmentLineMapper());
  }

  public List<PriceAdjustmentLine> list(String tenant, String owner) {
    SelectStatement select = new SelectBuilder().from(PPriceAdjustmentLine.TABLE_NAME)
        .select(PPriceAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustmentLine.OWNER, owner))
        .build();
    return jdbcTemplate.query(select, new PriceAdjustmentLineMapper());
  }

  public List<PriceAdjustmentLine> list(String tenant, Collection<String> owners) {
    SelectStatement select = new SelectBuilder().from(PPriceAdjustmentLine.TABLE_NAME)
        .select(PPriceAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.in2(PPriceAdjustmentLine.OWNER, owners.toArray()))
        .build();
    return jdbcTemplate.query(select, new PriceAdjustmentLineMapper());
  }

  public void update(String tenant, String owner, PriceAdjustmentLine line, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(line, "line");
    jdbcTemplate.update(buildUpdateStatement(tenant, owner, line));
  }

  public void batchUpdate(String tenant, String owner, List<PriceAdjustmentLine> lines) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notEmpty(lines, "lines");
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (PriceAdjustmentLine line : lines) {
      UpdateStatement updateStatement = buildUpdateStatement(tenant, owner, line);
      updater.add(updateStatement);
    }
    updater.update();
  }

  public void updateShopPrice(String tenant, String owner, String line, BigDecimal shopPrice) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(line, "line");

    UpdateStatement update = new UpdateBuilder().table(PPriceAdjustmentLine.TABLE_NAME)
        .addValue(PPriceAdjustmentLine.SKU_BASE_PRICE, shopPrice)
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustmentLine.OWNER, owner))
        .where(Predicates.equals(PPriceAdjustmentLine.UUID, line))
        .build();

    jdbcTemplate.update(update);
  }

  public void batchUpdate(String tenant, String owner, Collection<PriceAdjustmentLine> lines) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (PriceAdjustmentLine line : lines) {
      batchUpdater.add(buildUpdateStatement(tenant, owner, line));
    }
    batchUpdater.update();
  }

  private UpdateStatement buildUpdateStatement(String tenant, String owner, PriceAdjustmentLine line)
      throws BaasException {
    UpdateStatement update = new UpdateBuilder().table(PPriceAdjustmentLine.TABLE_NAME)
        .addValue(PPriceAdjustmentLine.SKU_ID, line.getSku().getId())
        .addValue(PPriceAdjustmentLine.SKU_GID, line.getSku().getGoodsGid())
        .addValue(PPriceAdjustmentLine.SKU_CODE, line.getSku().getCode())
        .addValue(PPriceAdjustmentLine.SKU_NAME, line.getSku().getName())
        .addValue(PPriceAdjustmentLine.SKU_QPC, line.getSku().getQpc())
        .addValue(PPriceAdjustmentLine.SKU_UNIT, line.getSku().getUnit())
        .addValue(PPriceAdjustmentLine.SKU_DEFINE, line.getSkuDefine().name())
        .addValue(PPriceAdjustmentLine.RAW, line.getRaw())
        .addValue(PPriceAdjustmentLine.AVE_WEEK_QTY,line.getAveWeekQty())

        .addValue(PPriceAdjustmentLine.SKU_IN_PRICE, line.getSkuInPrice())
        .addValue(PPriceAdjustmentLine.SKU_INIT_IN_PRICE, line.getSkuInitInPrice())
        .addValue(PPriceAdjustmentLine.SKU_BASE_PRICE, line.getSkuBasePrice())
        .addValue(PPriceAdjustmentLine.SKU_TOLERANCE_VALUE, line.getSkuToleranceValue())
        .addValue(PPriceAdjustmentLine.SKU_KV, line.getSkuKv())
        .addValue(PPriceAdjustmentLine.SKU_BV, line.getSkuBv())
        .addValue(PPriceAdjustmentLine.SKU_INCREASE_RATE, line.getSkuIncreaseRate())
        .addValue(PPriceAdjustmentLine.REMARK, line.getRemark())
        // 是否计算尾差
        .addValue(PPriceAdjustmentLine.CALC_TAIL_DIFF, line.getCalcTailDiff())

        .addValue(PPriceAdjustmentLine.SKU_GROUP, line.getSkuGroup())
        .addValue(PPriceAdjustmentLine.SKU_GROUP_NAME, line.getSkuGroupName())
        .addValue(PPriceAdjustmentLine.SKU_GROUP_TOLERANCE_VALUE, line.getSkuGroupToleranceValue())

        .addValue(PPriceAdjustmentLine.SKU_POSITION, line.getSkuPosition())
        .addValue(PPriceAdjustmentLine.SKU_POSITION_NAME, line.getSkuPositionName())
        .addValue(PPriceAdjustmentLine.SKU_POSITION_INCREASE_RATES,
            BaasJSONUtil.safeToJson(line.getSkuPositionIncreaseRates()))
        .addValue(PPriceAdjustmentLine.PRICE_RANGE_INCREASE_RATES,
            BaasJSONUtil.safeToJson(line.getPriceRangeIncreaseRates()))
        .addValue(PPriceAdjustmentLine.SKU_GRADE_INCREASE_RATES,
            BaasJSONUtil.safeToJson(line.getSkuGradeIncreaseRates()))
        .addValue(PPriceAdjustmentLine.INCREASE_TYPE,
            line.getIncreaseType() == null ? null : line.getIncreaseType().name())
        .addValue(PPriceAdjustmentLine.INCREASE_RULES, BaasJSONUtil.safeToJson(line.getIncreaseRules()))
        .addValue(PPriceAdjustmentLine.PRICE_GRADES, BaasJSONUtil.safeToJson(line.getPriceGrades()))
        .addValue(PPriceAdjustmentLine.HIGH_IN_PRICE, line.getHighInPrice())
        .addValue(PPriceAdjustmentLine.LOW_IN_PRICE, line.getLowInPrice())
        .addValue(PPriceAdjustmentLine.HIGH_BACK_GROSS_RATE, line.getHighBackGrossRate())
        .addValue(PPriceAdjustmentLine.LOW_BACK_GROSS_RATE, line.getLowBackGrossRate())
        .addValue(PPriceAdjustmentLine.HIGH_FRONT_GROSS_RATE, line.getHighFrontGrossRate())
        .addValue(PPriceAdjustmentLine.LOW_FRONT_GROSS_RATE, line.getLowFrontGrossRate())
        .addValue(PPriceAdjustmentLine.HIGH_MARKET_DIFF_RATE, line.getHighMarketDiffRate())
        .addValue(PPriceAdjustmentLine.LOW_MARKET_DIFF_RATE, line.getLowMarketDiffRate())
        .addValue(PPriceAdjustmentLine.HIGH_PRICE_FLOAT_RATE, line.getHighPriceFloatRate())
        .addValue(PPriceAdjustmentLine.LOW_PRICE_FLOAT_RATE, line.getLowPriceFloatRate())
        .addValue(PPriceAdjustmentLine.PRE_SKU_IN_PRICE, line.getPreSkuInPrice())
        .addValue(PPriceAdjustmentLine.PRE_SKU_INCREASE_RATE, line.getPreSkuIncreaseRate())
        .addValue(PPriceAdjustmentLine.PRE_PRICE_RANGE_INCREASE_RATES,
            BaasJSONUtil.safeToJson(line.getPrePriceRangeIncreaseRates()))
        .addValue(PPriceAdjustmentLine.PRE_PRICE_GRADES, BaasJSONUtil.safeToJson(line.getPrePriceGrades()))
        .addValue(PPriceAdjustmentLine.EXT, BaasJSONUtil.safeToJson(line.getExt()))
        .addValues(PEntity.forSaveModify(line))
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustmentLine.OWNER, owner))
        .where(Predicates.equals(PPriceAdjustmentLine.UUID, line.getUuid()))
        .build();
    return update;
  }

  public void batchInsert(String tenant, String owner, Collection<PriceAdjustmentLine> lines, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (PriceAdjustmentLine line : lines) {
      if (StringUtils.isBlank(line.getUuid())) {
        line.setUuid(UUID.randomUUID().toString());
      }
      InsertStatement insert = new InsertBuilder().table(PPriceAdjustmentLine.TABLE_NAME)
          .addValue(PPriceAdjustmentLine.TENANT, tenant)
          .addValue(PPriceAdjustmentLine.OWNER, owner)

          .addValue(PPriceAdjustmentLine.SKU_ID, line.getSku().getId())
          .addValue(PPriceAdjustmentLine.SKU_GID, line.getSku().getGoodsGid())
          .addValue(PPriceAdjustmentLine.SKU_CODE, line.getSku().getCode())
          .addValue(PPriceAdjustmentLine.SKU_NAME, line.getSku().getName())
          .addValue(PPriceAdjustmentLine.SKU_QPC, line.getSku().getQpc())
          .addValue(PPriceAdjustmentLine.SKU_UNIT, line.getSku().getUnit())
          .addValue(PPriceAdjustmentLine.SKU_DEFINE, line.getSkuDefine().name())
          .addValue(PPriceAdjustmentLine.RAW, line.getRaw())
          .addValue(PPriceAdjustmentLine.AVE_WEEK_QTY,line.getAveWeekQty())

          .addValue(PPriceAdjustmentLine.SKU_IN_PRICE, line.getSkuInPrice())
          .addValue(PPriceAdjustmentLine.SKU_INIT_IN_PRICE, line.getSkuInitInPrice())
          .addValue(PPriceAdjustmentLine.SKU_BASE_PRICE, line.getSkuBasePrice())
          .addValue(PPriceAdjustmentLine.SKU_TOLERANCE_VALUE, line.getSkuToleranceValue())
          .addValue(PPriceAdjustmentLine.SKU_KV, line.getSkuKv())
          .addValue(PPriceAdjustmentLine.SKU_BV, line.getSkuBv())
          .addValue(PPriceAdjustmentLine.SKU_INCREASE_RATE, line.getSkuIncreaseRate())

          .addValue(PPriceAdjustmentLine.SKU_GROUP, line.getSkuGroup())
          .addValue(PPriceAdjustmentLine.SKU_GROUP_NAME, line.getSkuGroupName())
          .addValue(PPriceAdjustmentLine.SKU_GROUP_TOLERANCE_VALUE, line.getSkuGroupToleranceValue())

          .addValue(PPriceAdjustmentLine.SKU_POSITION, line.getSkuPosition())
          .addValue(PPriceAdjustmentLine.SKU_POSITION_NAME, line.getSkuPositionName())
          .addValue(PPriceAdjustmentLine.SKU_POSITION_INCREASE_RATES,
              BaasJSONUtil.safeToJson(line.getSkuPositionIncreaseRates()))
          .addValue(PPriceAdjustmentLine.PRICE_RANGE_INCREASE_RATES,
              BaasJSONUtil.safeToJson(line.getPriceRangeIncreaseRates()))
          .addValue(PPriceAdjustmentLine.SKU_GRADE_INCREASE_RATES,
              BaasJSONUtil.safeToJson(line.getSkuGradeIncreaseRates()))
          .addValue(PPriceAdjustmentLine.INCREASE_TYPE,
              line.getIncreaseType() == null ? null : line.getIncreaseType().name())
          .addValue(PPriceAdjustmentLine.INCREASE_RULES, BaasJSONUtil.safeToJson(line.getIncreaseRules()))
          .addValue(PPriceAdjustmentLine.PRICE_GRADES, BaasJSONUtil.safeToJson(line.getPriceGrades()))
          // 是否计算尾差
          .addValue(PPriceAdjustmentLine.CALC_TAIL_DIFF, line.getCalcTailDiff())
          .addValue(PPriceAdjustmentLine.HIGH_IN_PRICE, line.getHighInPrice())
          .addValue(PPriceAdjustmentLine.LOW_IN_PRICE, line.getLowInPrice())
          .addValue(PPriceAdjustmentLine.HIGH_BACK_GROSS_RATE, line.getHighBackGrossRate())
          .addValue(PPriceAdjustmentLine.LOW_BACK_GROSS_RATE, line.getLowBackGrossRate())
          .addValue(PPriceAdjustmentLine.HIGH_FRONT_GROSS_RATE, line.getHighFrontGrossRate())
          .addValue(PPriceAdjustmentLine.LOW_FRONT_GROSS_RATE, line.getLowFrontGrossRate())
          .addValue(PPriceAdjustmentLine.HIGH_MARKET_DIFF_RATE, line.getHighMarketDiffRate())
          .addValue(PPriceAdjustmentLine.LOW_MARKET_DIFF_RATE, line.getLowMarketDiffRate())
          .addValue(PPriceAdjustmentLine.HIGH_PRICE_FLOAT_RATE, line.getHighPriceFloatRate())
          .addValue(PPriceAdjustmentLine.LOW_PRICE_FLOAT_RATE, line.getLowPriceFloatRate())
          .addValue(PPriceAdjustmentLine.PRE_SKU_IN_PRICE, line.getPreSkuInPrice())
          .addValue(PPriceAdjustmentLine.PRE_SKU_INCREASE_RATE, line.getPreSkuIncreaseRate())
          .addValue(PPriceAdjustmentLine.PRE_PRICE_RANGE_INCREASE_RATES,
              BaasJSONUtil.safeToJson(line.getPrePriceRangeIncreaseRates()))
          .addValue(PPriceAdjustmentLine.PRE_PRICE_GRADES, BaasJSONUtil.safeToJson(line.getPrePriceGrades()))
          .addValue(PPriceAdjustmentLine.EXT, BaasJSONUtil.safeToJson(line.getExt()))
          .addValues(PEntity.forSaveNew(line))
          .build();
      statements.add(insert);
    }
    batchUpdate(statements);
  }

  public List<PriceAdjustmentLine> list(String tenant, String owner, SkuDefine skuDefine) {
    Assert.notNull(tenant);
    Assert.notNull(owner);

    SelectStatement select = new SelectBuilder().from(PPriceAdjustmentLine.TABLE_NAME)
        .select(PPriceAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceAdjustmentLine.OWNER, owner))
        .where(Predicates.equals(PPriceAdjustmentLine.SKU_DEFINE, skuDefine.name()))
        .build();
    return jdbcTemplate.query(select, new PriceAdjustmentLineMapper());
  }
}
