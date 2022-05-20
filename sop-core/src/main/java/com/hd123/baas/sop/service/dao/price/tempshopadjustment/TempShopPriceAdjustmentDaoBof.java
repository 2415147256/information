package com.hd123.baas.sop.service.dao.price.tempshopadjustment;

import com.alibaba.druid.util.StringUtils;
import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustment;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustmentState;
import com.hd123.baas.sop.service.dao.price.tempadjustment.PTempPriceAdjustment;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class TempShopPriceAdjustmentDaoBof extends BofBaseDao {

  private static QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TempShopPriceAdjustment.class,
      TempShopPriceAdjustment.Schema.class).build();

  private static final TEMapper<TempShopPriceAdjustment> TEMP_SHOP_PRICE_ADJUSTMENT_TE_MAPPER = TEMapperBuilder
      .of(TempShopPriceAdjustment.class, TempShopPriceAdjustment.Schema.class)
      .primaryKey(TempShopPriceAdjustment.Schema.UUID)
      .map("state", TempShopPriceAdjustment.Schema.STATE, EnumConverters.toString(TempShopPriceAdjustmentState.class),
          EnumConverters.toEnum(TempShopPriceAdjustmentState.class))
      .build();

  public void insert(String tenant, TempShopPriceAdjustment temp, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    if (StringUtils.isEmpty(temp.getUuid())) {
      temp.setUuid(UUID.randomUUID().toString());
    }
    temp.setCreateInfo(operateInfo);
    temp.setLastModifyInfo(operateInfo);

    InsertStatement insertStatement = new InsertBuilder().table(TempShopPriceAdjustment.Schema.TABLE_NAME)
        .addValues(TEMP_SHOP_PRICE_ADJUSTMENT_TE_MAPPER.forInsert(temp))
        .build();
    jdbcTemplate.update(insertStatement);
  }

  public void update(String tenant, TempShopPriceAdjustment temp, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    if (StringUtils.isEmpty(temp.getUuid())) {
      temp.setUuid(UUID.randomUUID().toString());
    }
    temp.setLastModifyInfo(operateInfo);

    UpdateStatement build = new UpdateBuilder().table(TempShopPriceAdjustment.Schema.TABLE_NAME)
        .addValues(TEMP_SHOP_PRICE_ADJUSTMENT_TE_MAPPER.forUpdate(temp))
        .where(Predicates.equals(TempShopPriceAdjustment.Schema.TENANT, tenant))
        .where(Predicates.equals(TempShopPriceAdjustment.Schema.UUID, temp.getUuid()))
        .build();
    jdbcTemplate.update(build);
  }

  public TempShopPriceAdjustment get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectStatement selectStatement = new SelectBuilder().from(TempShopPriceAdjustment.Schema.TABLE_NAME)
        .where(Predicates.equals(PTempPriceAdjustment.TENANT, tenant))
        .where(Predicates.equals(PTempPriceAdjustment.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(selectStatement, TEMP_SHOP_PRICE_ADJUSTMENT_TE_MAPPER));
  }

  public void changeState(String tenant, String uuid, TempShopPriceAdjustmentState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(state, "state");
    UpdateStatement updateBuilder = new UpdateBuilder().table(TempShopPriceAdjustment.Schema.TABLE_NAME)
        .setValue(TempShopPriceAdjustment.Schema.STATE, state.name())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_TIME, operateInfo.getTime())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_OPERATOR_FULL_NAME,
            operateInfo.getOperator().getFullName())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_OPERATOR_NAMESPACE,
            operateInfo.getOperator().getNamespace())
        .where(Predicates.equals(TempShopPriceAdjustment.Schema.TENANT, tenant))
        .where(Predicates.equals(TempShopPriceAdjustment.Schema.UUID, uuid))
        .build();
    jdbcTemplate.update(updateBuilder);
  }

  public QueryResult<TempShopPriceAdjustment> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(TempShopPriceAdjustment.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, TEMP_SHOP_PRICE_ADJUSTMENT_TE_MAPPER);
  }

  public void cancel(String tenant, String orgId, Date executeDate, String uuid, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(executeDate, "executeDate");
    UpdateStatement updateBuilder = new UpdateBuilder().table(TempShopPriceAdjustment.Schema.TABLE_NAME)
        .setValue(TempShopPriceAdjustment.Schema.STATE, TempShopPriceAdjustmentState.CANCELED.name())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_TIME, operateInfo.getTime())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_OPERATOR_FULL_NAME,
            operateInfo.getOperator().getFullName())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_OPERATOR_NAMESPACE,
            operateInfo.getOperator().getNamespace())
        .where(Predicates.equals(TempShopPriceAdjustment.Schema.TENANT, tenant))
        .where(Predicates.equals(TempShopPriceAdjustment.Schema.ORG_ID, orgId))
        .where(Predicates.lessOrEquals(TempShopPriceAdjustment.Schema.EFFECTIVE_START_DATE, executeDate))
        .where(Predicates.notEquals(TempShopPriceAdjustment.Schema.UUID, uuid))
        .build();
    jdbcTemplate.update(updateBuilder);
  }
}
