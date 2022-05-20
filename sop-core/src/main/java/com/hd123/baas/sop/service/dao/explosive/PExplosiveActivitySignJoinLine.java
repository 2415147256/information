package com.hd123.baas.sop.service.dao.explosive;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivitySignJoin;
import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.baas.sop.service.api.promotion.EntityType;
import com.hd123.baas.sop.service.api.promotion.PomEntity;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.lang.StringUtil;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Entity
@Table(caption = PExplosiveActivitySignJoinLine.TABLE_CAPTION, name = PExplosiveActivitySignJoinLine.TABLE_NAME, indexes = {
        @Index(name = "idx_explosive_activity_sign_join_line_1", columnNames = { PExplosiveActivitySignJoinLine.ACTIVITY_UUID})
})
public class PExplosiveActivitySignJoinLine extends PEntity {

  public static final String TABLE_NAME = "sop_explosive_activity_sign_join_line";
  public static final String TABLE_CAPTION = "爆品活动报名明细";

  @Column(title = "活动ID", name = PExplosiveActivitySignJoinLine.ACTIVITY_UUID, length = 38)
  public static final String ACTIVITY_UUID = "activityUuid";

  @Column(title = "提交人id", name = PExplosiveActivitySignJoinLine.SUBMIT_ID, length = 40)
  public static final String SUBMIT_ID = "submitId";
  @Column(title = "提交时间", name = PExplosiveActivitySignJoinLine.SUBMIT_INFO_TIME, fieldClass = Date.class)
  public static final String SUBMIT_INFO_TIME = "submitTime";
  @Column(title = "提交人NS", name = PExplosiveActivitySignJoinLine.SUBMIT_NAMESPACE, length = 64)
  public static final String SUBMIT_NAMESPACE = "submitNS";
  @Column(title = "提交人", name = PExplosiveActivitySignJoinLine.SUBMIT_NAME, length = 64)
  public static final String SUBMIT_NAME = "submitName";

  @Column(title = "门店uuid", name = PExplosiveActivitySignJoinLine.JOIN_UNIT_UUID, length = 38)
  public static final String JOIN_UNIT_UUID = "joinUnitUuid";
  @Column(title = "门店Code", name = PExplosiveActivitySignJoinLine.JOIN_UNIT_CODE, length = 64)
  public static final String JOIN_UNIT_CODE = "joinUnitCode";
  @Column(title = "门店名称", name = PExplosiveActivitySignJoinLine.JOIN_UNIT_NAME, length = 128)
  public static final String JOIN_UNIT_NAME = "joinUnitName";


  @Column(title = "skuId", name = PExplosiveActivitySignJoinLine.SKU_ID, length = 38)
  public static final String SKU_ID = "skuId";
  @Column(title = "商品uuid", name = PExplosiveActivitySignJoinLine.ENTITY_UNIT_UUID, length = 38)
  public static final String ENTITY_UNIT_UUID = "entityUuid";
  @Column(title = "商品Code", name = PExplosiveActivitySignJoinLine.ENTITY_UNIT_CODE, length = 64)
  public static final String ENTITY_UNIT_CODE = "entityCode";
  @Column(title = "商品名称", name = PExplosiveActivitySignJoinLine.ENTITY_UNIT_NAME, length = 128)
  public static final String ENTITY_UNIT_NAME = "entityName";

  @Column(title = "商品分类id", name = PExplosiveActivitySignJoinLine.CATEGORY_UNIT_ID, length = 38)
  public static final String CATEGORY_UNIT_ID = "categoryId";
  @Column(title = "商品分类Code", name = PExplosiveActivitySignJoinLine.CATEGORY_UNIT_CODE, length = 64)
  public static final String CATEGORY_UNIT_CODE = "categoryCode";
  @Column(title = "商品分类名称", name = PExplosiveActivitySignJoinLine.CATEGORY_UNIT_NAME, length = 128)
  public static final String CATEGORY_UNIT_NAME = "categoryName";

