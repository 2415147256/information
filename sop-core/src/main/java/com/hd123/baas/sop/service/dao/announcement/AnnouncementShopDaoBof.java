package com.hd123.baas.sop.service.dao.announcement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.hd123.baas.sop.service.api.announcement.Announcement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.announcement.AnnouncementShop;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/20.
 */
@Repository
public class AnnouncementShopDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(AnnouncementShop.class, PAnnouncementShop.class)
      .build();

  public QueryResult<AnnouncementShop> query(String tenant, String owner, QueryDefinition qd) {
    qd.addByField(AnnouncementShop.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(AnnouncementShop.Queries.OWNER, Cop.EQUALS, owner);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new AnnouncementShopMapper());
  }

  public long queryCount(String tenant, String owner, QueryDefinition qd) {
    qd.addByField(AnnouncementShop.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(AnnouncementShop.Queries.OWNER, Cop.EQUALS, owner);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    selectStatement.getSelectClause().getFields().clear();
    selectStatement.select("count(1)");
    QueryResult<Long> result = executor.query(selectStatement, new SingleColumnRowMapper<>(Long.class));
    if (result.getRecordCount() == 0) {
      return 0;
    }
    return result.getRecords().get(0);
  }

  public AnnouncementShop get(String tenant, String owner, String uuid) {
    SelectStatement select = new SelectBuilder().from(PAnnouncementShop.TABLE_NAME)
        .select(PAnnouncementShop.allColumns())
        .where(Predicates.equals(PAnnouncementShop.TENANT, tenant))
        .where(Predicates.equals(PAnnouncementShop.OWNER, owner))
        .where(Predicates.equals(PAnnouncementShop.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new AnnouncementShopMapper()));
  }

  public void insert(String tenant, String owner, AnnouncementShop shop) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(shop, "shop");

    if (StringUtils.isBlank(shop.getUuid())) {
      shop.setUuid(UUID.randomUUID().toString());
    }
    InsertStatement insert = buildInsertStatement(tenant, owner, shop);
    jdbcTemplate.update(insert);
  }

  public void batchDelete(String tenant, String owner, Collection<String> uuids) {
    DeleteStatement delete = new DeleteBuilder().table(PAnnouncementShop.TABLE_NAME)
        .where(Predicates.equals(PAnnouncementShop.TENANT, tenant))
        .where(Predicates.equals(PAnnouncementShop.OWNER, owner))
        .where(Predicates.in2(PAnnouncementShop.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public void deleteByOwner(String tenant, String owner) {
    DeleteStatement delete = new DeleteBuilder().table(PAnnouncementShop.TABLE_NAME)
        .where(Predicates.equals(PAnnouncementShop.TENANT, tenant))
        .where(Predicates.equals(PAnnouncementShop.OWNER, owner))
        .build();
    jdbcTemplate.update(delete);
  }

  public void deleteByOwners(String tenant, Collection<String> owners) {
    if (CollectionUtils.isEmpty(owners)) {
      return;
    }
    DeleteStatement delete = new DeleteBuilder().table(PAnnouncementShop.TABLE_NAME)
        .where(Predicates.equals(PAnnouncementShop.TENANT, tenant))
        .where(Predicates.in2(PAnnouncementShop.OWNER, owners.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<AnnouncementShop> list(String tenant, String owner) {
    SelectStatement select = new SelectBuilder().from(PAnnouncementShop.TABLE_NAME)
        .select(PAnnouncementShop.allColumns())
        .where(Predicates.equals(PAnnouncementShop.TENANT, tenant))
        .where(Predicates.equals(PAnnouncementShop.OWNER, owner))
        .build();
    return jdbcTemplate.query(select, new AnnouncementShopMapper());
  }

  public List<Announcement> listByIds(String tenant, List<String> ids) {
    SelectStatement select = new SelectBuilder().from(PAnnouncement.TABLE_NAME)
        .select(PAnnouncement.allColumns())
        .where(Predicates.equals(PAnnouncement.TENANT, tenant))
        .where(Predicates.in2(PAnnouncement.UUID, ids.toArray()))
        .build();
    return jdbcTemplate.query(select, new AnnouncementMapper());
  }

  public void update(String tenant, String owner, AnnouncementShop shop) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(shop, "shop");
    UpdateStatement update = new UpdateBuilder().table(PAnnouncementShop.TABLE_NAME)
        .addValue(PAnnouncementShop.SHOP, shop.getShop())
        .addValue(PAnnouncementShop.SHOP_CODE, shop.getShopCode())
        .addValue(PAnnouncementShop.SHOP_NAME, shop.getShopName())
        .where(Predicates.equals(PAnnouncementShop.TENANT, tenant))
        .where(Predicates.equals(PAnnouncementShop.OWNER, owner))
        .where(Predicates.equals(PAnnouncementShop.UUID, shop.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void batchInsert(String tenant, String owner, Collection<AnnouncementShop> shops) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    if (CollectionUtils.isEmpty(shops)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (AnnouncementShop shop : shops) {
      if (StringUtils.isBlank(shop.getUuid())) {
        shop.setUuid(UUID.randomUUID().toString());
      }
      statements.add(buildInsertStatement(tenant, owner, shop));
    }
    batchUpdate(statements);
  }

  private InsertStatement buildInsertStatement(String tenant, String owner, AnnouncementShop shop) {
    InsertStatement insert = new InsertBuilder().table(PAnnouncementShop.TABLE_NAME)
        .addValue(PAnnouncementShop.TENANT, tenant)
        .addValue(PAnnouncementShop.OWNER, owner)
        .addValue(PAnnouncementShop.UUID, shop.getUuid())
        .addValue(PAnnouncementShop.SHOP, shop.getShop())
        .addValue(PAnnouncementShop.SHOP_CODE, shop.getShopCode())
        .addValue(PAnnouncementShop.SHOP_NAME, shop.getShopName())
        .build();
    return insert;
  }

}
