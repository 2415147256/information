package com.hd123.baas.sop.service.dao.todo;

import com.hd123.baas.sop.service.api.todo.CategoryEnum;
import com.hd123.baas.sop.service.api.todo.ShelfStateEnum;
import com.hd123.baas.sop.service.api.todo.SourceEnum;
import com.hd123.baas.sop.service.api.todo.TargetTypeEnum;
import com.hd123.baas.sop.service.api.todo.TodoSetting;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessContext;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessException;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicate;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
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
public class TodoSettingDao {

  private static String alias;

  private static final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TodoSetting.class, PSetting.class)
      .addConditionProcessor(new QueryConditionProcessor() {
        @Override
        public Predicate process(QueryCondition condition, QueryProcessContext queryProcessContext)
            throws IllegalArgumentException, QueryProcessException {
          alias = queryProcessContext.getPerzAlias();
          if (StringUtils.equals(condition.getOperation(), TodoSetting.Queries.KEYWORD_LIKE)) {
            String keyword = (String) condition.getParameter();
            Predicate idLike = Predicates.like(alias, PSetting.ID, keyword);
            Predicate nameLike = Predicates.like(alias, PSetting.SETTING_NAME, keyword);
            return Predicates.and(Predicates.or(idLike, nameLike));
          }
          if (StringUtils.equals(condition.getOperation(), TodoSetting.Queries.SCENE_NAME_LIKE)) {
            String scene = (String) condition.getParameter();
            return Predicates.like(PTodoScene.TABLE_ALIAS, PTodoScene.NAME, scene);
          }
          if (StringUtils.equals(condition.getOperation(), TodoSetting.Queries.SOURCE_NAME_EQUALS)) {
            String source = (String) condition.getParameter();
            return Predicates.equals(PTodoScene.TABLE_ALIAS, PTodoScene.SOURCE, source);
          }
          return null;
        }
      })
      .build();

  private static TEMapper<TodoSetting> MAPPER = TEMapperBuilder.of(TodoSetting.class, PTodoSetting.class)
      .primaryKey(PTodoSetting.UUID)
      .map("state", PTodoSetting.STATE, EnumConverters.toString(ShelfStateEnum.class),
          EnumConverters.toEnum(ShelfStateEnum.class))
      .map("category", PTodoSetting.CATEGORY, EnumConverters.toString(CategoryEnum.class),
          EnumConverters.toEnum(CategoryEnum.class))
      .build();
  private static TEMapper<TodoSetting> QUERY_MAPPER = TEMapperBuilder.of(TodoSetting.class, PSetting.class)
      .primaryKey(PSetting.UUID)
      .map("state", PTodoSetting.STATE, EnumConverters.toString(ShelfStateEnum.class),
          EnumConverters.toEnum(ShelfStateEnum.class))
      .map("source", PSetting.SOURCE, EnumConverters.toString(SourceEnum.class),
          EnumConverters.toEnum(SourceEnum.class))
      .map("target", PSetting.TARGET, EnumConverters.toString(TargetTypeEnum.class),
          EnumConverters.toEnum(TargetTypeEnum.class))
      .map("category", PTodoSetting.CATEGORY, EnumConverters.toString(CategoryEnum.class),
          EnumConverters.toEnum(CategoryEnum.class))
      .build();
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public QueryResult<TodoSetting> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    JdbcPagingQueryExecutor<TodoSetting> executor = new JdbcPagingQueryExecutor<>(jdbcTemplate);
    qd.addByField(TodoSetting.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    innerJoin(alias, select);
    return executor.query(select, QUERY_MAPPER);
  }

  public int insert(String tenant, TodoSetting entity) {
    Assert.hasText(tenant, "tenant");
    entity.setTenant(tenant);
    InsertStatement insert = new InsertBuilder().table(PTodoSetting.TABLE_NAME)
        .addValues(MAPPER.forInsert(entity, true))
        .build();
    return jdbcTemplate.update(insert);
  }

  public int delete(String tenant, String uuid) {
    Assert.notNull(uuid, "uuid");
    Assert.hasText(tenant, "tenant");
    DeleteStatement delete = new DeleteBuilder().table(PTodoSetting.TABLE_NAME)
        .where(Predicates.equals(PTodoSetting.UUID, uuid))
        .where(Predicates.equals(PTodoSetting.TENANT, tenant))
        .build();
    return jdbcTemplate.update(delete);
  }

  public TodoSetting get(String tenant, String uuid, boolean forUpdate) {
    Assert.notNull(uuid, "uuid");
    Assert.hasText(tenant, "tenant");
    SelectStatement select = new SelectBuilder().from(PTodoSetting.TABLE_NAME, PTodoSetting.TABLE_ALIAS)
        .where(Predicates.equals(PTodoSetting.TABLE_ALIAS, PTodoSetting.UUID, uuid))
        .where(Predicates.equals(PTodoSetting.TABLE_ALIAS, PTodoSetting.TENANT, tenant))
        .build();
    innerJoin(PTodoSetting.TABLE_ALIAS, select);
    select.select(PTodoSetting.TABLE_ALIAS + ".*");
    if (forUpdate) {
      select.forUpdate();
    }
    List<TodoSetting> records = jdbcTemplate.query(select, QUERY_MAPPER);
    if (CollectionUtils.isNotEmpty(records)) {
      return records.get(0);
    }
    return null;
  }

  public int update(String tenant, TodoSetting entity) {
    Assert.hasText(tenant, "tenant");
    entity.setTenant(tenant);
    UpdateStatement update = new UpdateBuilder().table(PTodoSetting.TABLE_NAME)
        .addValues(MAPPER.forUpdate(entity))
        .where(Predicates.equals(PTodoSetting.UUID, entity.getUuid()))
        .where(Predicates.equals(PTodoSetting.TENANT, tenant))
        .build();
    return jdbcTemplate.update(update);
  }

  private void innerJoin(String alias, SelectStatement select) {
    select.innerJoin(PTodoScene.TABLE_NAME, PTodoScene.TABLE_ALIAS,
        Predicates.equals(alias, PSetting.SCENE_ID, PTodoScene.TABLE_ALIAS, PTodoScene.UUID));
    select.select(PTodoScene.TABLE_ALIAS + "." + PSetting.NAME, PTodoScene.TABLE_ALIAS + "." + PSetting.CODE,
        PTodoScene.TABLE_ALIAS + "." + PSetting.SOURCE, PTodoScene.TABLE_ALIAS + "." + PSetting.SOURCE_EXT,
        PTodoScene.TABLE_ALIAS + "." + PSetting.TARGET, PTodoScene.TABLE_ALIAS + "." + PSetting.TARGET_EXT,
        PTodoScene.TABLE_ALIAS + "." + PSetting.FINISH_CONDITION, PTodoScene.TABLE_ALIAS + "." + PSetting.IS_USED,
        PTodoScene.TABLE_ALIAS + "." + PSetting.URL_EXT);
  }

  public List<TodoSetting> listBySceneId(String tenant, String orgId, String sceneId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.hasText(sceneId, "sceneId");

    SelectStatement select = new SelectBuilder().from(PTodoSetting.TABLE_NAME)
        .where(Predicates.equals(PTodoSetting.TENANT, tenant))
        .where(Predicates.equals(PTodoSetting.ORG_ID, orgId))
        .where(Predicates.equals(PTodoSetting.SCENE_ID, sceneId))
        .where(Predicates.equals(PTodoSetting.STATE, ShelfStateEnum.UP.name()))
        .build();

    return jdbcTemplate.query(select, MAPPER);
  }
}
