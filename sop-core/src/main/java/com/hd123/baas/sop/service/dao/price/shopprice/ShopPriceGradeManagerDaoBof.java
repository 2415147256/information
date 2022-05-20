package com.hd123.baas.sop.service.dao.price.shopprice;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceGradeManager;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author zhengzewang on 2020/11/20.
 */
@Repository
public class ShopPriceGradeManagerDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopPriceGradeManager.class,
      PShopPriceGradeManager.class).build();

  public QueryResult<ShopPriceGradeManager> query(String tenant, QueryDefinition qd) {
    qd.addByField(ShopPriceGradeManager.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new ShopPriceGradeManagerMapper());
  }

  public void batchInsert(String tenant, Collection<ShopPriceGradeManager> managers) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(managers)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopPriceGradeManager manager : managers) {
      updater.add(buildInsertStatement(tenant, manager));
    }
    updater.update();
  }

  private InsertStatement buildInsertStatement(String tenant, ShopPriceGradeManager manager) {
    if (StringUtils.isBlank(manager.getUuid())) {
      manager.setUuid(UUID.randomUUID().toString());
    }
    return new InsertBuilder().table(PShopPriceGradeManager.TABLE_NAME)
        .addValues(PShopPriceGradeManager.forSaveNew(manager))
        .addValue(PShopPriceGradeManager.TENANT, tenant)
        .addValue(PShopPriceGradeManager.ORG_ID,manager.getOrgId())
        .addValue(PShopPriceGradeManager.SHOP, manager.getShop())
        .addValue(PShopPriceGradeManager.SKU_GROUP, manager.getSkuGroup())
        .addValue(PShopPriceGradeManager.SKU_POSITION, manager.getSkuPosition())
        .addValue(PShopPriceGradeManager.PRICE_GRADE, manager.getPriceGrade())
        .addValue(PShopPriceGradeManager.SOURCE,manager.getSource())
        .addValue(PShopPriceGradeManager.SOURCE_CREATE_TIME, manager.getSourceCreateTime())
        .addValue(PShopPriceGradeManager.EFFECTIVE_START_DATE, manager.getEffectiveStartDate())
        .addValue(PShopPriceGradeManager.CREATE_INFO_TIME, new Date())
        .addValue(PShopPriceGradeManager.LAST_MODIFY_INFO_TIME, new Date())
        .build();
  }

  public void batchDelete(String tenant, Collection<String> uuids) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(uuids)) {
      return;
    }
    DeleteStatement delete = new DeleteBuilder().table(PShopPriceGradeManager.TABLE_NAME)
        .where(Predicates.equals(PShopPriceGradeManager.TENANT, tenant))
        .where(Predicates.in2(PShopPriceGradeManager.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

}
