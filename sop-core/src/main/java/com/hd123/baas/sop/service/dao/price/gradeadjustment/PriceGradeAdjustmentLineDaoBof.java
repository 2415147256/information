package com.hd123.baas.sop.service.dao.price.gradeadjustment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.gradeadjustment.PriceGradeAdjustmentLine;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/12.
 */
@Repository
public class PriceGradeAdjustmentLineDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceGradeAdjustmentLine.class,
      PPriceGradeAdjustmentLine.class).addConditionProcessor((condition, context) -> {
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(PriceGradeAdjustmentLine.Queries.PRICE_GRADE_IS_NULL,
            condition.getOperation())) {
          return Predicates.isNull(alias, PPriceGradeAdjustmentLine.PRICE_GRADE);
        }
        return null;
      }).build();

  public QueryResult<PriceGradeAdjustmentLine> query(String tenant, String owner, QueryDefinition qd) {
    qd.addByField(PriceGradeAdjustmentLine.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PriceGradeAdjustmentLine.Queries.OWNER, Cop.EQUALS, owner);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PriceGradeAdjustmentLineMapper());
  }

  public long queryCount(String tenant, String owner, QueryDefinition qd) {
    qd.addByField(PriceGradeAdjustmentLine.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PriceGradeAdjustmentLine.Queries.OWNER, Cop.EQUALS, owner);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    selectStatement.getSelectClause().getFields().clear();
    selectStatement.select("count(1)");
    QueryResult<Long> result = executor.query(selectStatement, new SingleColumnRowMapper<>(Long.class));
    if (result.getRecordCount() == 0) {
      return 0;
    }
    return result.getRecords().get(0);
  }

  public PriceGradeAdjustmentLine getByGroupAndPosition(String tenant, String owner, String skuGroup,
      String skuPosition) {
    SelectStatement select = new SelectBuilder().from(PPriceGradeAdjustmentLine.TABLE_NAME)
        .select(PPriceGradeAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceGradeAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceGradeAdjustmentLine.OWNER, owner))
        .where(Predicates.equals(PPriceGradeAdjustmentLine.SKU_GROUP, skuGroup))
        .where(Predicates.equals(PPriceGradeAdjustmentLine.SKU_POSITION, skuPosition))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceGradeAdjustmentLineMapper()));
  }

  public PriceGradeAdjustmentLine get(String tenant, String owner, String uuid) {
    SelectStatement select = new SelectBuilder().from(PPriceGradeAdjustmentLine.TABLE_NAME)
        .select(PPriceGradeAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceGradeAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceGradeAdjustmentLine.OWNER, owner))
        .where(Predicates.equals(PPriceGradeAdjustmentLine.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceGradeAdjustmentLineMapper()));
  }

  public void insert(String tenant, String owner, PriceGradeAdjustmentLine line) {
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
    DeleteStatement delete = new DeleteBuilder().table(PPriceGradeAdjustmentLine.TABLE_NAME)
        .where(Predicates.equals(PPriceGradeAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceGradeAdjustmentLine.OWNER, owner))
        .where(Predicates.in2(PPriceGradeAdjustmentLine.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<PriceGradeAdjustmentLine> list(String tenant, String owner, Collection<String> uuids) {
    if (CollectionUtils.isEmpty(uuids)) {
      return new ArrayList<>();
    }
    SelectStatement select = new SelectBuilder().from(PPriceGradeAdjustmentLine.TABLE_NAME)
        .select(PPriceGradeAdjustmentLine.allColumns())
        .where(Predicates.equals(PPriceGradeAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceGradeAdjustmentLine.OWNER, owner))
        .where(Predicates.in2(PPriceGradeAdjustmentLine.UUID, uuids.toArray()))
        .build();
    return jdbcTemplate.query(select, new PriceGradeAdjustmentLineMapper());
  }

  public void update(String tenant, String owner, PriceGradeAdjustmentLine line) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(line, "line");
    UpdateStatement update = new UpdateBuilder().table(PPriceGradeAdjustmentLine.TABLE_NAME)
        .addValue(PPriceGradeAdjustmentLine.SKU_GROUP, line.getSkuGroup())
        .addValue(PPriceGradeAdjustmentLine.SKU_GROUP_NAME, line.getSkuGroupName())
        .addValue(PPriceGradeAdjustmentLine.SKU_POSITION, line.getSkuPosition())
        .addValue(PPriceGradeAdjustmentLine.SKU_POSITION_NAME, line.getSkuPositionName())
        .addValue(PPriceGradeAdjustmentLine.PRICE_GRADE, line.getPriceGrade())
        .addValue(PPriceGradeAdjustmentLine.PRICE_GRADE_NAME, line.getPriceGradeName())
        .where(Predicates.equals(PPriceGradeAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PPriceGradeAdjustmentLine.OWNER, owner))
        .where(Predicates.equals(PPriceGradeAdjustmentLine.UUID, line.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void batchInsert(String tenant, String owner, Collection<PriceGradeAdjustmentLine> lines)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (PriceGradeAdjustmentLine line : lines) {
      if (StringUtils.isBlank(line.getUuid())) {
        line.setUuid(UUID.randomUUID().toString());
      }
      statements.add(buildInsertStatement(tenant, owner, line));
    }
    batchUpdate(statements);
  }

  private InsertStatement buildInsertStatement(String tenant, String owner, PriceGradeAdjustmentLine line) {
    InsertStatement insert = new InsertBuilder().table(PPriceGradeAdjustmentLine.TABLE_NAME)
        .addValue(PPriceGradeAdjustmentLine.TENANT, tenant)
        .addValue(PPriceGradeAdjustmentLine.OWNER, owner)
        .addValue(PPriceGradeAdjustmentLine.UUID, line.getUuid())
        .addValue(PPriceGradeAdjustmentLine.SKU_GROUP, line.getSkuGroup())
        .addValue(PPriceGradeAdjustmentLine.SKU_GROUP_NAME, line.getSkuGroupName())
        .addValue(PPriceGradeAdjustmentLine.SKU_POSITION, line.getSkuPosition())
        .addValue(PPriceGradeAdjustmentLine.SKU_POSITION_NAME, line.getSkuPositionName())
        .addValue(PPriceGradeAdjustmentLine.PRICE_GRADE, line.getPriceGrade())
        .addValue(PPriceGradeAdjustmentLine.PRICE_GRADE_NAME, line.getPriceGradeName())
        .build();
    return insert;
  }

}
