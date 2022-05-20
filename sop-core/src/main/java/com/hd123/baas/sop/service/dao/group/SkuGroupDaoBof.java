package com.hd123.baas.sop.service.dao.group;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.entity.SkuGroup;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
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
import com.hd123.rumba.commons.lang.Assert;

@Repository
public class SkuGroupDaoBof extends BofBaseDao {

  private static QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuGroup.class, PSkuGroup.class).build();

  public void insert(String tenant, SkuGroup skuGroup) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(skuGroup);
    Assert.notNull(skuGroup.getName(), "name");
    InsertStatement insert = new InsertBuilder().table(PSkuGroup.TABLE_NAME)
        .addValue(PSkuGroup.TENANT, tenant)
        .addValue(PSkuGroup.ORG_ID, skuGroup.getOrgId())
        .addValue(PSkuGroup.UUID, skuGroup.getUuid())
        .addValue(PSkuGroup.NAME, skuGroup.getName())
        .addValue(PSkuGroup.TOLERANCE_VALUE, skuGroup.getToleranceValue())
        .build();
    jdbcTemplate.update(insert);
  }

  public void update(String tenant, SkuGroup skuGroup) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(skuGroup);
    Assert.notNull(skuGroup.getUuid(), "uuid");
    Assert.notNull(skuGroup.getName(), "name");
    UpdateStatement update = new UpdateBuilder().table(PSkuGroup.TABLE_NAME)
        .setValue(PSkuGroup.NAME, skuGroup.getName())
        .setValue(PSkuGroup.TOLERANCE_VALUE, skuGroup.getToleranceValue())
        .where(Predicates.equals(PSkuGroup.TENANT, tenant))
        .where(Predicates.equals(PSkuGroup.UUID, skuGroup.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void delete(String tenant, List<Integer> uuids) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(uuids);
    DeleteStatement delete = new DeleteBuilder().table(PSkuGroup.TABLE_NAME)
        .where(Predicates.in2(PSkuGroup.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<SkuGroup> list(String tenant) {
    return list(tenant, null);
  }

  public List<SkuGroup> list(String tenant, String orgId) {
    Assert.notNull(tenant, "租戶");
    SelectStatement select = new SelectBuilder().select(PSkuGroup.allColumns())
        .from(PSkuGroup.TABLE_NAME)
        .where(Predicates.equals(PSkuGroup.TENANT, tenant))
        .build();
    if (StringUtils.isNotBlank(orgId)) {
      select.where(Predicates.equals(PSkuGroup.ORG_ID, orgId));
    }
    return jdbcTemplate.query(select, new SkuGroupMapper());
  }

  public QueryResult<SkuGroup> query(String tenant, QueryDefinition qd) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(SkuGroup.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new SkuGroupMapper());
  }


  public SkuGroup query(String tenant, Integer skuGroupId) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(skuGroupId, "skuGroupId");
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuGroup.Queries.UUID, Cop.EQUALS, skuGroupId);
    QueryResult<SkuGroup> result = query(tenant, qd);
    return getFirst(result.getRecords());
  }

  public SkuGroup queryByName(String tenant, String orgId, String name) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(name, "name");
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuGroup.Queries.NAME, Cop.EQUALS, name);
    qd.addByField(SkuGroup.Queries.ORG_ID, Cop.EQUALS, orgId);
    QueryResult<SkuGroup> result = query(tenant, qd);
    return getFirst(result.getRecords());
  }

  public SkuGroup get(String tenant, String uuid) {
    Assert.notNull(tenant,"租户");
    Assert.notNull(uuid,"uuid");
    SelectBuilder select = new SelectBuilder().from(PSkuGroup.TABLE_NAME)
            .where(Predicates.equals(PSkuGroup.TENANT,tenant))
            .where(Predicates.equals(PSkuGroup.UUID,uuid));
    return getFirst(jdbcTemplate.query(select.build(),new SkuGroupMapper()));
  }

  public static void main(String[] args) {
    QueryDefinition qd = new QueryDefinition();
    int page = 0;
    qd.setPageSize(4);
    qd.addByField(SkuGroup.Queries.TENANT, Cop.EQUALS, "mkhtest");
    for (int i = 0; i < 2; i++) {
      qd.setPage(page++);
      SelectStatement select = QUERY_PROCESSOR.process(qd);
      System.out.println(select.getSql());
    }
  }
}
