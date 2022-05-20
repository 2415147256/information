package com.hd123.baas.sop.service.dao.todo;

import com.hd123.baas.sop.service.api.todo.*;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.*;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TodoSceneDao {

  private static final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TodoScene.class, PTodoScene.class)
      .addConditionProcessor(new QueryConditionProcessor(){
        @Override
        public Predicate process(QueryCondition condition, QueryProcessContext queryProcessContext) throws IllegalArgumentException, QueryProcessException {
          if (StringUtils.equals(condition.getOperation(), TodoScene.Queries.KEYWORD_LIKE)) {
            String keyword = (String) condition.getParameter();
            Predicate codeLike = Predicates.like(PTodoScene.CODE, keyword);
            Predicate nameLike = Predicates.like(PTodoScene.NAME, keyword);
            return Predicates.and(Predicates.or(codeLike, nameLike));
          }
          return null;
        }
      }).build();


  private static TEMapper<TodoScene> MAPPER = TEMapperBuilder.of(TodoScene.class, PTodoScene.class)
      .primaryKey(PTodoScene.UUID)
      .map("source", PTodoScene.SOURCE, EnumConverters.toString(SourceEnum.class),
          EnumConverters.toEnum(SourceEnum.class))
      .map("target", PTodoScene.TARGET, EnumConverters.toString(TargetTypeEnum.class),
          EnumConverters.toEnum(TargetTypeEnum.class))
      .build();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public QueryResult<TodoScene> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    JdbcPagingQueryExecutor<TodoScene> executor = new JdbcPagingQueryExecutor<>(jdbcTemplate);
    qd.addByField(TodoScene.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, MAPPER);
  }

  public int insert(String tenant, TodoScene entity) {
    Assert.hasText(tenant, "tenant");
    entity.setTenant(tenant);

    InsertStatement insert = new InsertBuilder().table(PTodoScene.TABLE_NAME)
        .addValues(MAPPER.forInsert(entity, true))
        .build();
    return jdbcTemplate.update(insert);
  }

  public int delete(String tenant, String uuid) {
    Assert.notNull(uuid, "uuid");
    Assert.hasText(tenant, "tenant");
    DeleteStatement delete = new DeleteBuilder().table(PTodoScene.TABLE_NAME)
        .where(Predicates.equals(PTodoScene.UUID, uuid))
        .where(Predicates.equals(PTodoScene.TENANT, tenant))
        .build();
    return jdbcTemplate.update(delete);
  }

  public TodoScene get(String tenant, String uuid,boolean forUpdate) {
    Assert.notNull(uuid, "uuid");
    Assert.hasText(tenant, "tenant");
    SelectStatement select = new SelectBuilder().from(PTodoScene.TABLE_NAME)
        .where(Predicates.equals(PTodoScene.UUID, uuid))
        .where(Predicates.equals(PTodoScene.TENANT, tenant))
        .build();
    if(forUpdate){
      select.forUpdate();
    }
    List<TodoScene> records = jdbcTemplate.query(select, MAPPER);
    if (CollectionUtils.isNotEmpty(records)) {
      return records.get(0);
    }
    return null;
  }
  public TodoScene getByCode(String tenant, String orgId,String code,boolean forUpdate) {

    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.hasText(code,"code");
    SelectStatement select = new SelectBuilder().from(PTodoScene.TABLE_NAME)
        .where(Predicates.equals(PTodoScene.TENANT, tenant))
        .where(Predicates.equals(PTodoScene.ORG_ID, orgId))
        .where(Predicates.equals(PTodoScene.CODE, code))
        .build();
    if(forUpdate){
      select.forUpdate();
    }
    List<TodoScene> records = jdbcTemplate.query(select, MAPPER);
    if (CollectionUtils.isNotEmpty(records)) {
      return records.get(0);
    }
    return null;
  }


  public int update(String tenant, TodoScene entity) {
    Assert.hasText(tenant, "tenant");
    entity.setTenant(tenant);
    UpdateStatement update = new UpdateBuilder().table(PTodoScene.TABLE_NAME)
        .addValues(MAPPER.forUpdate(entity))
        .where(Predicates.equals(PTodoScene.UUID, entity.getUuid()))
        .where(Predicates.equals(PTodoScene.TENANT, tenant))
        .build();
    return jdbcTemplate.update(update);
  }
}
