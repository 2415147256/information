package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.electricscale.ShopElecScaleTemplate;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ShopElecScaleTemplateDaoBof extends BofBaseDao {
  public static final ShopElecScaleTemplateMapper SHOP_ELEC_SCALE_TEMPLATE_MAPPER = new ShopElecScaleTemplateMapper();

  public List<ShopElecScaleTemplate> getByElecScaleTemplate(String tenant, String elecScaleTemplate) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(elecScaleTemplate, "elecScaleTemplate");
    SelectBuilder select = new SelectBuilder().from(PShopElecScaleTemplate.TABLE_NAME)
        .where(Predicates.equals(PShopElecScaleTemplate.TENANT, tenant))
        .where(Predicates.equals(PShopElecScaleTemplate.ELECTRONIC_SCALE_TEMPLATE, elecScaleTemplate));
    return jdbcTemplate.query(select.build(), SHOP_ELEC_SCALE_TEMPLATE_MAPPER);
  }

  public void deleteByTemplate(String tenant, String keyBoardTemUuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(keyBoardTemUuid, "模板id");
    DeleteBuilder delete = new DeleteBuilder().table(PShopElecScaleTemplate.TABLE_NAME)
        .where(Predicates.equals(PShopElecScaleTemplate.TENANT, tenant))
        .where(Predicates.equals(PShopElecScaleTemplate.ELECTRONIC_SCALE_TEMPLATE, keyBoardTemUuid));
    jdbcTemplate.update(delete.build());
  }

  public void bindShops(String tenant, String keyBoardTemUuid, List<UCN> shops) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(keyBoardTemUuid, "模板id");
    List<InsertStatement> list = new ArrayList<>();
    shops.stream().forEach(i -> {
      InsertBuilder insertBuilder = insertBuilder(tenant, keyBoardTemUuid, i);
      list.add(insertBuilder.build());
    });
    batchUpdate(list);
  }

  private InsertBuilder insertBuilder(String tenant, String templateUuid, UCN shop) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(templateUuid, "templateUuid");
    InsertBuilder insert = new InsertBuilder().table(PShopElecScaleTemplate.TABLE_NAME)
        .addValue(PShopElecScaleTemplate.TENANT, tenant)
        .addValue(PShopElecScaleTemplate.UUID, UUID.randomUUID().toString())
        .addValue(PShopElecScaleTemplate.ELECTRONIC_SCALE_TEMPLATE, templateUuid)
        .addValue(PShopElecScaleTemplate.SHOP, shop.getUuid())
        .addValue(PShopElecScaleTemplate.SHOPCODE, shop.getCode())
        .addValue(PShopElecScaleTemplate.SHOPNAME, shop.getName());
    return insert;
  }

  public void bindAllShop(String tenant, String keyBoardTemUuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(keyBoardTemUuid, "模板id");
    InsertBuilder insert = new InsertBuilder().table(PShopElecScaleTemplate.TABLE_NAME)
        .addValue(PShopElecScaleTemplate.TENANT, tenant)
        .addValue(PShopElecScaleTemplate.UUID, UUID.randomUUID().toString())
        .addValue(PShopElecScaleTemplate.ELECTRONIC_SCALE_TEMPLATE, keyBoardTemUuid)
        .addValue(PShopElecScaleTemplate.ISALLSHOP, true);
    jdbcTemplate.update(insert.build());

  }

  public List<ShopElecScaleTemplate> listByTemplates(String tenant, List<String> templates) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(templates, "elecScaleTemplate");
    SelectBuilder select = new SelectBuilder().from(PShopElecScaleTemplate.TABLE_NAME)
        .where(Predicates.equals(PShopElecScaleTemplate.TENANT, tenant))
        .where(Predicates.in(PShopElecScaleTemplate.TABLE_NAME, PShopElecScaleTemplate.ELECTRONIC_SCALE_TEMPLATE,
            templates.toArray()));
    return jdbcTemplate.query(select.build(), SHOP_ELEC_SCALE_TEMPLATE_MAPPER);
  }

  public List<ShopElecScaleTemplate> listByShopCode(String tenant, String shopCode) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopCode, "shopCode");
    SelectBuilder select = new SelectBuilder().from(PShopElecScaleTemplate.TABLE_NAME)
        .where(Predicates.equals(PShopElecScaleTemplate.TENANT, tenant))
        .where(Predicates.or(Predicates.equals(PShopElecScaleTemplate.SHOPCODE, shopCode),
            Predicates.equals(PShopElecScaleTemplate.ISALLSHOP, 1)));
    return jdbcTemplate.query(select.build(), SHOP_ELEC_SCALE_TEMPLATE_MAPPER);
  }
}
