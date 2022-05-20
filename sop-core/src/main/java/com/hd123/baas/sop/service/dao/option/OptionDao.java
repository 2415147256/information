package com.hd123.baas.sop.service.dao.option;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.option.Option;
import com.hd123.baas.sop.service.dao.PStandardEntity;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OptionDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(Option.class,
      POption.class).build();


  public QueryResult<Option> query (String tenant, QueryDefinition qd) {
    Assert.hasText(tenant);
    Assert.notNull(qd);

    if (!StringUtil.isNullOrBlank(tenant)) {
      qd.addByField(Option.Queries.TENANT, Cop.EQUALS,tenant);
    }
    SelectStatement statement = QUERY_PROCESSOR.process(qd);
    return executor.query(statement, new OptionMapper());
  }

  public List<Option> list(String tenant, String type, List<String> keys) {
    Assert.hasText(tenant);
    Assert.hasText(type);
    Assert.notEmpty(keys);

    SelectBuilder select = new SelectBuilder()
        .from(POption.TABLE_NAME, POption.TABLE_ALIAS)
        .where(Predicates.equals(POption.TENANT, tenant))
        .where(Predicates.equals(POption.TYPE, type))
        .where(Predicates.in2(POption.OP_KEY, keys.toArray()));

    List<Option> list = jdbcTemplate.query(select.build(), new OptionMapper());

    return CollectionUtils.isEmpty(list) ? new ArrayList<>() : list;

  }

  public Option get(String tenant, String type, String key) {
    Assert.hasText(tenant);
    Assert.hasText(type);
    Assert.hasText(key);

    SelectBuilder select = new SelectBuilder()
        .from(POption.TABLE_NAME, POption.TABLE_ALIAS)
        .where(Predicates.equals(POption.TENANT, tenant))
        .where(Predicates.equals(POption.TYPE, type))
        .where(Predicates.equals(POption.OP_KEY,key));

    List<Option> list = jdbcTemplate.query(select.build(), new OptionMapper());
    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);
  }

  public Option get(String tenant, String uuid) {
    return get(tenant, uuid, false);
  }

  public Option get(String tenant, String uuid,boolean forUpdate) {

    Assert.hasText(tenant);
    Assert.hasText(uuid);

    SelectBuilder select = new SelectBuilder()
        .from(POption.TABLE_NAME, POption.TABLE_ALIAS)
        .where(Predicates.equals(POption.TENANT, tenant))
        .where(Predicates.equals(POption.UUID, uuid));

    if (forUpdate) {
      select.forUpdate();
    }

    List<Option> list = jdbcTemplate.query(select.build(), new OptionMapper());

    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);

  }

  @Tx
  public void saveNew(Option entity) {
    Assert.notNull(entity);

    if (StringUtils.isEmpty(entity.getUuid())) {
      entity.setUuid(IdGenUtils.buildRdUuid());
    }

    InsertStatement insertStatement = new InsertBuilder().table(POption.TABLE_NAME)
        .addValues(POption.toFieldValues(entity))
        .build();

    jdbcTemplate.update(insertStatement);

  }

  @Tx
  public void update(Option entity) {
    Assert.notNull(entity);
    Assert.hasText(entity.getUuid());

    UpdateStatement updateStatement = new UpdateBuilder().table(POption.TABLE_NAME)
        .setValues(POption.toFieldValues(entity))
        .where(Predicates.equals(POption.TYPE, entity.getType().name()))
        .where(Predicates.equals(POption.TENANT, entity.getTenant()))
        .where(Predicates.equals(POption.OP_KEY, entity.getOpKey()))
        .build();

    jdbcTemplate.update(updateStatement);
  }

  @Tx
  public void batchSaveNew(List<Option> entities) {
    Assert.notEmpty(entities);

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);

    for (Option entity : entities) {
      if (StringUtils.isEmpty(entity.getUuid())) {
        entity.setUuid(IdGenUtils.buildRdUuid());
      }

      InsertStatement insertStatement  = new InsertBuilder().table(POption.TABLE_NAME)
          .addValues(POption.toFieldValues(entity))
          .build();
      batchUpdater.add(insertStatement);
    }


    batchUpdater.update();

  }

  @Tx
  public void batchUpdate(List<Option> entities) {
    Assert.notEmpty(entities);

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);

    for (Option entity : entities) {

      UpdateStatement updateStatement  = new UpdateBuilder().table(POption.TABLE_NAME)
          .setValues(POption.toFieldValues(entity))
          .where(Predicates.equals(POption.TYPE, entity.getType().name()))
          .where(Predicates.equals(POption.TENANT, entity.getTenant()))
          .where(Predicates.equals(POption.OP_KEY, entity.getOpKey()))
          .build();
      batchUpdater.add(updateStatement);
    }
    batchUpdater.update();

  }

  public void delete(String tenant, String type,List<String> keys) {
    Assert.hasText(tenant);
    Assert.hasText(type);
    Assert.notEmpty(keys);

    DeleteStatement delete = new DeleteBuilder()
        .table(POption.TABLE_NAME)
        .where(Predicates.equals(POption.TENANT, tenant))
        .where(Predicates.equals(POption.TYPE, type))
        .where(Predicates.in2(POption.OP_KEY, keys.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public void batchUpdate(String tenant, List<Option> entities, OperateInfo operateInfo) {
    Assert.notEmpty(entities);

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);

    for (Option entity : entities) {

      UpdateStatement updateStatement = new UpdateBuilder().table(POption.TABLE_NAME)
          .setValue(POption.OP_VALUE, entity.getOpValue())
          .setValue(PStandardEntity.LAST_MODIFIER_ID, operateInfo.getTime())
          .setValue(PStandardEntity.LAST_MODIFIER_ID, operateInfo.getOperator().getId())
          .setValue(PStandardEntity.LAST_MODIFIER_NAME, operateInfo.getOperator().getFullName())
          .setValue(PStandardEntity.LAST_MODIFIER_NAMESPACE, operateInfo.getOperator().getNamespace())
          .where(Predicates.equals(POption.TYPE, entity.getType().name()))
          .where(Predicates.equals(POption.TENANT, tenant))
          .where(Predicates.equals(POption.OP_KEY, entity.getOpKey()))
          .build();
      batchUpdater.add(updateStatement);
    }
    batchUpdater.update();

  }
}
