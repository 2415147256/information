package com.hd123.baas.sop.service.dao.explosivev2.report;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2Type;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
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

import java.util.List;

/**
 * 爆品活动日志(ExplosiveLogV2)表数据库访问层
 *
 * @author liuhaoxin
 * @since 2021-12-07 18:04:34
 */
@Repository
public class ExplosiveLogV2DaoBof extends BofBaseDao {

  private static final TEMapper<ExplosiveLogV2> MAPPER = TEMapperBuilder
      .of(ExplosiveLogV2.class, ExplosiveLogV2.Schema.class)
      .map("shop.uuid", ExplosiveLogV2.Schema.SHOP_ID)
      .map("shop.code", ExplosiveLogV2.Schema.SHOP_CODE)
      .map("shop.name", ExplosiveLogV2.Schema.SHOP_NAME)
      .map("sourceType", ExplosiveLogV2.Schema.SOURCE_TYPE, EnumConverters.toString(ExplosiveLogV2Type.class), EnumConverters.toEnum(ExplosiveLogV2Type.class))
      .primaryKey(ExplosiveLogV2.Schema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ExplosiveLogV2.class,
      ExplosiveLogV2.Schema.class).build();

  public void insert(String tenant, ExplosiveLogV2 explosiveLogV2, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(explosiveLogV2, "explosiveLogV2");

    explosiveLogV2.setTenant(tenant);
    explosiveLogV2.setCreated(operateInfo.getTime());
    InsertStatement insert = new InsertBuilder().table(ExplosiveLogV2.Schema.TABLE_NAME)
        .values(MAPPER.forInsert(explosiveLogV2, true))
        .build();
    jdbcTemplate.update(insert);
  }

  public void batchInsert(String tenant, List<ExplosiveLogV2> logV2s, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(logV2s, "logV2s");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveLogV2 logV2 : logV2s) {
      logV2.setTenant(tenant);
      logV2.setCreated(operateInfo.getTime());
      InsertStatement insert = new InsertBuilder().table(ExplosiveLogV2.Schema.TABLE_NAME)
          .addValues(MAPPER.forInsert(logV2))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public void update(String tenant, ExplosiveLogV2 explosiveLogV2, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(explosiveLogV2, "explosiveLogV2");
    explosiveLogV2.setCreated(operateInfo.getTime());
    explosiveLogV2.setTenant(tenant);
    UpdateStatement update = new UpdateBuilder()
        .table(ExplosiveLogV2.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveLogV2.Schema.UUID, explosiveLogV2.getUuid()))
        .setValues(MAPPER.forInsert(explosiveLogV2, true))
        .build();
    jdbcTemplate.update(update);
  }

  public ExplosiveLogV2 get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().from(ExplosiveLogV2.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveLogV2.Schema.TENANT, tenant),
            Predicates.equals(ExplosiveLogV2.Schema.UUID, uuid))
        .build();
    return getFirst(select, MAPPER);
  }

  public ExplosiveLogV2 getByExplosiveId(String tenant, String explosiveId, String shopId, ExplosiveLogV2Type type) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(explosiveId, "explosiveId");
    Assert.hasText(shopId, "shopId");
    Assert.notNull(type, "type");
    SelectStatement select = new SelectBuilder()
        .from(ExplosiveLogV2.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveLogV2.Schema.EXPLOSIVE_ID, explosiveId))
        .where(Predicates.equals(ExplosiveLogV2.Schema.SHOP_ID, shopId))
        .where(Predicates.equals(ExplosiveLogV2.Schema.SOURCE_TYPE, type.toString()))
        .build();
    List<ExplosiveLogV2> query = jdbcTemplate.query(select, MAPPER);
    return CollectionUtils.isEmpty(query) ? null : query.get(0);
  }
}