  @Column(title = "配货规格", name = PExplosiveActivityDetail.ALC_QPC, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String ALC_QPC = "alcQpc";
  @Column(title = "配货单位", name = PExplosiveActivityDetail.ALC_UNIT, length = 32)
  public static final String ALC_UNIT = "alcUnit";

  @Column(title = "预定日期", name = PExplosiveActivitySignJoinLine.SIGN_DATE, fieldClass = Date.class)
  public static final String SIGN_DATE = "signDate";

  @Column(title = "类型", name = PExplosiveActivitySignJoinLine.ENTITY_TYPE, length = 64)
  public static final String ENTITY_TYPE = "entityType";

  @Column(title = "qpc", name = PExplosiveActivitySignJoinLine.QPC, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String QPC = "qpc";

  @Column(title = "规格", name = PExplosiveActivitySignJoinLine.SPECIFICATION, length = 64)
  public static final String SPECIFICATION = "specification";

  @Column(title = "生产厂家", name = PExplosiveActivitySignJoinLine.MANUFACTORY, length = 64)
  public static final String MANUFACTORY = "manufactory";

  @Column(title = "计量单位", name = PExplosiveActivitySignJoinLine.MEASUREUNIT, length = 32)
  public static final String MEASUREUNIT = "measureUnit";

  @Column(title = "原价", name = PExplosiveActivitySignJoinLine.PRICE, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String PRICE = "price";

  @Column(title = "订货量", name = PExplosiveActivitySignJoinLine.SIGN_QTY, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String SIGN_QTY = "signQty";

  public static final String[] COLUMNS = {
          PEntity.UUID, ACTIVITY_UUID, ENTITY_TYPE, QPC, SPECIFICATION, MANUFACTORY, MEASUREUNIT, PRICE, SIGN_QTY, ALC_QPC, ALC_UNIT,
          ENTITY_UNIT_UUID, ENTITY_UNIT_CODE, ENTITY_UNIT_NAME, CATEGORY_UNIT_ID, CATEGORY_UNIT_CODE, CATEGORY_UNIT_NAME, JOIN_UNIT_UUID,
          JOIN_UNIT_CODE, JOIN_UNIT_NAME, SUBMIT_NAME, SUBMIT_ID, SKU_ID, SUBMIT_NAMESPACE, SUBMIT_INFO_TIME, SIGN_DATE
  };

  public static Map<String, Object> toFieldValues(String activityUuid, ExplosiveActivitySignJoin signJoin, ExplosiveActivitySignJoin.ExplosiveActivitySignLine signLine) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString().replace("-", ""));
    fvm.put(ACTIVITY_UUID, activityUuid);
    fvm.put(SIGN_QTY, signLine.getSignQty());
    fvm.put(SIGN_DATE, signLine.getSignDate());
    if (signJoin.getSubmitInfo() != null) {
      if (signJoin.getSubmitInfo().getOperator() != null) {
        fvm.put(SUBMIT_ID, signJoin.getSubmitInfo().getOperator().getId());
        fvm.put(SUBMIT_NAME, signJoin.getSubmitInfo().getOperator().getFullName());
        fvm.put(SUBMIT_NAMESPACE, signJoin.getSubmitInfo().getOperator().getNamespace());
      }
      fvm.put(SUBMIT_INFO_TIME, signJoin.getSubmitInfo().getTime());
    }
    if (signJoin.getStore() != null) {
      fvm.put(JOIN_UNIT_UUID, signJoin.getStore().getUuid());
      fvm.put(JOIN_UNIT_CODE, signJoin.getStore().getCode());
      fvm.put(JOIN_UNIT_NAME, signJoin.getStore().getName());
    }
    if (signLine.getEntity() != null) {
      if (signLine.getEntity().getCategory() != null) {
        fvm.put(CATEGORY_UNIT_ID, signLine.getEntity().getCategory().getId());
        fvm.put(CATEGORY_UNIT_CODE, signLine.getEntity().getCategory().getCode());
        fvm.put(CATEGORY_UNIT_NAME, signLine.getEntity().getCategory().getName());
      }
      fvm.put(SKU_ID, signLine.getEntity().getSkuId());
      fvm.put(QPC, signLine.getEntity().getQpc());
      fvm.put(ALC_QPC, signLine.getEntity().getAlcQpc());
      fvm.put(ALC_UNIT, signLine.getEntity().getAlcUnit());
      fvm.put(PRICE, signLine.getEntity().getPrice());
      fvm.put(ENTITY_UNIT_UUID, signLine.getEntity().getUuid());
      fvm.put(ENTITY_UNIT_CODE, signLine.getEntity().getCode());
      fvm.put(ENTITY_UNIT_NAME, signLine.getEntity().getName());
      fvm.put(MANUFACTORY, signLine.getEntity().getManufactory());
      fvm.put(MEASUREUNIT, signLine.getEntity().getMeasureUnit());
      fvm.put(SPECIFICATION, signLine.getEntity().getSpecification());
      fvm.put(ENTITY_TYPE, signLine.getEntity().getEntityType().name());
    }
    return fvm;
  }

  public static void inject(ResultSet rs, ExplosiveActivitySignJoin target) throws SQLException {
    target.setSubmitInfo(new OperateInfo(rs.getTimestamp(SUBMIT_INFO_TIME), new Operator(rs.getString(SUBMIT_NAMESPACE), rs.getString(SUBMIT_ID), rs.getString(SUBMIT_NAME))));
    target.setStore(new UCN(rs.getString(JOIN_UNIT_UUID), rs.getString(JOIN_UNIT_CODE), rs.getString(JOIN_UNIT_NAME)));
  }

  public static void inject(ResultSet rs, ExplosiveActivitySignJoin.ExplosiveActivitySignLine target) throws SQLException {
    PomEntity pomEntity = new PomEntity();
    pomEntity.setSkuId(rs.getString(SKU_ID));
    pomEntity.setUuid(rs.getString(ENTITY_UNIT_UUID));
    pomEntity.setCode(rs.getString(ENTITY_UNIT_CODE));
    pomEntity.setName(rs.getString(ENTITY_UNIT_NAME));
    pomEntity.setEntityType(StringUtil.toEnum(rs.getString(ENTITY_TYPE), EntityType.class));
    pomEntity.setQpc(rs.getBigDecimal(QPC));
    Category category = new Category();
    category.setId(rs.getString(CATEGORY_UNIT_ID));
    category.setCode(rs.getString(CATEGORY_UNIT_CODE));
    category.setName(rs.getString(CATEGORY_UNIT_NAME));
    pomEntity.setCategory(category);
    pomEntity.setSpecification(rs.getString(SPECIFICATION));
    pomEntity.setMeasureUnit(rs.getString(MEASUREUNIT));
    pomEntity.setAlcQpc(rs.getBigDecimal(ALC_QPC));
    pomEntity.setAlcUnit(rs.getString(ALC_UNIT));
    pomEntity.setManufactory(rs.getString(MANUFACTORY));
    pomEntity.setPrice(rs.getBigDecimal(PRICE));
    target.setSignDate(rs.getTimestamp(SIGN_DATE));
    target.setSignQty(rs.getBigDecimal(SIGN_QTY));
    target.setEntity(pomEntity);
  }
}
