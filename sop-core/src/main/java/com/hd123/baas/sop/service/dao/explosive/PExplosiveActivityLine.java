package com.hd123.baas.sop.service.dao.explosive;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivityLine;
import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.baas.sop.service.api.promotion.EntityType;
import com.hd123.baas.sop.service.api.promotion.PomEntity;
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
@Table(caption = PExplosiveActivityLine.TABLE_CAPTION, name = PExplosiveActivityLine.TABLE_NAME, indexes = {
        @Index(name = "idx_explosive_activity_1", columnNames = { PExplosiveActivityLine.ACTIVITY_UUID})
})
public class PExplosiveActivityLine extends PEntity {

  public static final String TABLE_NAME = "sop_explosive_activity_line";
  public static final String TABLE_CAPTION = "爆品活动明细";

  @Column(title = "活动ID", name = PExplosiveActivityLine.ACTIVITY_UUID, length = 38)
  public static final String ACTIVITY_UUID = "activityUuid";

  @Column(title = "skuId", name = PExplosiveActivityLine.SKU_ID, length = 38)
  public static final String SKU_ID = "skuId";
  @Column(title = "商品uuid", name = PExplosiveActivityLine.ENTITY_UNIT_UUID, length = 38)
  public static final String ENTITY_UNIT_UUID = "entityUuid";
  @Column(title = "商品Code", name = PExplosiveActivityLine.ENTITY_UNIT_CODE, length = 64)
  public static final String ENTITY_UNIT_CODE = "entityCode";
  @Column(title = "商品名称", name = PExplosiveActivityLine.ENTITY_UNIT_NAME, length = 128)
  public static final String ENTITY_UNIT_NAME = "entityName";

  @Column(title = "商品分类id", name = PExplosiveActivityLine.CATEGORY_UNIT_ID, length = 38)
  public static final String CATEGORY_UNIT_ID = "categoryId";
  @Column(title = "商品分类Code", name = PExplosiveActivityLine.CATEGORY_UNIT_CODE, length = 64)
  public static final String CATEGORY_UNIT_CODE = "categoryCode";
  @Column(title = "商品分类名称", name = PExplosiveActivityLine.CATEGORY_UNIT_NAME, length = 128)
  public static final String CATEGORY_UNIT_NAME = "categoryName";

