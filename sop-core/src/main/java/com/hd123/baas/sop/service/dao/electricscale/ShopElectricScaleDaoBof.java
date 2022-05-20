package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.electricscale.ShopElecScale;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessContext;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessException;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.Predicate;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;

@Service
public class ShopElectricScaleDaoBof extends BofBaseDao {

  private static final ShopElectricScaleMapper ELECTRIC_SCALE_MAPPER = new ShopElectricScaleMapper();

  private static QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopElecScale.class, PShopElecScale.class)
      .addConditionProcessor(new QueryConditionProcessor() {
        @Override
        public Predicate process(QueryCondition condition, QueryProcessContext queryProcessContext)
            throws IllegalArgumentException, QueryProcessException {
          if (condition == null) {
            return null;
          }
          String operation = condition.getOperation();
          String shopKeyWord = ShopElecScale.Queries.SHOP_KEY_WORD_LIKE;
          if (StringUtils.equals(operation, shopKeyWord)) {
            return Predicates.or(like(PShopElecScale.SHOP_CODE, condition.getParameter()),
                like(PShopElecScale.SHOP_NAME, condition.getParameter()));
          }
          return null;
        }
      })
      .build();

  public String insert(String tenant, ShopElecScale scale, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(scale, "电子秤");
    if (scale.getUuid() == null) {
      scale.setUuid(UUID.randomUUID().toString());
    }
    InsertBuilder insert = new InsertBuilder().table(PShopElecScale.TABLE_NAME)
        .addValue(PShopElecScale.UUID, scale.getUuid())
        .addValue(PShopElecScale.TENANT, tenant)
        .addValue(PShopElecScale.NAME, scale.getName())
        .addValue(PShopElecScale.MODEL, scale.getModel())
        .addValue(PShopElecScale.IP, scale.getIp())
        .addValue(PShopElecScale.ORG_ID, scale.getOrgId())
        .addValue(PShopElecScale.ELECTRONIC_SCALE, scale.getElectronicScale())
        .addValue(PShopElecScale.SHOP_CODE, scale.getShopCode())
        .addValue(PShopElecScale.SHOP_NAME, scale.getShopName());
    if (operateInfo != null) {
      insert.addValues(PStandardEntity.toCreateInfoFieldValues(operateInfo));
      insert.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    int count = jdbcTemplate.update(insert.build());
    if (count != 1) {
      throw new BaasException("插入电子秤失败!");
    }
    return scale.getUuid();
  }

  public void update(String tenant, ShopElecScale scale, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(scale, "电子秤");
    Assert.notNull(scale.getUuid(), "uuid");
    UpdateBuilder update = new UpdateBuilder().table(PShopElecScale.TABLE_NAME)
        .setValue(PShopElecScale.IP, scale.getIp())
        .where(Predicates.equals(PShopElecScale.TENANT, tenant))
        .where(Predicates.equals(PShopElecScale.UUID, scale.getUuid()));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    jdbcTemplate.update(update.build());
  }

  public void delete(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    DeleteBuilder delete = new DeleteBuilder().table(PShopElecScale.TABLE_NAME)
        .where(Predicates.equals(PShopElecScale.TENANT, tenant))
        .where(Predicates.equals(PShopElecScale.UUID, uuid));
    jdbcTemplate.update(delete.build());
  }

  public ShopElecScale getByIp(String tenant, String shopCode, String ip) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(ip, "ip");
    SelectBuilder select = new SelectBuilder().from(PShopElecScale.TABLE_NAME)
        .where(Predicates.equals(PShopElecScale.TENANT, tenant))
        .where(Predicates.equals(PShopElecScale.IP, ip))
        .where(Predicates.equals(PShopElecScale.SHOP_CODE, shopCode));
    List<ShopElecScale> result = jdbcTemplate.query(select.build(), ELECTRIC_SCALE_MAPPER);
    if (CollectionUtils.isEmpty(result)) {
      return null;
    }
    return result.get(0);
  }

  public QueryResult<ShopElecScale> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "qd");
    SelectStatement process = QUERY_PROCESSOR.process(qd);
    QueryResult result = executor.query(process, ELECTRIC_SCALE_MAPPER);
    return result;
  }

  public ShopElecScale get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    SelectBuilder select = new SelectBuilder().from(PShopElecScale.TABLE_NAME)
        .where(Predicates.equals(PShopElecScale.TENANT, tenant))
        .where(Predicates.equals(PShopElecScale.UUID, uuid));
    List<ShopElecScale> result = jdbcTemplate.query(select.build(), ELECTRIC_SCALE_MAPPER);
    if (CollectionUtils.isEmpty(result)) {
      return null;
    }
    return result.get(0);
  }

  public ShopElecScale getByShopCodeAndUUid(String tenant, String shopCode, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopCode, "门店code");
    Assert.notNull(uuid, "uuid");
    SelectBuilder select = new SelectBuilder().from(PShopElecScale.TABLE_NAME)
        .where(Predicates.equals(PShopElecScale.TENANT, tenant))
        .where(Predicates.equals(PShopElecScale.SHOP_CODE, shopCode))
        .where(Predicates.equals(PShopElecScale.UUID, uuid));
    List<ShopElecScale> result = jdbcTemplate.query(select.build(), ELECTRIC_SCALE_MAPPER);
    if (CollectionUtils.isEmpty(result)) {
      return null;
    }
    return result.get(0);
  }

  public List<ShopElecScale> getByShopCode(String tenant, String shopCode) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopCode, "门店code");
    SelectBuilder select = new SelectBuilder().from(PShopElecScale.TABLE_NAME)
        .where(Predicates.equals(PShopElecScale.TENANT, tenant))
        .where(Predicates.equals(PShopElecScale.SHOP_CODE, shopCode));
    return jdbcTemplate.query(select.build(), ELECTRIC_SCALE_MAPPER);
  }
}
