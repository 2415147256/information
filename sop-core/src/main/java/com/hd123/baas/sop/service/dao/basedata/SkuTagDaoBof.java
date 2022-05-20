/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： sop-parent 文件名： SkuBomDao.java 模块说明： 修改历史： 2020年11月17日 - XLT - 创建。
 */
package com.hd123.baas.sop.service.dao.basedata;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.sku.DSkuTag;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorProvider;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lina
 */
@Repository
public class SkuTagDaoBof extends MasBofBaseDao implements QueryProcessorProvider {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(DSkuTag.class, PSkuTag.class)
      .build();

  @Override
  public QueryProcessor getQueryProcessor() {
    return QUERY_PROCESSOR;
  }

  @Tx
  public void insert(List<DSkuTag> dSkuBomList) {
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (DSkuTag dSkuTag : dSkuBomList) {
      dSkuTag.setUuid(generateUUID());
      InsertStatement insertStatement = new InsertBuilder().table(PSkuTag.TABLE_NAME)
          .addValues(PSkuTag.toFieldValues(dSkuTag)).build();
      updater.add(insertStatement);
    }
    updater.update();
  }

  @Tx
  public void remove(String tenant, String orgType, String orgId, List<String> skuIds) {
    DeleteStatement deleteStatement = new DeleteBuilder().table(PSkuTag.TABLE_NAME)
        .where(Predicates.equals(PSkuTag.TENANT, tenant))
        .where(Predicates.equals(PSkuTag.ORG_ID, orgId))
        .where(Predicates.equals(PSkuTag.ORG_TYPE, orgType))
        .where(Predicates.in2(PSkuTag.SKU_ID, skuIds.toArray())).build();
    jdbcTemplate.update(deleteStatement);
  }

  @Tx
  public void removeByGoodsIdList(String tenant, String orgType, String orgId, List<String> goodsIdList) {
    DeleteStatement deleteStatement = new DeleteBuilder().table(PSkuTag.TABLE_NAME)
        .where(Predicates.equals(PSkuTag.TENANT, tenant))
        .where(Predicates.equals(PSkuTag.ORG_ID, orgId))
        .where(Predicates.equals(PSkuTag.ORG_TYPE, orgType))
        .where(Predicates.in2(PSkuTag.GOODS_GID, goodsIdList.toArray())).build();
    jdbcTemplate.update(deleteStatement);
  }

  @Tx
  public void remove(String tenant, String orgType, String orgId, String skuId) {
    DeleteStatement deleteStatement = new DeleteBuilder().table(PSkuTag.TABLE_NAME)
        .where(Predicates.equals(PSkuTag.TENANT, tenant))
        .where(Predicates.equals(PSkuTag.ORG_ID, orgId))
        .where(Predicates.equals(PSkuTag.ORG_TYPE, orgType))
        .where(Predicates.equals(PSkuTag.SKU_ID, skuId)).build();
    jdbcTemplate.update(deleteStatement);
  }

  public QueryResult<DSkuTag> query(QueryDefinition qd) {
    Assert.notNull(qd);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new SkuTagMapper());
  }

}