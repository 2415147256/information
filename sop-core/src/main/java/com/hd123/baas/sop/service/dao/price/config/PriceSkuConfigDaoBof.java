package com.hd123.baas.sop.service.dao.price.config;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;
import static com.hd123.rumba.commons.jdbc.sql.Predicates.or;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfig;
import com.hd123.baas.sop.service.dao.taskgroup.PTaskGroup;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Repository
@Slf4j
public class PriceSkuConfigDaoBof extends BofBaseDao {

  private static final PriceSkuConfigMapper PRICE_SKU_CONFIG_MAPPER = new PriceSkuConfigMapper();

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceSku.class, PPriceSku.class)
      .addConditionProcessor((condition, context) -> {
        if (condition == null) {
          return null;
        }
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(PriceSku.Queries.SKU_KEYWORD, condition.getOperation())) {
          return or(like(alias, PPriceSku.CODE, condition.getParameter()),
              like(alias, PPriceSku.NAME, condition.getParameter()));
        }
        return null;
      })
      .build();

  private QueryProcessor QUERY_PROCESSOR_CONFIG = new QueryProcessorBuilder(PriceSkuConfig.class, PPriceSkuConfig.class)
      .addConditionProcessor((condition, context) -> {
        if (condition == null) {
          return null;
        }
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(PriceSkuConfig.Queries.TOLERANCE_BETWEEN, condition.getOperation())) {
          List<Object> parameters = condition.getParameters();
          if (parameters != null && parameters.size() == 2) {
            Object left = parameters.get(0);
            Object right = parameters.get(1);
            if (left == null && right == null) {
              return null;
            }
            if (left == null) {
              return Predicates.lessOrEquals(alias, PPriceSkuConfig.TOLERANCE_VALUE, right);
            } else if (right == null) {
              return Predicates.greaterOrEquals(alias, PPriceSkuConfig.TOLERANCE_VALUE, left);
            } else {
              return Predicates.between(alias, PPriceSkuConfig.TOLERANCE_VALUE, left, right);
            }
          }
        }
        if (StringUtils.equalsIgnoreCase(PriceSkuConfig.Queries.SKU_POSITION_IS_NULL, condition.getOperation())) {
          return Predicates.isNull(alias, PPriceSkuConfig.SKU_POSITION);
        }
        if (StringUtils.equalsIgnoreCase(PriceSkuConfig.Queries.INCREASERATE_IS_NULL, condition.getOperation())) {
          return Predicates.isNull(alias, PPriceSkuConfig.INCREASE_RATE);
        }
        return null;
      })
      .build();

  public QueryResult<PriceSkuConfig> querySkuConfig(String tenant, QueryDefinition qd) {
    qd.addByField(PriceSkuConfig.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR_CONFIG.process(qd);
    return executor.query(select, new PriceSkuConfigMapper());
  }

  public QueryResult<PriceSku> query(String tenant, QueryDefinition qd) {
    qd.addByField(PriceSku.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PriceSku.Queries.DELETED, Cop.EQUALS, 0);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PriceSkuMapper());
  }

  public PriceSku getSku(String tenant, String orgId, String skuId) {
    Assert.hasText(tenant, "租户");
    Assert.hasText(skuId, "skuId");
    SelectStatement select = new SelectBuilder().from(PPriceSku.TABLE_NAME)
        .select(PPriceSku.allColumns())
        .where(Predicates.equals(PPriceSku.TENANT, tenant))
        .where(Predicates.equals(PPriceSku.ID, skuId))
        .where(Predicates.equals(PPriceSku.ORG_ID, orgId))
        .where(Predicates.equals(PPriceSku.DELETED, 0))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceSkuMapper()));
  }

  public List<PriceSku> getSkus(String tenant, String orgId, Collection<String> skuIds) {
    Assert.hasText(tenant, "租户");
    if (CollectionUtils.isEmpty(skuIds)) {
      return new ArrayList<>();
    }
    SelectStatement select = new SelectBuilder().from(PPriceSku.TABLE_NAME)
        .select(PPriceSku.allColumns())
        .where(Predicates.equals(PPriceSku.TENANT, tenant))
        .where(Predicates.in2(PPriceSku.ID, skuIds.toArray()))
        .where(Predicates.in2(PPriceSku.ORG_ID, orgId))
        .where(Predicates.equals(PPriceSku.DELETED, 0))
        .build();
    return jdbcTemplate.query(select, new PriceSkuMapper());
  }

  public void batchInsert(String tenant, Collection<PriceSkuConfig> configs, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    if (CollectionUtils.isEmpty(configs)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (PriceSkuConfig config : configs) {
      if (StringUtils.isBlank(config.getUuid())) {
        config.setUuid(UUID.randomUUID().toString());
      }
      statements.add(buildInsertStatement(tenant, config, operateInfo));
    }
    batchUpdate(statements);
  }

  public void insert(String tenant, PriceSkuConfig config, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    if (StringUtils.isBlank(config.getUuid())) {
      config.setUuid(UUID.randomUUID().toString());
    }
    jdbcTemplate.update(buildInsertStatement(tenant, config, operateInfo));
  }

  private InsertStatement buildInsertStatement(String tenant, PriceSkuConfig config, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(config, "config");
    Assert.notNull(config, config.getOrgId());
    Assert.notNull(operateInfo, "operateInfo");
    config.setCreateInfo(operateInfo);
    config.setLastModifyInfo(operateInfo);
    return new InsertBuilder().table(PPriceSkuConfig.TABLE_NAME)
        .addValue(PPriceSkuConfig.TENANT, tenant)
        .addValue(PPriceSkuConfig.ORG_ID, config.getOrgId())
        .addValue(PPriceSkuConfig.SKU_ID, config.getSku().getId())
        .addValue(PPriceSkuConfig.TOLERANCE_VALUE, config.getToleranceValue())
        .addValue(PPriceSkuConfig.KV, config.getKv())
        .addValue(PPriceSkuConfig.BV, config.getBv())
        .addValue(PPriceSkuConfig.INCREASE_RATE, config.getIncreaseRate())
        .addValue(PPriceSkuConfig.CALC_TAIL_DIFF, config.getCalcTailDiff())
        .addValue(PPriceSkuConfig.SKU_POSITION, config.getSkuPosition())
        .addValue(PPriceSkuConfig.HIGH_IN_PRICE, config.getHighInPrice())
        .addValue(PPriceSkuConfig.LOW_IN_PRICE, config.getLowInPrice())
        .addValue(PPriceSkuConfig.HIGH_BACK_GROSS_RATE, config.getHighBackGrossRate())
        .addValue(PPriceSkuConfig.LOW_BACK_GROSS_RATE, config.getLowBackGrossRate())
        .addValue(PPriceSkuConfig.HIGH_FRONT_GROSS_RATE, config.getHighFrontGrossRate())
        .addValue(PPriceSkuConfig.LOW_FRONT_GROSS_RATE, config.getLowFrontGrossRate())
        .addValue(PPriceSkuConfig.HIGH_MARKET_DIFF_RATE, config.getHighMarketDiffRate())
        .addValue(PPriceSkuConfig.LOW_MARKET_DIFF_RATE, config.getLowMarketDiffRate())
        .addValue(PPriceSkuConfig.HIGH_PRICE_FLOAT_RATE, config.getHighPriceFloatRate())
        .addValue(PPriceSkuConfig.LOW_PRICE_FLOAT_RATE, config.getLowPriceFloatRate())
        .addValue(PPriceSkuConfig.INCREASE_TYPE, BaasJSONUtil.safeToJson(config.getIncreaseRules()))
        .addValue(PPriceSkuConfig.EXT, BaasJSONUtil.safeToJson(config.getExt()))
        .addValues(PStandardEntity.forSaveNew(config))
        .build();
  }

  private UpdateStatement buildUpdateStatement(String tenant, PriceSkuConfig config, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(config.getOrgId(), "orgId");
    Assert.notNull(config, "商品基本配置");
    Assert.notNull(config.getUuid(), "商品基本配置id");
    Assert.notNull(config.getSku(), "商品");
    Assert.notNull(config.getSku().getId(), "商品ID");
    config.setLastModifyInfo(operateInfo);

    return new UpdateBuilder().table(PPriceSkuConfig.TABLE_NAME)
        .addValues(PStandardEntity.forSaveModify(config))
        .addValue(PPriceSkuConfig.SKU_ID, config.getSku().getId())
        .addValue(PPriceSkuConfig.TOLERANCE_VALUE, config.getToleranceValue())
        .addValue(PPriceSkuConfig.KV, config.getKv())
        .addValue(PPriceSkuConfig.BV, config.getBv())
        .addValue(PPriceSkuConfig.INCREASE_RATE, config.getIncreaseRate())
        .addValue(PPriceSkuConfig.CALC_TAIL_DIFF, config.getCalcTailDiff())
        .addValue(PPriceSkuConfig.SKU_POSITION, config.getSkuPosition())
        .addValue(PPriceSkuConfig.HIGH_IN_PRICE, config.getHighInPrice())
        .addValue(PPriceSkuConfig.LOW_IN_PRICE, config.getLowInPrice())
        .addValue(PPriceSkuConfig.HIGH_BACK_GROSS_RATE, config.getHighBackGrossRate())
        .addValue(PPriceSkuConfig.LOW_BACK_GROSS_RATE, config.getLowBackGrossRate())
        .addValue(PPriceSkuConfig.HIGH_FRONT_GROSS_RATE, config.getHighFrontGrossRate())
        .addValue(PPriceSkuConfig.LOW_FRONT_GROSS_RATE, config.getLowFrontGrossRate())
        .addValue(PPriceSkuConfig.HIGH_MARKET_DIFF_RATE, config.getHighMarketDiffRate())
        .addValue(PPriceSkuConfig.LOW_MARKET_DIFF_RATE, config.getLowMarketDiffRate())
        .addValue(PPriceSkuConfig.HIGH_PRICE_FLOAT_RATE, config.getHighPriceFloatRate())
        .addValue(PPriceSkuConfig.LOW_PRICE_FLOAT_RATE, config.getLowPriceFloatRate())
        .addValue(PPriceSkuConfig.INCREASE_TYPE,
            config.getIncreaseType() != null ? config.getIncreaseType().name() : null)
        .addValue(PPriceSkuConfig.INCREASE_RULES, BaasJSONUtil.safeToJson(config.getIncreaseRules()))
        .addValue(PPriceSkuConfig.EXT, BaasJSONUtil.safeToJson(config.getExt()))
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PTaskGroup.UUID, config.getUuid()))
        .build();
  }

  /**
   * 根据商品id删除
   *
   * @param tenant
   *          租户
   * @param skuIds
   *          商品code集合
   */
  public void batchDeleteBySkuIds(String tenant, Collection<String> skuIds) {
    Assert.notNull(tenant, "租户");
    if (CollectionUtils.isEmpty(skuIds)) {
      return;
    }
    DeleteStatement delete = new DeleteBuilder().table(PPriceSkuConfig.TABLE_NAME)
        .where(Predicates.equals(PPriceSkuConfig.TENANT, tenant))
        .where(Predicates.in2(PPriceSkuConfig.SKU_ID, skuIds.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  /**
   * 根据商品code删除
   * 
   * @param tenant
   *          租户
   * @param skuCodes
   *          商品code集合
   */
  public void batchDeleteBySkuCodes(String tenant, Collection<String> skuCodes) {
    Assert.notNull(tenant, "租户");
    if (CollectionUtils.isEmpty(skuCodes)) {
      return;
    }
    DeleteStatement delete = new DeleteBuilder().table(PPriceSkuConfig.TABLE_NAME)
        .where(Predicates.equals(PPriceSkuConfig.TENANT, tenant))
        .where(Predicates.in2(PPriceSkuConfig.SKU_ID,
            new SelectBuilder().select(PPriceSku.ID)
                .from(PPriceSku.TABLE_NAME)
                .where(Predicates.in2(PPriceSku.CODE, skuCodes.toArray()))
                .build()))
        .build();
    jdbcTemplate.update(delete);
  }

  public void batchUpdate(String tenant, Collection<PriceSkuConfig> configs, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(configs)) {
      return;
    }
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (PriceSkuConfig config : configs) {
      batchUpdater.add(buildUpdateStatement(tenant, config, operateInfo));
    }
    batchUpdater.update();
  }

  public int update(String tenant, PriceSkuConfig config, OperateInfo operateInfo) throws BaasException {
    return jdbcTemplate.update(buildUpdateStatement(tenant, config, operateInfo));
  }

  public PriceSkuConfig getBySkuId(String tenant, String orgId, String skuId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(skuId, "商品id");
    SelectStatement select = new SelectBuilder().from(PPriceSkuConfig.TABLE_NAME)
        .select(PPriceSkuConfig.allColumns())
        .where(Predicates.equals(PPriceSkuConfig.TENANT, tenant))
        .where(Predicates.equals(PPriceSkuConfig.SKU_ID, skuId))
        .where(Predicates.equals(PPriceSkuConfig.ORG_ID, orgId))
        .build();
    return getFirst(jdbcTemplate.query(select, PRICE_SKU_CONFIG_MAPPER));
  }

  public List<PriceSkuConfig> listBySkuIds(String tenant, String orgId, Collection<String> skuIds) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(orgId, "orgId");
    if (CollectionUtils.isEmpty(skuIds)) {
      return new ArrayList<>();
    }
    SelectStatement select = new SelectBuilder().from(PPriceSkuConfig.TABLE_NAME)
        .select(PPriceSkuConfig.allColumns())
        .where(Predicates.equals(PPriceSkuConfig.TENANT, tenant))
        .where(Predicates.equals(PPriceSkuConfig.ORG_ID, orgId))
        .where(Predicates.in2(PPriceSkuConfig.SKU_ID, skuIds.toArray()))
        .build();
    return jdbcTemplate.query(select, PRICE_SKU_CONFIG_MAPPER);
  }

}
