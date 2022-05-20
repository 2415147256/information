package com.hd123.baas.sop.service.dao.explosivev2.sign;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2State;
import com.hd123.baas.sop.service.dao.converter.ObjectNodeToStringConver;
import com.hd123.baas.sop.service.dao.converter.StringToObjectNodeConver;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 爆品活动(ExplosiveSignV2)表数据库访问层
 *
 * @author liuhaoxin
 * @since 2021-12-02
 */
@Repository
public class ExplosiveSignV2DaoBof extends BofBaseDao {

  private static final TEMapper<ExplosiveSignV2> MAPPER = TEMapperBuilder
      .of(ExplosiveSignV2.class, ExplosiveSignV2.Schema.class)
      .map("shop.uuid", ExplosiveSignV2.Schema.SHOP_ID)
      .map("shop.code", ExplosiveSignV2.Schema.SHOP_CODE)
      .map("shop.name", ExplosiveSignV2.Schema.SHOP_NAME)
      .map("state", ExplosiveSignV2.Schema.STATE, EnumConverters.toString(ExplosiveSignV2State.class), EnumConverters.toEnum(ExplosiveSignV2State.class))
      .map("ext", ExplosiveSignV2.Schema.EXT, new ObjectNodeToStringConver(), new StringToObjectNodeConver())
      .primaryKey(ExplosiveSignV2.Schema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ExplosiveSignV2.class,
      ExplosiveSignV2.Schema.class).build();

  public void batchInsert(String tenant, List<ExplosiveSignV2> signs, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(signs, "signs");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveSignV2 sign : signs) {
      sign.setTenant(tenant);
      sign.setCreateInfo(operateInfo);
      sign.setLastModifyInfo(operateInfo);
      InsertStatement insert = new InsertBuilder().table(ExplosiveSignV2.Schema.TABLE_NAME)
          .addValues(MAPPER.forInsert(sign))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public int updateState(String tenant, String uuid, ExplosiveSignV2State state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    UpdateStatement update = new UpdateBuilder().table(ExplosiveSignV2.Schema.TABLE_NAME)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .setValue(ExplosiveSignV2.Schema.STATE, state.name())
        .where(Predicates.equals(ExplosiveSignV2.Schema.TENANT, tenant))
        .where(Predicates.equals(ExplosiveSignV2.Schema.UUID, uuid))
        .build();
    return jdbcTemplate.update(update);
  }

  public void batchUpdateState(String tenant, List<String> uuids, ExplosiveSignV2State state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuids, "uuids");

    UpdateStatement update = new UpdateBuilder().table(ExplosiveSignV2.Schema.TABLE_NAME)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .setValue(ExplosiveSignV2.Schema.STATE, state.name())
        .where(Predicates.equals(ExplosiveSignV2.Schema.TENANT, tenant))
        .where(Predicates.in2(ExplosiveSignV2.Schema.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(update);
  }

  public ExplosiveSignV2 get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().from(ExplosiveSignV2.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveSignV2.Schema.TENANT, tenant),
            Predicates.equals(ExplosiveSignV2.Schema.UUID, uuid))
        .build();
    return getFirst(select, MAPPER);
  }

  public QueryResult<ExplosiveSignV2> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(ExplosiveSignV2.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, MAPPER);
  }

  public List<ExplosiveSignV2> list(String tenant, List<String> uuids, boolean forUpdate) {
    Assert.hasText(tenant, "tenant");

    if (CollectionUtils.isEmpty(uuids)) {
      return new ArrayList<>();
    }

    SelectStatement select = new SelectBuilder().from(ExplosiveSignV2.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveSignV2.Schema.TENANT, tenant),
            Predicates.in2(ExplosiveSignV2.Schema.UUID, uuids.toArray()))
        .build();
    if (forUpdate) {
      select.forUpdate();
    }
    return jdbcTemplate.query(select, MAPPER);
  }



}

