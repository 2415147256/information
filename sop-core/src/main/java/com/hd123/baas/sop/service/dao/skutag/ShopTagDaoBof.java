package com.hd123.baas.sop.service.dao.skutag;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;

import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.skutag.ShopTag;
import com.hd123.baas.sop.service.api.skutag.SkuShopTag;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.*;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class ShopTagDaoBof extends BofBaseDao {

  private static final SkuShopTagMapper SKU_SHOP_TAG_MAPPER = new SkuShopTagMapper();

  private static final TEMapper<ShopTag> TAG_MAPPER = TEMapperBuilder.of(ShopTag.class, ShopTag.Schema.class)
      .primaryKey(ShopTag.Schema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopTag.class, ShopTag.Schema.class)
      .addConditionProcessor(new MyConditionProcessor())
      .build();

  public void batchInsert(String tenant, List<ShopTag> tags) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(tags);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopTag tag : tags) {
      InsertStatement insertStatement = buildInsert(tenant, tag);
      updater.add(insertStatement);
    }
    updater.update();
  }

  public int update(String tenant, ShopTag tag) {
    Assert.notNull(tag);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(tag.getUuid(), "uuid");
    UpdateStatement update = buildUpdate(tenant, tag);
    return jdbcTemplate.update(update);
  }

  public void batchUpdate(String tenant, List<ShopTag> tags) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(tags);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopTag tag : tags) {
      Assert.notNull(tenant, "租戶");
      Assert.notNull(tag.getUuid(), "uuid");
      Assert.notNull(tag.getSkuId(), "skuId");
      Assert.notNull(tag.getShop(), "shop");
      UpdateStatement update = buildUpdate(tenant, tag);
      updater.add(update);
    }
    updater.update();
  }

  public ShopTag get(String tenant, String uuid) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().from(ShopTag.Schema.TABLE_NAME)
        .where(Predicates.equals(ShopTag.Schema.TENANT, tenant))
        .where(Predicates.equals(ShopTag.Schema.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, TAG_MAPPER));
  }

  public void delete(String tenant, String uuid) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    DeleteStatement delete = new DeleteBuilder().table(ShopTag.Schema.TABLE_NAME)
        .where(Predicates.equals(ShopTag.Schema.TENANT, tenant))
        .where(Predicates.equals(ShopTag.Schema.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  public void deleteBySkuId(String tenant, String orgId, String skuId, String shop) {
    Assert.hasLength(tenant, "tenant");
    Assert.hasLength(orgId, "orgId");
    Assert.notNull(skuId, "skuId");
    Assert.notNull(shop, "shop");

    DeleteStatement delete = new DeleteBuilder().table(ShopTag.Schema.TABLE_NAME)
        .where(Predicates.equals(ShopTag.Schema.TENANT, tenant))
        .where(Predicates.equals(ShopTag.Schema.ORG_ID, orgId))
        .where(Predicates.equals(ShopTag.Schema.SKU_ID, skuId))
        .where(Predicates.equals(ShopTag.Schema.SHOP, shop))
        .build();
    jdbcTemplate.update(delete);
  }

  public QueryResult<SkuShopTag> query(String tenant, String orgId, String skuId, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(skuId, "skuId");

    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.where(Predicates.equals(ShopTag.Schema.TENANT, tenant));
    select.where(Predicates.equals(ShopTag.Schema.ORG_ID, orgId));
    select.where(Predicates.equals(ShopTag.Schema.SKU_ID, skuId));
    select.groupBy(ShopTag.Schema.SHOP);
    return executor.query(select, SKU_SHOP_TAG_MAPPER);
  }

  public int countShopNum(String tenant, String orgId, String skuId) {
    SelectStatement subSelect = new SelectBuilder().select("count(1)")
        .from(ShopTag.Schema.TABLE_NAME)
        .where(Predicates.equals(ShopTag.Schema.TENANT, tenant))
        .where(Predicates.equals(ShopTag.Schema.ORG_ID, orgId))
        .where(Predicates.equals(ShopTag.Schema.SKU_ID, skuId))
        .groupBy(ShopTag.Schema.SHOP)
        .build();
    SelectStatement select = new SelectBuilder().select("count(1)").from(subSelect, "a").build();
    List<Integer> count = jdbcTemplate.query(select, new SingleColumnRowMapper<>(Integer.class));
    if (CollectionUtils.isEmpty(count)) {
      return 0;
    }
    return count.get(0);
  }

  public List<ShopTag> getTags(String tenant, String orgId, String skuId, String shop) {
    SelectStatement select = new SelectBuilder().from(ShopTag.Schema.TABLE_NAME)
        .where(Predicates.equals(ShopTag.Schema.TENANT, tenant))
        .where(Predicates.equals(ShopTag.Schema.ORG_ID, orgId))
        .where(Predicates.equals(ShopTag.Schema.SKU_ID, skuId))
        .where(Predicates.equals(ShopTag.Schema.SHOP, shop))
        .build();
    return jdbcTemplate.query(select, TAG_MAPPER);
  }

  private InsertStatement buildInsert(String tenant, ShopTag tag) {
    if (StringUtils.isBlank(tag.getUuid())) {
      tag.setUuid(UUID.randomUUID().toString());
    }
    return new InsertBuilder().table(ShopTag.Schema.TABLE_NAME)
        .addValue(ShopTag.Schema.TENANT, tenant)
        .addValue(ShopTag.Schema.ORG_ID, tag.getOrgId())
        .addValue(ShopTag.Schema.SKU_ID, tag.getSkuId())
        .addValue(ShopTag.Schema.SHOP, tag.getShop())
        .addValue(ShopTag.Schema.SHOP_NAME, tag.getShopName())
        .addValue(ShopTag.Schema.SHOP_CODE, tag.getShopCode())
        .addValue(ShopTag.Schema.TAG_ID, tag.getTagId())
        .addValues(PStandardEntity.forSaveNew(tag))
        .build();
  }

  private UpdateStatement buildUpdate(String tenant, ShopTag tag) {
    return new UpdateBuilder().table(ShopTag.Schema.TABLE_NAME)
        .setValue(ShopTag.Schema.TAG_ID, tag.getTagId())
        .setValues(PStandardEntity.forSaveModify(tag))
        .where(Predicates.equals(ShopTag.Schema.TENANT, tenant))
        .build();
  }

  public boolean isExistTag(String tenant, String orgId, String tagId) {
    SelectStatement select = new SelectBuilder().from(ShopTag.Schema.TABLE_NAME)
        .where(Predicates.equals(ShopTag.Schema.TENANT, tenant))
        .where(Predicates.equals(ShopTag.Schema.ORG_ID, orgId))
        .where(Predicates.equals(ShopTag.Schema.TAG_ID, tagId))
        .limit(1)
        .build();
    return getFirst(jdbcTemplate.query(select, TAG_MAPPER)) != null;
  }

  public static class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context)
        throws IllegalArgumentException, QueryProcessException {
      if (condition == null) {
        return null;
      }
      if (com.alibaba.druid.util.StringUtils.equals(condition.getOperation(), ShopTag.Queries.SHOP_KEYWORD_LIKE)) {
        return Predicates.or(like(ShopTag.Schema.SHOP_CODE, condition.getParameter()),
            like(ShopTag.Schema.SHOP_NAME, condition.getParameter()));
      }
      return null;
    }
  }
}
