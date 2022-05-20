package com.hd123.baas.sop.service.dao.basedata;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.sku.DSku;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorProvider;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author lina
 */
@Repository
public class SkuDaoBof extends MasBofBaseDao implements QueryProcessorProvider {
  @Override
  public QueryProcessor getQueryProcessor() {
    return QUERY_PROCESSOR;
  }

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(DSku.class, PSku.class)
      .build();

  public QueryResult<DSku> query(QueryDefinition qd) {
    Assert.notNull(qd);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new SkuMapper());
  }

  @Tx
  public void insert(DSku dSku) {
    if (StringUtil.isNullOrBlank(dSku.getUuid())) {
      dSku.setUuid(UUID.randomUUID().toString());
      InsertStatement insertStatement = new InsertBuilder().table(PSku.TABLE_NAME).addValues(PSku.toFieldValues(dSku)).build();
      jdbcTemplate.update(insertStatement);
    } else {
      UpdateStatement updateStatement = new UpdateBuilder().table(PSku.TABLE_NAME).addValues(PSku.toFieldValues(dSku)).where(Predicates.equals(PSku.TENANT, dSku.getTenant()))
          .where(Predicates.equals(PSku.ID, dSku.getId()))
          .where(Predicates.equals(PSku.UUID, dSku.getUuid()))
          .build();
      jdbcTemplate.update(updateStatement);
    }
  }

  @Tx
  public void batchInit(List<DSku> skuList) {
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (DSku sku : skuList) {
      if (StringUtil.isNullOrBlank(sku.getUuid())) {
        sku.setUuid(generateUUID());
        InsertStatement insertStatement = new InsertBuilder().table(PSku.TABLE_NAME).addValues(PSku.toFieldValues(sku)).build();
        updater.add(insertStatement);
      } else {
        UpdateStatement updateStatement = new UpdateBuilder().table(PSku.TABLE_NAME).addValues(PSku.toFieldValues(sku)).where(Predicates.equals(PSku.UUID, sku.getUuid())).build();
        updater.add(updateStatement);
      }
    }
    updater.update();
  }

  @Tx
  public void remove(String tenant, String orgType, String orgId, String id) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(id, "id");
    DeleteStatement updateStatement = new DeleteBuilder().table(PSku.TABLE_NAME)
        .where(Predicates.equals(PSku.TENANT, tenant))
        .where(Predicates.equals(PSku.ID, id))
        .where(Predicates.equals(PSku.ORG_ID, orgId))
        .where(Predicates.equals(PSku.ORG_TYPE, orgType))
        .build();
    jdbcTemplate.update(updateStatement);
  }

  @Tx
  public void removeByIds(String tenant,String orgType, String orgId, List<String> ids) {
    Assert.notNull(tenant, "tenant");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");
    Assert.notNull(ids, "goodsIdList");
    DeleteStatement deleteStatement = new DeleteBuilder().table(PSku.TABLE_NAME)
        .where(Predicates.equals(PSku.TENANT, tenant))
        .where(Predicates.equals(PSku.ORG_ID, orgId))
        .where(Predicates.equals(PSku.ORG_TYPE, orgType))
        .where(Predicates.in2(PSku.ID, ids.toArray()))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  @Tx
  public void removeByGoodsIdList(String tenant,String orgType, String orgId, List<String> goodsIdList) {
    Assert.notNull(tenant, "tenant");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");
    Assert.notNull(goodsIdList, "goodsIdList");
    DeleteStatement deleteStatement = new DeleteBuilder().table(PSku.TABLE_NAME)
        .where(Predicates.equals(PSku.TENANT, tenant))
        .where(Predicates.equals(PSku.ORG_ID, orgId))
        .where(Predicates.equals(PSku.ORG_TYPE, orgType))
        .where(Predicates.in2(PSku.GOODS_GID, goodsIdList.toArray()))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public DSku get(String tenant,String orgType, String orgId, String id) {
    Assert.notNull(tenant, "租户");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");
    Assert.notNull(id, "id");
    SelectBuilder select = new SelectBuilder().from(PSku.TABLE_NAME)
        .where(Predicates.equals(PSku.TENANT, tenant))
        .where(Predicates.equals(PSku.ORG_ID, orgId))
        .where(Predicates.equals(PSku.ORG_TYPE, orgType))
        .where(Predicates.equals(PSku.ID, id));
    List<DSku> result = jdbcTemplate.query(select.build(), new SkuMapper());
    if (CollectionUtils.isEmpty(result)) {
      return null;
    }
    return result.get(0);
  }
}
