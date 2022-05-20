package com.hd123.baas.sop.service.dao.price.shopprice;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceGrade;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author zhengzewang on 2020/11/16.
 */
@Repository
public class ShopPriceGradeDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopPriceGrade.class, PShopPriceGrade.class)
      .build();

  public QueryResult<ShopPriceGrade> query(String tenant, QueryDefinition qd) {
    qd.addByField(ShopPriceGrade.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new ShopPriceGradeMapper());
  }

  public ShopPriceGrade get(String tenant, String uuid) {
    SelectStatement select = new SelectBuilder().from(PShopPriceGrade.TABLE_NAME)
        .select(PShopPriceGrade.allColumns())
        .where(Predicates.equals(PShopPriceGrade.TENANT, tenant))
        .where(Predicates.equals(PShopPriceGrade.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new ShopPriceGradeMapper()));
  }

  public ShopPriceGrade getByShopAndGroupAndPosition(String tenant, String shop, String skuGroup, String skuPosition) {
    SelectStatement select = new SelectBuilder().from(PShopPriceGrade.TABLE_NAME)
        .select(PShopPriceGrade.allColumns())
        .where(Predicates.equals(PShopPriceGrade.TENANT, tenant))
        .where(Predicates.equals(PShopPriceGrade.SHOP, shop))
        .where(Predicates.equals(PShopPriceGrade.SKU_GROUP, skuGroup))
        .where(Predicates.equals(PShopPriceGrade.SKU_POSITION, skuPosition))
        .build();
    return getFirst(jdbcTemplate.query(select, new ShopPriceGradeMapper()));
  }

  public List<ShopPriceGrade> listByShop(String tenant, String shop) {
    SelectStatement select = new SelectBuilder().from(PShopPriceGrade.TABLE_NAME)
        .select(PShopPriceGrade.allColumns())
        .where(Predicates.equals(PShopPriceGrade.TENANT, tenant))
        .where(Predicates.equals(PShopPriceGrade.SHOP, shop))
        .build();
    return jdbcTemplate.query(select, new ShopPriceGradeMapper());
  }

  public void insert(String tenant, ShopPriceGrade grade) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(grade, "grade");
    InsertStatement insert = buildInsertStatement(tenant, grade);
    jdbcTemplate.update(insert);
  }

  public void update(String tenant, ShopPriceGrade grade) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(grade, "grade");
    Assert.hasText(grade.getUuid(), "grade.uuid");
    jdbcTemplate.update(buildUpdateStatement(tenant, grade));
  }

  public void batchUpdate(String tenant, Collection<ShopPriceGrade> grades) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(grades)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopPriceGrade grade : grades) {
      Assert.hasText(grade.getUuid(), "grade.uuid");
      updater.add(buildUpdateStatement(tenant, grade));
    }
    updater.update();
  }

  public void batchInsert(String tenant, Collection<ShopPriceGrade> grades) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(grades)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (ShopPriceGrade grade : grades) {
      statements.add(buildInsertStatement(tenant, grade));
    }
    batchUpdate(statements);
  }

  private UpdateStatement buildUpdateStatement(String tenant, ShopPriceGrade grade) {
    UpdateStatement update = new UpdateBuilder().table(PShopPriceGrade.TABLE_NAME)
        .addValues(PShopPriceGrade.forSaveModify(grade))
        .addValue(PShopPriceGrade.ORG_ID, grade.getOrgId())
        .addValue(PShopPriceGrade.SHOP, grade.getShop())
        .addValue(PShopPriceGrade.SOURCE, grade.getSource())
        .addValue(PShopPriceGrade.SKU_GROUP, grade.getSkuGroup())
        .addValue(PShopPriceGrade.SKU_POSITION, grade.getSkuPosition())
        .addValue(PShopPriceGrade.PRICE_GRADE, grade.getPriceGrade())
        .addValue(PShopPriceGrade.SOURCE_CREATE_TIME, grade.getSourceCreateTime())
        .addValue(PShopPriceGrade.LAST_MODIFY_INFO_TIME, new Date())

        .where(Predicates.equals(PShopPriceGrade.TENANT, tenant))
        .where(Predicates.equals(PShopPriceGrade.UUID, grade.getUuid()))
        .build();
    return update;
  }

  private InsertStatement buildInsertStatement(String tenant, ShopPriceGrade grade) {
    if (StringUtils.isBlank(grade.getUuid())) {
      grade.setUuid(UUID.randomUUID().toString());
    }

    return new InsertBuilder().table(PShopPriceGrade.TABLE_NAME)
        .addValues(PShopPriceGrade.forSaveNew(grade))
        .addValue(PShopPriceGrade.TENANT, tenant)
        .addValue(PShopPriceGrade.ORG_ID, grade.getOrgId())
        .addValue(PShopPriceGrade.SHOP, grade.getShop())
        .addValue(PShopPriceGrade.SOURCE, grade.getSource())
        .addValue(PShopPriceGrade.SKU_GROUP, grade.getSkuGroup())
        .addValue(PShopPriceGrade.SKU_POSITION, grade.getSkuPosition())
        .addValue(PShopPriceGrade.PRICE_GRADE, grade.getPriceGrade())
        .addValue(PShopPriceGrade.SOURCE_CREATE_TIME, grade.getSourceCreateTime())
        .addValue(PShopPriceGrade.CREATE_INFO_TIME, new Date())
        .addValue(PShopPriceGrade.LAST_MODIFY_INFO_TIME, new Date())
        .build();
  }

}
