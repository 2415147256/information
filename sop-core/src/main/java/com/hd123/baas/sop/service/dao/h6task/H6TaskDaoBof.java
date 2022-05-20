package com.hd123.baas.sop.service.dao.h6task;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Repository
public class H6TaskDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(H6Task.class, PH6Task.class).build();

  public QueryResult<H6Task> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(H6Task.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new H6TaskMapper());
  }

  public H6Task get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectStatement select = new SelectBuilder().select(PH6Task.allColumns())
        .from(PH6Task.TABLE_NAME)
        .where(Predicates.equals(PH6Task.TENANT, tenant))
        .where(Predicates.equals(PH6Task.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new H6TaskMapper()));
  }

  public H6Task getWithLock(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectStatement select = new SelectBuilder().select(PH6Task.allColumns())
        .from(PH6Task.TABLE_NAME)
        .where(Predicates.equals(PH6Task.TENANT, tenant))
        .where(Predicates.equals(PH6Task.UUID, uuid))
        .forUpdate()
        .build();
    return getFirst(jdbcTemplate.query(select, new H6TaskMapper()));
  }

  public List<H6Task> getByDate(String tenant,String orgId, H6TaskType type, Date executeDate) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(type, "type");
    Assert.notNull(executeDate, "executeDate");
    SelectStatement select = new SelectBuilder().select(PH6Task.allColumns())
        .from(PH6Task.TABLE_NAME)
        .where(Predicates.equals(PH6Task.TENANT, tenant))
        .where(Predicates.equals(PH6Task.ORG_ID,orgId))
        .where(Predicates.equals(PH6Task.TYPE, type.name()))
        .where(Predicates.equals(PH6Task.EXECUTE_DATE, executeDate))
        .where(Predicates.notEquals(PH6Task.STATE, H6TaskState.FINISHED.name()))
        .build();
    return jdbcTemplate.query(select, new H6TaskMapper());
  }

  public String insert(String tenant, H6Task h6Task, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(h6Task, "h6Task");
    Assert.notNull(operateInfo, "operateInfo");
    InsertStatement select = buildInsertStatement(tenant, h6Task, operateInfo);
    jdbcTemplate.update(select);
    return h6Task.getUuid();
  }

  public void update(String tenant, H6Task h6Task, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(h6Task, "h6Task");
    Assert.notNull(operateInfo, "operateInfo");
    UpdateStatement update = buildUpdateStatement(tenant, h6Task, operateInfo);
    jdbcTemplate.update(update);
  }

  private InsertStatement buildInsertStatement(String tenant, H6Task h6Task, OperateInfo operateInfo)
      throws BaasException {
    h6Task.setCreateInfo(operateInfo);
    h6Task.setLastModifyInfo(operateInfo);
    if (StringUtils.isBlank(h6Task.getUuid())) {
      h6Task.setUuid(UUID.randomUUID().toString());
    }
    InsertBuilder builder = new InsertBuilder().table(PH6Task.TABLE_NAME)
        .addValues(PH6Task.forSaveNew(h6Task))
        .addValue(PH6Task.TENANT, tenant)
        .addValue(PH6Task.ORG_ID, h6Task.getOrgId())
        .addValue(PH6Task.FILE_URL, h6Task.getFileUrl())
        .addValue(PH6Task.EXECUTE_DATE, h6Task.getExecuteDate())
        .addValue(PH6Task.OCCURRED_TIME, h6Task.getOccurredTime())
        .addValue(PH6Task.TYPE, h6Task.getType().name())
        .addValue(PH6Task.STATE, h6Task.getState().name())
        .addValue(PH6Task.FLOW_NO, h6Task.getFlowNo())
        .addValue(PH6Task.ERR_MSG, h6Task.getErrMsg());
    return builder.build();
  }

  private UpdateStatement buildUpdateStatement(String tenant, H6Task h6Task, OperateInfo operateInfo)
      throws BaasException {
    h6Task.setLastModifyInfo(operateInfo);
    UpdateBuilder builder = new UpdateBuilder().table(PH6Task.TABLE_NAME)
        .addValues(PH6Task.forSaveModify(h6Task))
        .addValue(PH6Task.FILE_URL, h6Task.getFileUrl())
        .addValue(PH6Task.EXECUTE_DATE, h6Task.getExecuteDate())
        .addValue(PH6Task.OCCURRED_TIME, h6Task.getOccurredTime())
        .addValue(PH6Task.TYPE, h6Task.getType().name())
        .addValue(PH6Task.STATE, h6Task.getState().name())
        .addValue(PH6Task.FLOW_NO, h6Task.getFlowNo())
        .addValue(PH6Task.ERR_MSG, h6Task.getErrMsg())

        .where(Predicates.equals(PH6Task.TENANT, tenant))
        .where(Predicates.equals(PH6Task.UUID, h6Task.getUuid()));
    return builder.build();
  }

  public void updateErrMsg(String tenant, String uuid, String errMsg, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(operateInfo, "operateInfo");

    UpdateBuilder builder = new UpdateBuilder().table(PH6Task.TABLE_NAME)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .addValue(PH6Task.ERR_MSG, errMsg)

        .where(Predicates.equals(PH6Task.TENANT, tenant))
        .where(Predicates.equals(PH6Task.UUID, uuid));
    jdbcTemplate.update(builder.build());
  }

}
