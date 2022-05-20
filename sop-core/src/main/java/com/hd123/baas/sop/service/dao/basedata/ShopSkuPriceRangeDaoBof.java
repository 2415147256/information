package com.hd123.baas.sop.service.dao.basedata;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.skutag.ShopSkuPriceRange;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessContext;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessException;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorProvider;
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
import com.hd123.rumba.commons.lang.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shenmin
 */
@Repository
public class ShopSkuPriceRangeDaoBof extends MasBofBaseDao implements QueryProcessorProvider {

  @Autowired
  private JdbcPagingQueryExecutor<ShopSkuPriceRange> executor;

  public static final byte NOT_DELETE_TAG = 0;

  public static final TEMapper<ShopSkuPriceRange> MAPPER = TEMapperBuilder.of(ShopSkuPriceRange.class, PShopSkuPriceRange.class)
      .map("tagIds", PShopSkuPriceRange.TAG_IDS)
      .primaryKey(PShopSkuPriceRange.UUID)
      .build();

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopSkuPriceRange.class, PShopSkuPriceRange.class)
      .addConditionProcessor(new MyConditionProcessor())
      .build();

  @Tx
  public String insert(String tenant, ShopSkuPriceRange priceRange) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceRange, "priceRange");
    priceRange.setTenant(tenant);
    if (StringUtil.isNullOrBlank(priceRange.getUuid())) {
      priceRange.setUuid(IdGenUtils.buildRdUuid());
    }
    InsertStatement insert = new InsertBuilder()
        .table(PShopSkuPriceRange.TABLE_NAME)
        .addValues(MAPPER.forInsert(priceRange))
        .build();
    jdbcTemplate.update(insert);
    return priceRange.getUuid();
  }

  public ShopSkuPriceRange get(String tenant, String uuid, boolean forUpdate) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectBuilder builder = new SelectBuilder()
        .from(PShopSkuPriceRange.TABLE_NAME)
        .where(Predicates.equals(PShopSkuPriceRange.TENANT, tenant))
        .where(Predicates.equals(PShopSkuPriceRange.UUID, uuid));
    SelectStatement select = forUpdate ? builder.forUpdate().build() : builder.build();
    return getFirst(select, MAPPER);
  }

  public void update(String tenant, ShopSkuPriceRange priceRange) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceRange, "priceRange");
    priceRange.setTenant(tenant);
    UpdateStatement update = new UpdateBuilder()
        .table(PShopSkuPriceRange.TABLE_NAME)
        .where(Predicates.equals(PShopSkuPriceRange.TENANT, tenant))
        .where(Predicates.equals(PShopSkuPriceRange.UUID, priceRange.getUuid()))
        .addValues(MAPPER.forUpdate(priceRange, true))
        .build();
    jdbcTemplate.update(update);
  }

  @Override
  public QueryProcessor getQueryProcessor() {
    return QUERY_PROCESSOR;
  }

  public void delete(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    DeleteStatement delete = new DeleteBuilder()
        .table(PShopSkuPriceRange.TABLE_NAME)
        .where(Predicates.equals(PShopSkuPriceRange.TENANT, tenant))
        .where(Predicates.equals(PShopSkuPriceRange.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  public QueryResult<ShopSkuPriceRange> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(PShopSkuPriceRange.TENANT, Cop.EQUALS, tenant);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    return executor.query(selectStatement, MAPPER);
  }

  public List<ShopSkuPriceRange> list(String tenant, String shopId, List<String> skuIds) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopId, "shopId");

    if (CollectionUtils.isEmpty(skuIds)) {
      return new ArrayList<>();
    }

    SelectBuilder builder = new SelectBuilder()
        .from(PShopSkuPriceRange.TABLE_NAME)
        .where(Predicates.equals(PShopSkuPriceRange.TENANT, tenant))
        .where(Predicates.equals(PShopSkuPriceRange.SHOP_ID, shopId))
        .where(Predicates.in2(PShopSkuPriceRange.SKU_ID, skuIds.toArray()))
        .where(Predicates.equals(PShopSkuPriceRange.DELETED, NOT_DELETE_TAG));
    return jdbcTemplate.query(builder.build(), MAPPER);
  }

  public void batchInsert(String tenant, List<ShopSkuPriceRange> priceRanges) {
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    priceRanges.forEach(i -> {
      i.setTenant(tenant);
      if (StringUtil.isNullOrBlank(i.getUuid())) {
        i.setUuid(IdGenUtils.buildRdUuid());
      }
      updater.add(buildInsert(i));
    });
    updater.update();
  }

  private InsertStatement buildInsert(ShopSkuPriceRange priceRange) {
    return new InsertBuilder()
        .table(PShopSkuPriceRange.TABLE_NAME)
        .addValues(MAPPER.forInsert(priceRange))
        .build();
  }

  public class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context) throws IllegalArgumentException, QueryProcessException {
      if (context == null) {
        return null;
      }
      String alias = context.getPerzAlias();
      if (ShopSkuPriceRange.Queries.KEYWORD_LIKE.equals(condition.getOperation())) {
        return Predicates.or(Predicates.like(alias, PShopSkuPriceRange.SKU_CODE, condition.getParameter()),
            Predicates.like(alias, PShopSkuPriceRange.SKU_NAME, condition.getParameter()));
      }
      return null;
    }
  }
}