  @Column(title = "类型", name = PExplosiveActivityLine.ENTITY_TYPE, length = 64)
  public static final String ENTITY_TYPE = "entityType";
  @Column(title = "qpc", name = PExplosiveActivityLine.QPC, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String QPC = "qpc";

  @Column(title = "规格", name = PExplosiveActivityLine.SPECIFICATION, length = 64)
  public static final String SPECIFICATION = "specification";

  @Column(title = "生产厂家", name = PExplosiveActivityLine.MANUFACTORY, length = 64)
  public static final String MANUFACTORY = "manufactory";

  @Column(title = "计量单位", name = PExplosiveActivityLine.MEASUREUNIT, length = 32)
  public static final String MEASUREUNIT = "measureUnit";

  @Column(title = "原价", name = PExplosiveActivityLine.PRICE, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String PRICE = "price";

  @Column(title = "配货规格", name = PExplosiveActivityDetail.ALC_QPC, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String ALC_QPC = "alcQpc";
  @Column(title = "配货单位", name = PExplosiveActivityDetail.ALC_UNIT, length = 32)
  public static final String ALC_UNIT = "alcUnit";

  @Column(title = "促销价", name = PExplosiveActivityLine.PRM_PRICE, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String PRM_PRICE = "prmPrice";

  @Column(title = "商品到店价", name = PExplosiveActivityLine.BASE_PRICE, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String BASE_PRICE = "basePrice";

  @Column(title = "每店最大订货量", name = PExplosiveActivityLine.MAX_SIGN_QTY, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String MAX_SIGN_QTY = "maxSignQty";

  @Column(title = "每店最小订货量", name = PExplosiveActivityLine.MIN_SIGN_QTY, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String MIN_SIGN_QTY = "minSignQty";

  @Column(title = "建议量", name = PExplosiveActivityLine.SUGGEST_QTY, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String SUGGEST_QTY = "suggestQty";

  public static final String[] COLUMNS = {
          PEntity.UUID, ACTIVITY_UUID, ENTITY_TYPE, QPC, SPECIFICATION, ALC_QPC, ALC_UNIT,
          MANUFACTORY, MEASUREUNIT, PRICE, PRM_PRICE, BASE_PRICE, MAX_SIGN_QTY, ENTITY_UNIT_UUID, ENTITY_UNIT_CODE, ENTITY_UNIT_NAME,
          CATEGORY_UNIT_ID, CATEGORY_UNIT_CODE, CATEGORY_UNIT_NAME, MIN_SIGN_QTY, SKU_ID,SUGGEST_QTY};


  public static Map<String, Object> toFieldValues(String activityUuid, ExplosiveActivityLine entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString());
    fvm.put(ACTIVITY_UUID, activityUuid);
    if (entity.getEntity() != null) {
      if (entity.getEntity().getCategory() != null) {
        fvm.put(CATEGORY_UNIT_ID, entity.getEntity().getCategory().getId());
        fvm.put(CATEGORY_UNIT_CODE, entity.getEntity().getCategory().getCode());
        fvm.put(CATEGORY_UNIT_NAME, entity.getEntity().getCategory().getName());
      }
      fvm.put(SKU_ID, entity.getEntity().getSkuId());
      fvm.put(ENTITY_UNIT_UUID, entity.getEntity().getUuid());
      fvm.put(ENTITY_UNIT_CODE, entity.getEntity().getCode());
      fvm.put(ENTITY_UNIT_NAME, entity.getEntity().getName());
      if (entity.getEntity().getEntityType() != null) {
        fvm.put(ENTITY_TYPE, entity.getEntity().getEntityType().name());
      }
      fvm.put(PRICE, entity.getEntity().getPrice());
      fvm.put(QPC, entity.getEntity().getQpc());
      fvm.put(ALC_QPC, entity.getEntity().getAlcQpc());
      fvm.put(ALC_UNIT, entity.getEntity().getAlcUnit());
      fvm.put(SPECIFICATION, entity.getEntity().getSpecification());
      fvm.put(MANUFACTORY, entity.getEntity().getManufactory());
      fvm.put(MEASUREUNIT, entity.getEntity().getMeasureUnit());
    }
    fvm.put(PRM_PRICE, entity.getPrmPrice());
    fvm.put(BASE_PRICE, entity.getBasePrice());
    fvm.put(MAX_SIGN_QTY, entity.getMaxSignQty());
    fvm.put(MIN_SIGN_QTY, entity.getMinSignQty());
    fvm.put(SUGGEST_QTY, entity.getSuggestQty());
    return fvm;
  }

  public static ExplosiveActivityLine mapRow(ResultSet rs, int i) throws SQLException {
    ExplosiveActivityLine target = new ExplosiveActivityLine();
    target.setMaxSignQty(rs.getBigDecimal(MAX_SIGN_QTY));
    target.setMinSignQty(rs.getBigDecimal(MIN_SIGN_QTY));
    target.setPrmPrice(rs.getBigDecimal(PRM_PRICE));
    target.setBasePrice(rs.getBigDecimal(BASE_PRICE));
    target.setSuggestQty(rs.getBigDecimal(SUGGEST_QTY));

    Category category = new Category();
    category.setId(rs.getString(CATEGORY_UNIT_ID));
    category.setCode(rs.getString(CATEGORY_UNIT_CODE));
    category.setName(rs.getString(CATEGORY_UNIT_NAME));

    PomEntity pomEntity = new PomEntity();
    pomEntity.setSkuId(rs.getString(SKU_ID));
    pomEntity.setCategory(category);
    pomEntity.setQpc(rs.getBigDecimal(QPC));
    pomEntity.setAlcQpc(rs.getBigDecimal(ALC_QPC));
    pomEntity.setAlcUnit(rs.getString(ALC_UNIT));
    pomEntity.setPrice(rs.getBigDecimal(PRICE));
    pomEntity.setUuid(rs.getString(ENTITY_UNIT_UUID));
    pomEntity.setCode(rs.getString(ENTITY_UNIT_CODE));
    pomEntity.setName(rs.getString(ENTITY_UNIT_NAME));
    pomEntity.setMeasureUnit(rs.getString(MEASUREUNIT));
    pomEntity.setManufactory(rs.getString(MANUFACTORY));
    pomEntity.setSpecification(rs.getString(SPECIFICATION));
    pomEntity.setEntityType(StringUtil.toEnum(rs.getString(ENTITY_TYPE), EntityType.class));
    target.setEntity(pomEntity);
    return target;
  }
}
