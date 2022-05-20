package com.hd123.baas.sop.service.dao.range;

import java.util.List;

import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.entity.PriceRange;
import com.hd123.baas.sop.service.dao.grade.PPriceGrade;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

@Repository
public class PriceRangeDaoBof extends BofBaseDao {
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceRange.class, PPriceRange.class).build();

  public int insert(String tenant, PriceRange priceRange) {
    Assert.notNull(priceRange);
    Assert.notNull(tenant, "租戶");
    InsertStatement insert = buildInsert(tenant, priceRange);
    return jdbcTemplate.update(insert);
  }

  public void batchInsert(String tenant, List<PriceRange> priceRanges) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(priceRanges);
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (PriceRange priceRange : priceRanges){
      InsertStatement insert = buildInsert(tenant, priceRange);
      batchUpdater.add(insert);
    }
    batchUpdater.update();
  }

  public int update(String tenant, PriceRange priceRange) {
    Assert.notNull(priceRange);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(priceRange.getUuid(), "uuid");
    Assert.notNull(priceRange.getName(), "名称");
    UpdateStatement update = new UpdateBuilder().table(PPriceRange.TABLE_NAME)
        .setValue(PPriceRange.NAME, priceRange.getName())
        .where(Predicates.equals(PPriceRange.TENANT, tenant))
        .where(Predicates.equals(PPriceRange.UUID, priceRange.getUuid()))
        .build();
    return jdbcTemplate.update(update);

  }

  public void delete(String tenant, List<Integer> uuids) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(uuids, "uuids");
    DeleteStatement delete = new DeleteBuilder().table(PPriceRange.TABLE_NAME)
        .where(Predicates.in2(PPriceRange.UUID, uuids.toArray()))
        .where(Predicates.equals(PPriceRange.TENANT, tenant))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<PriceRange> list(String tenant, String orgId) {
    Assert.notNull(tenant, "租戶");
    SelectStatement select = new SelectBuilder().select(PPriceRange.allColumns())
        .from(PPriceRange.TABLE_NAME)
        .where(Predicates.equals(PPriceGrade.TENANT, tenant))
        .orderBy(PPriceRange.NAME, true)
        .build();
    if (StringUtils.isNotBlank(orgId)) {
      select.where((Predicates.equals(PPriceGrade.ORG_ID, orgId)));
    }
    return jdbcTemplate.query(select, new PriceRangeMapper());
  }

  public QueryResult<PriceRange> query(String tenant, QueryDefinition qd) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(PriceRange.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PriceRangeMapper());
  }

  public PriceRange queryByName(String tenant, String orgId, String name) {
    QueryDefinition gradeQd = new QueryDefinition();
    gradeQd.addByField(PriceRange.Queries.NAME, Cop.EQUALS, name);
    gradeQd.addByField(PriceRange.Queries.ORG_ID, Cop.EQUALS, orgId);
    QueryResult<PriceRange> query = query(tenant, gradeQd);
    PriceRange first = getFirst(query.getRecords());
    return first;
  }

  public PriceRange queryByUuid(String tenant, Integer uuid) {
    QueryDefinition gradeQd = new QueryDefinition();
    gradeQd.addByField(PriceRange.Queries.UUID, Cop.EQUALS, uuid);
    QueryResult<PriceRange> query = query(tenant, gradeQd);
    PriceRange first = getFirst(query.getRecords());
    return first;
  }

  private InsertStatement buildInsert(String tenant, PriceRange priceRange) {
    Assert.notNull(priceRange.getName(), "名称");
    InsertStatement insert = new InsertBuilder().table(PPriceRange.TABLE_NAME)
        .addValue(PPriceRange.TENANT, tenant)
        .addValue(PPriceRange.ORG_ID, priceRange.getOrgId())
        .addValue(PPriceRange.UUID, priceRange.getUuid())
        .addValue(PPriceRange.NAME, priceRange.getName())
        .build();
    return insert;
  }

}
