package com.hd123.baas.sop.service.dao.price.pricepromotion;

import java.util.*;

import com.hd123.baas.sop.service.api.price.pricepromotion.ConflictPromotionLine;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionLine;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionState;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/13.
 */
@Repository
public class PricePromotionLineDaoBof extends BofBaseDao {

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PricePromotionLine.class,
      PPricePromotionLine.class).build();

  public QueryResult<PricePromotionLine> query(String tenant, String owner, QueryDefinition qd) {
    qd.addByField(PricePromotionLine.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PricePromotionLine.Queries.OWNER, Cop.EQUALS, owner);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PricePromotionLineMapper());
  }

  public long queryCount(String tenant, String owner, QueryDefinition qd) {
    qd.addByField(PricePromotionLine.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PricePromotionLine.Queries.OWNER, Cop.EQUALS, owner);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    selectStatement.getSelectClause().getFields().clear();
    selectStatement.select("count(1)");
    QueryResult<Long> result = executor.query(selectStatement, new SingleColumnRowMapper<>(Long.class));
    if (result.getRecordCount() == 0) {
      return 0;
    }
    return result.getRecords().get(0);
  }

  public PricePromotionLine get(String tenant, String owner, String uuid) {
    SelectStatement select = new SelectBuilder().from(PPricePromotionLine.TABLE_NAME)
        .select(PPricePromotionLine.allColumns())
        .where(Predicates.equals(PPricePromotionLine.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionLine.OWNER, owner))
        .where(Predicates.equals(PPricePromotionLine.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new PricePromotionLineMapper()));
  }

  public void insert(String tenant, String owner, PricePromotionLine line) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(line, "line");

    if (StringUtils.isBlank(line.getUuid())) {
      line.setUuid(UUID.randomUUID().toString());
    }
    InsertStatement insert = buildInsertStatement(tenant, owner, line);
    jdbcTemplate.update(insert);
  }

  public void batchDelete(String tenant, String owner, Collection<String> uuids) {
    DeleteStatement delete = new DeleteBuilder().table(PPricePromotionLine.TABLE_NAME)
        .where(Predicates.equals(PPricePromotionLine.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionLine.OWNER, owner))
        .where(Predicates.in2(PPricePromotionLine.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<PricePromotionLine> list(String tenant, String owner, Collection<String> uuids) {
    if (CollectionUtils.isEmpty(uuids)) {
      return new ArrayList<>();
    }
    SelectStatement select = new SelectBuilder().from(PPricePromotionLine.TABLE_NAME)
        .select(PPricePromotionLine.allColumns())
        .where(Predicates.equals(PPricePromotionLine.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionLine.OWNER, owner))
        .where(Predicates.in2(PPricePromotionLine.UUID, uuids.toArray()))
        .build();
    return jdbcTemplate.query(select, new PricePromotionLineMapper());
  }

  public List<PricePromotionLine> listBySkuIds(String tenant, String owner, Collection<String> skuIds) {
    if (CollectionUtils.isEmpty(skuIds)) {
      return new ArrayList<>();
    }
    SelectStatement select = new SelectBuilder().from(PPricePromotionLine.TABLE_NAME)
        .select(PPricePromotionLine.allColumns())
        .where(Predicates.equals(PPricePromotionLine.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionLine.OWNER, owner))
        .where(Predicates.in2(PPricePromotionLine.SKU_ID, skuIds.toArray()))
        .build();
    return jdbcTemplate.query(select, new PricePromotionLineMapper());
  }

  public void update(String tenant, String owner, PricePromotionLine line) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(line, "line");
    UpdateStatement update = buildUpdateStatement(tenant, owner, line);
    jdbcTemplate.update(update);
  }

  public void batchUpdate(String tenant, String owner, Collection<PricePromotionLine> lines) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    lines.forEach(u -> updater.add(buildUpdateStatement(tenant, owner, u)));
    updater.update();
  }

  public void batchInsert(String tenant, String owner, Collection<PricePromotionLine> lines) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (PricePromotionLine line : lines) {
      if (StringUtils.isBlank(line.getUuid())) {
        line.setUuid(UUID.randomUUID().toString());
      }
      statements.add(buildInsertStatement(tenant, owner, line));
    }
    batchUpdate(statements);
  }

  public void batchInsert(String tenant, Collection<PricePromotionLine> lines) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (PricePromotionLine line : lines) {
      Assert.hasText(line.getOwner(), "owner");
      if (StringUtils.isBlank(line.getUuid())) {
        line.setUuid(UUID.randomUUID().toString());
      }
      statements.add(buildInsertStatement(tenant, line.getOwner(), line));
    }
    batchUpdate(statements);
  }

  private InsertStatement buildInsertStatement(String tenant, String owner, PricePromotionLine line) {
    return new InsertBuilder().table(PPricePromotionLine.TABLE_NAME)
        .addValues(PPricePromotionLine.forSaveNew(line))
        .addValue(PPricePromotionLine.TENANT, tenant)
        .addValue(PPricePromotionLine.OWNER, owner)
        .addValue(PPricePromotionLine.TYPE, line.getType().name())
        .addValue(PPricePromotionLine.RULE, line.getRule())
        .addValue(PPricePromotionLine.SKU_ID, line.getSku().getId())
        .addValue(PPricePromotionLine.SKU_CODE, line.getSku().getCode())
        .addValue(PPricePromotionLine.SKU_NAME, line.getSku().getName())
        .addValue(PPricePromotionLine.SKU_QPC, line.getSku().getQpc())
        .addValue(PPricePromotionLine.SKU_UNIT, line.getSku().getUnit())
        .addValue(PPricePromotionLine.SKU_GROUP, line.getSkuGroup())
        .addValue(PPricePromotionLine.SKU_GROUP_NAME, line.getSkuGroupName())
        .build();
  }

  private UpdateStatement buildUpdateStatement(String tenant, String owner, PricePromotionLine line) {
    return new UpdateBuilder().table(PPricePromotionLine.TABLE_NAME)
        .addValue(PPricePromotionLine.TYPE, line.getType().name())
        .addValue(PPricePromotionLine.RULE, line.getRule())
        .addValue(PPricePromotionLine.SKU_ID, line.getSku().getId())
        .addValue(PPricePromotionLine.SKU_CODE, line.getSku().getCode())
        .addValue(PPricePromotionLine.SKU_NAME, line.getSku().getName())
        .addValue(PPricePromotionLine.SKU_QPC, line.getSku().getQpc())
        .addValue(PPricePromotionLine.SKU_UNIT, line.getSku().getUnit())
        .addValue(PPricePromotionLine.SKU_GROUP, line.getSkuGroup())
        .addValue(PPricePromotionLine.SKU_GROUP_NAME, line.getSkuGroupName())
        .where(Predicates.equals(PPricePromotionLine.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionLine.OWNER, owner))
        .where(Predicates.equals(PPricePromotionLine.UUID, line.getUuid()))
        .build();
  }

  public PricePromotionLine getBySkuGroup(String tenant, String owner, String skuGroup) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "owner");
    Assert.notNull(skuGroup, "skuGroup");
    SelectStatement statement = new SelectBuilder().from(PPricePromotionLine.TABLE_NAME)
        .where(Predicates.equals(PPricePromotionLine.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionLine.OWNER, owner))
        .where(Predicates.equals(PPricePromotionLine.SKU_GROUP, skuGroup))
        .build();
    List<PricePromotionLine> query = jdbcTemplate.query(statement, new PricePromotionLineMapper());
    if (CollectionUtils.isNotEmpty(query)) {
      return query.get(0);
    }
    return null;
  }

  public QueryResult<ConflictPromotionLine> listConflict(String tenant, String orgId, String uuid, List<String> shops,
      Collection<String> skuIds, Collection<String> skuGroup, Date startDate, Date endDate, int offSet, int size) {
    Assert.notEmpty(shops, "门店");
    Assert.notNull(startDate, "起始日期");
    Assert.notNull(endDate, "结束日期");
    String[] states = new String[] {
        PricePromotionState.AUDITED.name(), PricePromotionState.PUBLISHED.name() };
    SelectBuilder select = new SelectBuilder()
        .select(PPricePromotionShop.SHOP_NAME, PPricePromotionShop.SHOP_CODE, PPricePromotionLine.SKU_NAME,
            PPricePromotionLine.SKU_CODE, PPricePromotionLine.SKU_GROUP, PPricePromotionLine.SKU_GROUP_NAME,
            PPricePromotionLine.TABLE_ALIAS + "." + PPricePromotionLine.TYPE, PPricePromotionLine.RULE,
            PPricePromotion.EFFECTIVE_END_DATE, PPricePromotion.EFFECTIVE_START_DATE, PPricePromotion.FLOW_NO,
            PPricePromotion.TABLE_ALIAS + "." + PPricePromotion.UUID, PPricePromotion.CREATE_INFO_OPERATOR_ID,
            PPricePromotion.CREATE_INFO_OPERATOR_FULL_NAME, PPricePromotion.CREATE_INFO_TIME)
        .from(PPricePromotionLine.TABLE_NAME, PPricePromotionLine.TABLE_ALIAS)
        .leftJoin(PPricePromotion.TABLE_NAME, PPricePromotion.TABLE_ALIAS,
            Predicates.equals(PPricePromotion.TABLE_ALIAS, PPricePromotion.UUID, PPricePromotionLine.TABLE_ALIAS,
                PPricePromotionLine.OWNER))
        .leftJoin(PPricePromotionShop.TABLE_NAME, PPricePromotionShop.TABLE_ALIAS,
            Predicates.equals(PPricePromotionShop.TABLE_ALIAS, PPricePromotionShop.OWNER, PPricePromotion.TABLE_ALIAS,
                PPricePromotion.UUID))
        .where(Predicates.equals(PPricePromotionLine.TABLE_ALIAS, PPricePromotionLine.TENANT, tenant))
        .where(Predicates.notEquals(PPricePromotion.TABLE_ALIAS, PPricePromotion.UUID, uuid))
        .where(Predicates.in(PPricePromotion.TABLE_ALIAS, PPricePromotion.STATE, states))
        .where(Predicates.or(Predicates.in(PPricePromotionShop.TABLE_ALIAS, PPricePromotionShop.SHOP, shops.toArray()),
            Predicates.equals(PPricePromotionShop.TABLE_ALIAS, PPricePromotionShop.SHOP, "*")))
        .where(Predicates.lessOrEquals(PPricePromotion.TABLE_ALIAS, PPricePromotion.EFFECTIVE_START_DATE, endDate))
        .where(Predicates.greaterOrEquals(PPricePromotion.TABLE_ALIAS, PPricePromotion.EFFECTIVE_END_DATE, startDate))
        .where(Predicates.equals(PPricePromotion.TABLE_ALIAS, PPricePromotion.ORG_ID, orgId))
        .limit(offSet, size);
    OrPredicate orPredicate = new OrPredicate();
    if (CollectionUtils.isNotEmpty(skuIds)) {
      orPredicate.add(Predicates.in(PPricePromotionLine.TABLE_ALIAS, PPricePromotionLine.SKU_ID, skuIds.toArray()));
    }
    if (CollectionUtils.isNotEmpty(skuGroup)) {
      orPredicate
          .add(Predicates.in(PPricePromotionLine.TABLE_ALIAS, PPricePromotionLine.SKU_GROUP, skuGroup.toArray()));
    }
    if (CollectionUtils.isNotEmpty(skuIds) || CollectionUtils.isNotEmpty(skuGroup)) {
      select.where(orPredicate);
    }
    return executor.query(select.build(), new ConflictPromotionLineMapper());
  }
}
