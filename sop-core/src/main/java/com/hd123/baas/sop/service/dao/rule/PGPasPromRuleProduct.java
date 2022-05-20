/**
 * 版权所有(C),上海海鼎信息工程股份有限公司,2021,所有权利保留。
 * <p>
 * 项目名: sop-pms-parent
 * 文件名: PGPasPromRuleProduct.java
 * 模块说明:
 * 修改历史:
 * 2021年04月06日 - wushuaijun- 创建
 */
package com.hd123.baas.sop.service.dao.rule;

import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.baas.sop.service.api.promotion.EntityType;
import com.hd123.baas.sop.service.api.promotion.PromotionType;
import com.hd123.baas.sop.service.api.promotion.condition.ProductCondition;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;
import com.hd123.rumba.commons.lang.StringUtil;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wushuaijun
 * @since 1.0
 */

@Entity
@Table(caption = PGPasPromRuleProduct.TABLE_CAPTION, name = PGPasPromRuleProduct.TABLE_NAME, indexes = {
        @Index(name = "idx_g_pas_prom_rule_product_1", columnNames = PGPasPromRuleProduct.RULE_UUID),
        @Index(name = "idx_g_pas_prom_rule_product_2", columnNames = PGPasPromRuleProduct.ENTITY_UNIT_UUID),
        @Index(name = "idx_g_pas_prom_rule_product_3", columnNames = PGPasPromRuleProduct.ENTITY_UNIT_CODE),
        @Index(name = "idx_g_pas_prom_rule_product_4", columnNames = PGPasPromRuleProduct.ENTITY_UNIT_NAME),

})
public class PGPasPromRuleProduct extends PEntity {

  public static final String TABLE_NAME = "sop_gpas_prom_rule_product";
  public static final String TABLE_CAPTION = "批发订货促销规则单-促销商品";

  @Column(title = "批发订货促销规则单uuid", name = PGPasPromRuleProduct.RULE_UUID, length = 38)
  public static final String RULE_UUID = "ruleUuid";
  @Column(title = "商品uuid", name = PGPasPromRuleProduct.ENTITY_UNIT_UUID, length = 38)
  public static final String ENTITY_UNIT_UUID = "entityUuid";
  @Column(title = "商品Code", name = PGPasPromRuleProduct.ENTITY_UNIT_CODE, length = 64)
  public static final String ENTITY_UNIT_CODE = "entityCode";
  @Column(title = "商品名称", name = PGPasPromRuleProduct.ENTITY_UNIT_NAME, length = 128)
  public static final String ENTITY_UNIT_NAME = "entityName";

  @Column(title = "类型", name = PGPasPromRuleProduct.ENTITY_TYPE, length = 64)
  public static final String ENTITY_TYPE = "entityType";
  @Column(title = "qpc", name = PGPasPromRuleProduct.QPC, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String QPC = "qpc";
  @Column(title = "生产厂家", name = PGPasPromRuleProduct.MANUFACTORY, length = 64)
  public static final String MANUFACTORY = "manufactory";
  @Column(title = "计量单位", name = PGPasPromRuleProduct.MEASUREUNIT, length = 32)
  public static final String MEASUREUNIT = "measureUnit";
  @Column(title = "原价", name = PGPasPromRuleProduct.PRICE, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String PRICE = "price";

  public static final String[] COLUMNS = { PEntity.UUID, RULE_UUID, ENTITY_UNIT_UUID,
          ENTITY_UNIT_CODE, ENTITY_UNIT_NAME, ENTITY_TYPE, QPC, MANUFACTORY, MEASUREUNIT, PRICE};

  public static Map<String, Object> toFieldValues(String uuid, PromotionType promotionType, EntityType entityType, ProductCondition.Item item) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString());
    fvm.put(RULE_UUID, uuid);
    fvm.put(ENTITY_UNIT_UUID, item.getUuid());
    fvm.put(ENTITY_UNIT_CODE, item.getCode());
    fvm.put(ENTITY_UNIT_NAME, item.getName());
    fvm.put(ENTITY_TYPE, entityType.toString());
    fvm.put(QPC, item.getQpc());
    fvm.put(MANUFACTORY, item.getManufactory());
    fvm.put(MEASUREUNIT, item.getMeasureUnit());
    fvm.put(PRICE, item.getPrice());
    return fvm;
  }

  public static ProductCondition.Item mapRow(ResultSet rs, int rowNum) throws SQLException {
    ProductCondition.Item target = new ProductCondition.Item();
    target.setCode(rs.getString(ENTITY_UNIT_CODE));
    target.setName(rs.getString(ENTITY_UNIT_NAME));
    target.setUuid(rs.getString(ENTITY_UNIT_UUID));
    target.setEntityType(StringUtil.toEnum(rs.getString(ENTITY_TYPE), EntityType.class));
    target.setPrice(rs.getBigDecimal(PRICE));
    target.setMeasureUnit(rs.getString(MEASUREUNIT));
    target.setManufactory(rs.getString(MANUFACTORY));
    target.setQpc(rs.getBigDecimal(QPC));
    return target;
  }

}
