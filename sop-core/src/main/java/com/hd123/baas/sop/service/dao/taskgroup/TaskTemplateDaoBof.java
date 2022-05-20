package com.hd123.baas.sop.service.dao.taskgroup;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.taskgroup.TaskTemplate;
import com.hd123.baas.sop.service.api.taskgroup.TaskTemplateMaxSeq;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author guyahui
 * @date 2021/4/29 20:18
 */
@Slf4j
@Repository
public class TaskTemplateDaoBof extends BofBaseDao {
  @Autowired
  protected JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;
  private static final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TaskTemplate.class,
      PTaskTemplate.class).build();
  public static final TEMapper<TaskTemplate> MAPPER = TEMapperBuilder.of(TaskTemplate.class, PTaskTemplate.class)
      .primaryKey(PTaskTemplate.UUID)
      .build();

  public static final TEMapper<TaskTemplateMaxSeq> MAX_SEQ_MAPPER = TEMapperBuilder.of(TaskTemplateMaxSeq.class, PTaskTemplateMaxSeq.class)
      .build();

  public List<TaskTemplate> listByOwners(String tenant, List<String> taskGroupIds) {
    Assert.notEmpty(tenant, "tenant");
    Assert.notEmpty(taskGroupIds, "taskGroupIds");
    SelectStatement selectStatement = new SelectBuilder().from(PTaskTemplate.TABLE_NAME)
        .where(Predicates.in2(PTaskTemplate.OWNER, taskGroupIds.toArray()))
        .where(Predicates.equals(PTaskTemplate.TENANT, tenant))
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  /**
   * 修改时间 2021-06-29 修改人：zhuyuntao 修改内容: 增加接口默认排序：排序方式为按照 巡检内容 增序排序
   *
   * @param tenant 租户
   * @param owner  主题ID
   * @return 巡检内容列表
   */
  public List<TaskTemplate> listByOwner(String tenant, String owner) {

    Assert.notEmpty(tenant, "tenant");
    Assert.notEmpty(owner, "owner");

    SelectStatement selectStatement = new SelectBuilder().from(PTaskTemplate.TABLE_NAME)
        .where(Predicates.equals(PTaskTemplate.OWNER, owner))
        .where(Predicates.equals(PTaskTemplate.TENANT, tenant))
        .orderBy(PTaskTemplate.SEQ)
        .orderBy(PTaskTemplate.CREATE_INFO_TIME)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public TaskTemplate getByOwnerAndName(String tenant, String owner, String name) {
    Assert.notEmpty(tenant, "tenant");
    Assert.notEmpty(owner, "owner");
    Assert.notEmpty(name, "name");

    SelectStatement selectStatement = new SelectBuilder().from(PTaskTemplate.TABLE_NAME)
        .where(Predicates.equals(PTaskTemplate.OWNER, owner))
        .where(Predicates.equals(PTaskTemplate.TENANT, tenant))
        .where(Predicates.equals(PTaskTemplate.NAME, name))
        .build();
    final List<TaskTemplate> query = jdbcTemplate.query(selectStatement, MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public int saveNew(String tenant, TaskTemplate taskTemplate, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskTemplate, "taskTemplate");
    InsertStatement insertStatement = buildInsertStatement(tenant, taskTemplate, operateInfo);
    return jdbcTemplate.update(insertStatement);
  }

  public int saveModifyTaskTemplate(String tenant, TaskTemplate taskTemplate, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(taskTemplate.getContent(), "content");
    Assert.notNull(taskTemplate.getScore(), "score");
    Assert.notEmpty(taskTemplate.getName(), "name");
    Assert.notEmpty(taskTemplate.getUuid(), "uuid");

    UpdateStatement updateStatement = buildUpdateStatement(tenant, taskTemplate, operateInfo);
    return jdbcTemplate.update(updateStatement);
  }

  public void delete(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    DeleteBuilder delete = new DeleteBuilder().table(PTaskTemplate.TABLE_NAME)
        .where(Predicates.equals(PTaskTemplate.TENANT, tenant))
        .where(Predicates.equals(PTaskTemplate.UUID, uuid));
    jdbcTemplate.update(delete.build());
  }

  /**
   * 批量添加
   *
   * @param tenant                 租户
   * @param insertTaskTemplateList 巡检内同列表
   * @param operateInfo            操作人信息
   */
  public void saveBatch(String tenant, Collection<TaskTemplate> insertTaskTemplateList, OperateInfo operateInfo) {
    Assert.notEmpty(tenant, "tenant");
    if (CollectionUtils.isEmpty(insertTaskTemplateList)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (TaskTemplate taskTemplate : insertTaskTemplateList) {
      statements.add(buildInsertStatement(tenant, taskTemplate, operateInfo));
    }
    batchUpdate(statements);
  }

  /**
   * 批量更新
   *
   * @param tenant                 租户
   * @param updateTaskTemplateList 巡检内同列表
   * @param operateInfo            操作人信息
   */
  public void updateBatch(String tenant, Collection<TaskTemplate> updateTaskTemplateList, OperateInfo operateInfo) {
    Assert.notEmpty(tenant, "tenant");
    if (CollectionUtils.isEmpty(updateTaskTemplateList)) {
      return;
    }
    List<UpdateStatement> statements = new ArrayList<>();
    for (TaskTemplate taskTemplate : updateTaskTemplateList) {
      statements.add(buildUpdateStatement(tenant, taskTemplate, operateInfo));
    }
    batchUpdate(statements);
  }

  private InsertStatement buildInsertStatement(String tenant, TaskTemplate taskTemplate, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskTemplate, "taskTemplate");
    InsertBuilder insert = new InsertBuilder().table(PTaskTemplate.TABLE_NAME)
        .addValues(PEntity.forSaveNew(taskTemplate))
        .addValue(PTaskTemplate.NAME, taskTemplate.getName())
        .addValue(PTaskTemplate.TENANT, tenant)
        .addValue(PTaskTemplate.CONTENT, taskTemplate.getContent())
        .addValue(PTaskTemplate.OWNER, taskTemplate.getOwner())
        .addValue(PTaskTemplate.FLOW_NO, taskTemplate.getFlowNo())
        .addValue(PTaskTemplate.SCORE, taskTemplate.getScore())
        .addValue(PTaskTemplate.NOTE, taskTemplate.getNote())
        .addValue(PTaskTemplate.VIDEO_NEEDED, taskTemplate.isVideoNeeded())
        .addValue(PTaskTemplate.WORD_NEEDED, taskTemplate.isWordNeeded())
        .addValue(PTaskTemplate.IMAGE_NEEDED, taskTemplate.isImageNeeded())
        .addValue(PTaskTemplate.SEQ, taskTemplate.getSeq());
    if (operateInfo != null) {
      insert.addValues(PStandardEntity.toCreateInfoFieldValues(operateInfo));
      insert.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    return insert.build();
  }

  private UpdateStatement buildUpdateStatement(String tenant, TaskTemplate taskTemplate, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(taskTemplate.getContent(), "content");
    Assert.notNull(taskTemplate.getScore(), "score");
    Assert.notEmpty(taskTemplate.getName(), "name");
    Assert.notEmpty(taskTemplate.getUuid(), "uuid");

    UpdateBuilder update = new UpdateBuilder().table(PTaskTemplate.TABLE_NAME)
        .setValue(PTaskTemplate.NAME, taskTemplate.getName())
        .setValue(PTaskTemplate.CONTENT, taskTemplate.getContent())
        .setValue(PTaskTemplate.SCORE, taskTemplate.getScore())
        .addValue(PTaskTemplate.NOTE, taskTemplate.getNote())
        .addValue(PTaskTemplate.VIDEO_NEEDED, taskTemplate.isVideoNeeded())
        .addValue(PTaskTemplate.WORD_NEEDED, taskTemplate.isWordNeeded())
        .addValue(PTaskTemplate.IMAGE_NEEDED, taskTemplate.isImageNeeded())
        .addValue(PTaskTemplate.SEQ, taskTemplate.getSeq())
        .where(Predicates.equals(PTaskTemplate.TENANT, tenant))
        .where(Predicates.equals(PTaskTemplate.UUID, taskTemplate.getUuid()));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    return update.build();
  }

  public void adjustSeq(String tenant, List<TaskTemplate> templateList, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(templateList)) {
      return;
    }
    List<UpdateStatement> statements = new ArrayList<>();
    for (TaskTemplate taskTemplate : templateList) {
      statements.add(buildUpdateSeqStatement(tenant, taskTemplate, operateInfo));
    }
    batchUpdate(statements);
  }

  private UpdateStatement buildUpdateSeqStatement(String tenant, TaskTemplate taskTemplate, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(taskTemplate.getUuid(), "uuid");
    Assert.notNull(taskTemplate.getSeq(), "seq");

    UpdateBuilder update = new UpdateBuilder().table(PTaskTemplate.TABLE_NAME)
        .setValue(PTaskTemplate.SEQ, taskTemplate.getSeq())
        .where(Predicates.equals(PTaskTemplate.TENANT, tenant))
        .where(Predicates.equals(PTaskTemplate.UUID, taskTemplate.getUuid()));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    return update.build();
  }

  public List<TaskTemplateMaxSeq> getTaskTemplateMaxSeqByOwnerList(String tenant, List<String> taskGroupIdList) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(taskGroupIdList, "taskGroupIdList");

    SelectStatement selectStatement = new SelectBuilder().select(PTaskTemplateMaxSeq.TENANT, PTaskTemplateMaxSeq.OWNER, "max(seq) as seq")
        .from(PTaskTemplateMaxSeq.TABLE_NAME)
        .where(Predicates.equals(PTaskTemplateMaxSeq.TENANT, tenant))
        .where(Predicates.in2(PTaskTemplateMaxSeq.OWNER, taskGroupIdList.toArray()))
        .groupBy(PTaskTemplateMaxSeq.OWNER, PTaskTemplateMaxSeq.TENANT).build();
    return jdbcTemplate.query(selectStatement, MAX_SEQ_MAPPER);
  }
}
