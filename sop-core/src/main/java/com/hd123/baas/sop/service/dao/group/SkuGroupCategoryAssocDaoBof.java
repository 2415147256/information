package com.hd123.baas.sop.service.dao.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.entity.SkuGroupCategoryAssoc;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

@Repository
public class SkuGroupCategoryAssocDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuGroupCategoryAssoc.class,
      PSkuGroupCategoryAssoc.class).build();

  public void batchInsert(String tenant, List<SkuGroupCategoryAssoc> list) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(list, "list");
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (SkuGroupCategoryAssoc assoc : list) {
      Assert.notNull(assoc.getSkuGroupId(), "skuGroup");
      Assert.notNull(assoc.getCategoryId(), "categoryId");
      InsertStatement insert = new InsertBuilder().table(PSkuGroupCategoryAssoc.TABLE_NAME)
          .addValue(PSkuGroupCategoryAssoc.TENANT, tenant)
          .addValue(PSkuGroupCategoryAssoc.UUID, assoc.getUuid())
          .addValue(PSkuGroupCategoryAssoc.ORG_ID, assoc.getOrgId())
          .addValue(PSkuGroupCategoryAssoc.SKU_GROUP_ID, assoc.getSkuGroupId())
          .addValue(PSkuGroupCategoryAssoc.CATEGORY_CODE, assoc.getCategoryCode())
          .addValue(PSkuGroupCategoryAssoc.CATEGORY_NAME, assoc.getCategoryName())
          .addValue(PSkuGroupCategoryAssoc.CATEGORY_ID, assoc.getCategoryId())
          .build();
      batchUpdater.add(insert);
    }
    batchUpdater.update();
  }

  public void remove(String tenant, Integer groupId, List<String> categoryIds) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "groupId");
    if (CollectionUtils.isEmpty(categoryIds)) {
      return;
    }

    DeleteStatement delete = new DeleteBuilder().table(PSkuGroupCategoryAssoc.TABLE_NAME)
        .where(Predicates.equals(PSkuGroupCategoryAssoc.TENANT, tenant))
        .where(Predicates.equals(PSkuGroupCategoryAssoc.SKU_GROUP_ID, groupId))
        .where(Predicates.in2(PSkuGroupCategoryAssoc.CATEGORY_ID, categoryIds.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public static void main(String[] args) {
    List<String> categoryIds = new ArrayList<>();
    DeleteStatement delete = new DeleteBuilder().table(PSkuGroupCategoryAssoc.TABLE_NAME)
        .where(Predicates.equals(PSkuGroupCategoryAssoc.TENANT, "tenant"))
        .where(Predicates.equals(PSkuGroupCategoryAssoc.SKU_GROUP_ID, "groupId"))
        .where(Predicates.in2(PSkuGroupCategoryAssoc.CATEGORY_ID, categoryIds.toArray()))
        .build();

    System.out.println(delete.getSql());
  }

  public QueryResult<SkuGroupCategoryAssoc> query(String tenant, QueryDefinition qd) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(SkuGroupCategoryAssoc.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new SkuGroupCategoryAssocMapper());
  }

  public List<SkuGroupCategoryAssoc> queryByCategoryIds(String tenant, List<String> categoryIds) {
    Assert.hasLength(tenant, "tenant");
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuGroupCategoryAssoc.Queries.CATEGORY_ID, Cop.IN, categoryIds);
    QueryResult<SkuGroupCategoryAssoc> query = query(tenant, qd);
    return query.getRecords();
  }

  public List<SkuGroupCategoryAssoc> queryAll(String tenant, String orgId) {
    Assert.hasLength(tenant, "tenant");

    SelectStatement select = new SelectBuilder().select(PSkuGroupCategoryAssoc.allColumns())
        .from(PSkuGroupCategoryAssoc.TABLE_NAME)
        .where(Predicates.equals(PSkuGroupCategoryAssoc.ORG_ID, orgId))
        .where(Predicates.equals(PSkuGroupCategoryAssoc.TENANT, tenant))
        .build();
    return jdbcTemplate.query(select, new SkuGroupCategoryAssocMapper());
  }

  public void batchDelete(String tenant, List<Integer> groupIds) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(groupIds);
    DeleteStatement delete = new DeleteBuilder().table(PSkuGroupCategoryAssoc.TABLE_NAME)
        .where(Predicates.equals(PSkuGroupCategoryAssoc.TENANT, tenant))
        .where(Predicates.in2(PSkuGroupCategoryAssoc.SKU_GROUP_ID, groupIds.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }
}
