package com.hd123.baas.sop.service.dao.subsidyplan;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.service.api.subsidyplan.DeductionRecord;
import com.hd123.baas.sop.service.api.subsidyplan.DeductionState;
import com.hd123.baas.sop.service.api.subsidyplan.DeductionType;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlan;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;

/**
 * @author liuhaoxin
 */
@Repository
public class DeductionRecordDaoBof extends BofBaseDao {

  private static final TEMapper<DeductionRecord> DEDUCTION_RECORD_MAPPER = TEMapperBuilder
      .of(DeductionRecord.class, DeductionRecord.DeductionRecordSchema.class)
      .map("activityType", DeductionRecord.DeductionRecordSchema.ACTIVITY_TYPE,
          EnumConverters.toString(ActivityType.class), EnumConverters.toEnum(ActivityType.class))
      .map("type", DeductionRecord.DeductionRecordSchema.TYPE, EnumConverters.toString(DeductionType.class),
          EnumConverters.toEnum(DeductionType.class))
      .map("state", DeductionRecord.DeductionRecordSchema.STATE, EnumConverters.toString(DeductionState.class),
          EnumConverters.toEnum(DeductionState.class))
      .primaryKey(DeductionRecord.DeductionRecordSchema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(DeductionRecord.class,
      DeductionRecord.DeductionRecordSchema.class).addConditionProcessor((condition, context) -> {
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(DeductionRecord.Queries.DETAIL_TYPE_EQUALS, condition.getOperation())) {
          String value = (String) condition.getParameter();

          SelectStatement select = new SelectBuilder().select("1")
              .from(DeductionRecord.DeductionRecordSchema.TABLE_NAME, DeductionRecord.DeductionRecordSchema.TABLE_ALIAS)
              .where(Predicates.equals(DeductionRecord.DeductionRecordSchema.TABLE_ALIAS,
                  DeductionRecord.DeductionRecordSchema.UUID, alias, DeductionRecord.DeductionRecordSchema.UUID))
              .where(Predicates.equals(DeductionRecord.DeductionRecordSchema.TABLE_ALIAS,
                  DeductionRecord.DeductionRecordSchema.TENANT, alias, DeductionRecord.DeductionRecordSchema.TENANT))
              .build();

          // 扣款 收入Income 支出expend
          if ("INCOME".equalsIgnoreCase(value)) {
            select.where(Predicates.greater(DeductionRecord.DeductionRecordSchema.TABLE_ALIAS,
                SubsidyPlan.SubsidyPlanSchema.AMOUNT, 0));
          } else {
            select.where(Predicates.less(DeductionRecord.DeductionRecordSchema.TABLE_ALIAS,
                SubsidyPlan.SubsidyPlanSchema.AMOUNT, 0));
          }
          return Predicates.exists(select);
        }
        return null;
      }).build();

  public void saveNew(String tenant, DeductionRecord deductionRecord, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(deductionRecord, "扣款记录");

    deductionRecord.setTenant(tenant);
    deductionRecord.setCreated(operateInfo.getTime());
    InsertStatement insert = new InsertBuilder().table(DeductionRecord.DeductionRecordSchema.TABLE_NAME)
        .values(DEDUCTION_RECORD_MAPPER.forInsert(deductionRecord, true))
        .build();
    jdbcTemplate.update(insert);
  }

  public void batchSaveNew(String tenant, List<DeductionRecord> deductionRecords, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(deductionRecords, "扣款记录");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (DeductionRecord deductionRecord : deductionRecords) {
      deductionRecord.setTenant(tenant);
      deductionRecord.setUuid(UUID.randomUUID().toString());
      deductionRecord.setCreated(operateInfo.getTime());
      InsertStatement insert = new InsertBuilder().table(DeductionRecord.DeductionRecordSchema.TABLE_NAME)
          .values(DEDUCTION_RECORD_MAPPER.forInsert(deductionRecord, true))
          .build();
      updater.add(insert);
    }
    updater.update();
    }

    public void batchUpdate(String tenant, List<DeductionRecord> deductionRecords, OperateInfo operateInfo) {
      Assert.notNull(tenant, "租户");
      Assert.notNull(deductionRecords, "扣款记录");

      BatchUpdater updater = new BatchUpdater(jdbcTemplate);
      for (DeductionRecord deductionRecord : deductionRecords) {
        deductionRecord.setTenant(tenant);
        deductionRecord.setCreated(operateInfo.getTime());
        UpdateStatement update = new UpdateBuilder().table(DeductionRecord.DeductionRecordSchema.TABLE_NAME)
            .addValues(DEDUCTION_RECORD_MAPPER.forUpdate(deductionRecord))
            .where(Predicates.equals(DeductionRecord.DeductionRecordSchema.UUID, deductionRecord.getUuid()))
            .build();
        updater.add(update);
      }
      updater.update();
    }

  public QueryResult<DeductionRecord> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "扣款记录");

    qd.addByField(DeductionRecord.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, DEDUCTION_RECORD_MAPPER);
  }

  public List<DeductionRecord> listByState(String tenant, DeductionState state) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(state, "state");

    SelectStatement select = new SelectBuilder().from(DeductionRecord.DeductionRecordSchema.TABLE_NAME)
        .where(Predicates.equals(DeductionRecord.DeductionRecordSchema.TENANT, tenant),
            Predicates.equals(DeductionRecord.DeductionRecordSchema.STATE, state.name()))
        .build();
    return jdbcTemplate.query(select, DEDUCTION_RECORD_MAPPER);
  }
}
