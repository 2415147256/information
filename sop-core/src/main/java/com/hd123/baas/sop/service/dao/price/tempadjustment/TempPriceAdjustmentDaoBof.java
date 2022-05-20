package com.hd123.baas.sop.service.dao.price.tempadjustment;

import com.hd123.rumba.commons.biz.query.Cop;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustment;
import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustmentState;
import com.hd123.baas.sop.service.dao.price.priceadjustment.PPriceAdjustment;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class TempPriceAdjustmentDaoBof extends BofBaseDao {
  private static QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TempPriceAdjustment.class,
      PTempPriceAdjustment.class).build();

  public void insert(String tenant, TempPriceAdjustment temp, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(temp, "temp");
    temp.setCreateInfo(operateInfo);
    temp.setLastModifyInfo(operateInfo);
    InsertStatement insert = new InsertBuilder().table(PTempPriceAdjustment.TABLE_NAME)
        .addValue(PTempPriceAdjustment.TENANT, tenant)
        .addValue(PTempPriceAdjustment.ORG_ID, temp.getOrgId())
        .addValue(PTempPriceAdjustment.FLOW_NO, temp.getFlowNo())
        .addValue(PTempPriceAdjustment.EFFECTIVE_START_DATE, temp.getEffectiveStartDate())
        .addValue(PTempPriceAdjustment.REASON, temp.getReason())
        .addValue(PTempPriceAdjustment.STATE, temp.getState().name())
        .addValues(PTempPriceAdjustment.forSaveNew(temp))
        .build();
    jdbcTemplate.update(insert);
  }

  public void update(String tenant, TempPriceAdjustment temp, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(temp, "adjustment");
    temp.setLastModifyInfo(operateInfo);
    UpdateStatement updateBuilder = new UpdateBuilder().table(PTempPriceAdjustment.TABLE_NAME)
        .addValue(PTempPriceAdjustment.FLOW_NO, temp.getFlowNo())
        .addValue(PTempPriceAdjustment.EFFECTIVE_START_DATE, temp.getEffectiveStartDate())
        .addValue(PTempPriceAdjustment.REASON, temp.getReason())
        .addValue(PTempPriceAdjustment.STATE, temp.getState().name())
        .addValues(PTempPriceAdjustment.forSaveModify(temp))
        .where(Predicates.equals(PTempPriceAdjustment.TENANT, tenant))
        .where(Predicates.equals(PTempPriceAdjustment.UUID, temp.getUuid()))
        .build();
    jdbcTemplate.update(updateBuilder);
  }

  public TempPriceAdjustment get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectStatement selectStatement = new SelectBuilder().from(PTempPriceAdjustment.TABLE_NAME)
        .where(Predicates.equals(PTempPriceAdjustment.TENANT, tenant))
        .where(Predicates.equals(PTempPriceAdjustment.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(selectStatement, new TempPriceAdjustmentMapper()));
  }

  public void changeState(String tenant, String uuid, TempPriceAdjustmentState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(state, "state");
    UpdateStatement updateBuilder = new UpdateBuilder().table(PTempPriceAdjustment.TABLE_NAME)
        .addValue(PTempPriceAdjustment.STATE, state.name())
        .addValues(PPriceAdjustment.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PTempPriceAdjustment.TENANT, tenant))
        .where(Predicates.equals(PTempPriceAdjustment.UUID, uuid))
        .build();
    jdbcTemplate.update(updateBuilder);
  }

  public QueryResult<TempPriceAdjustment> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(TempPriceAdjustment.Queries.TENANT, Cop.EQUALS,tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new TempPriceAdjustmentMapper());
  }
}
