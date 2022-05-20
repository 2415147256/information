package com.hd123.baas.sop.service.dao.explosive;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivity;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivityDetail;
import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.baas.sop.service.api.promotion.EntityType;
import com.hd123.baas.sop.service.api.promotion.PomEntity;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.spms.commons.calendar.DateRange;

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
@Table(caption = PExplosiveActivityDetail.TABLE_CAPTION, name = PExplosiveActivityDetail.TABLE_NAME, indexes = {
        @Index(name = "idx_explosive_activity_detail_1", columnNames = { PExplosiveActivityDetail.ACTIVITY_UUID})
})
public class PExplosiveActivityDetail extends PEntity {

  public static final String TABLE_NAME = "sop_explosive_activity_detail";
  public static final String TABLE_CAPTION = "爆品预定记录";

  @Column(title = "租户", name = PExplosiveActivityDetail.TENANT, length = 38)
  public static final String TENANT = "tenant";

  @Column(title = "组织ID", name = PExplosiveActivityDetail.TENANT, length = 38)
  public static final String ORG_ID = "orgId";

  @Column(title = "活动状态", name = PExplosiveActivityDetail.ACTIVITY_STATE, length = 32)
  public static final String ACTIVITY_STATE = "activityState";

  @Column(title = "促销活动uuid", name = PExplosiveActivityDetail.ACTIVITY_UUID, length = 38)
  public static final String ACTIVITY_UUID = "activityUuid";
  @Column(title = "促销活动Code", name = PExplosiveActivityDetail.ACTIVITY_CODE, length = 64)
  public static final String ACTIVITY_CODE = "activityCode";
  @Column(title = "促销活动名称", name = PExplosiveActivityDetail.ACTIVITY_NAME, length = 128)
  public static final String ACTIVITY_NAME = "activityName";
  @Column(title = "门店uuid", name = PExplosiveActivityDetail.STORE_EXAMPLE_UUID, length = 38)
  public static final String STORE_EXAMPLE_UUID = "joinUnitUuid";
  @Column(title = "门店Code", name = PExplosiveActivityDetail.STORE_EXAMPLE_CODE, length = 64)
  public static final String STORE_EXAMPLE_CODE = "joinUnitCode";
  @Column(title = "门店名称", name = PExplosiveActivityDetail.STORE_EXAMPLE_NAME, length = 128)
  public static final String STORE_EXAMPLE_NAME = "joinUnitName";
  @Column(title = "门店数量", name = PExplosiveActivityDetail.STORE_COUNT, fieldClass = Long.class)
  public static final String STORE_COUNT = "storeCount";

  @Column(title = "skuId", name = PExplosiveActivityDetail.SKU_ID, length = 38)
  public static final String SKU_ID = "skuId";
  @Column(title = "商品uuid", name = PExplosiveActivityDetail.ENTITY_UUID, length = 38)
  public static final String ENTITY_UUID = "entityUuid";
  @Column(title = "商品Code", name = PExplosiveActivityDetail.ENTITY_CODE, length = 64)
  public static final String ENTITY_CODE = "entityCode";
  @Column(title = "商品名称", name = PExplosiveActivityDetail.ENTITY_NAME, length = 128)
  public static final String ENTITY_NAME = "entityName";
  @Column(title = "商品分类id", name = PExplosiveActivityDetail.CATEGORY_ID, length = 38)
  public static final String CATEGORY_ID = "categoryId";
  @Column(title = "商品分类Code", name = PExplosiveActivityDetail.CATEGORY_CODE, length = 64)
  public static final String CATEGORY_CODE = "categoryCode";
  @Column(title = "商品分类名称", name = PExplosiveActivityDetail.CATEGORY_NAME, length = 128)
  public static final String CATEGORY_NAME = "categoryName";
  @Column(title = "配货规格", name = PExplosiveActivityDetail.ALC_QPC, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String ALC_QPC = "alcQpc";
  @Column(title = "配货单位", name = PExplosiveActivityDetail.ALC_UNIT, length = 32)
  public static final String ALC_UNIT = "alcUnit";


  @Column(title = "类型", name = PExplosiveActivityLine.ENTITY_TYPE, length = 64)
  public static final String ENTITY_TYPE = "entityType";
  @Column(title = "qpc", name = PExplosiveActivityLine.QPC, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String QPC = "qpc";
  @Column(title = "规格", name = PExplosiveActivityDetail.SPECIFICATION, length = 64)
  public static final String SPECIFICATION = "specification";
  @Column(title = "生产厂家", name = PExplosiveActivityDetail.MANUFACTORY, length = 64)
  public static final String MANUFACTORY = "manufactory";
  @Column(title = "计量单位", name = PExplosiveActivityDetail.MEASUREUNIT, length = 32)
  public static final String MEASUREUNIT = "measureUnit";
  @Column(title = "原价", name = PExplosiveActivityDetail.PRICE, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String PRICE = "price";
  @Column(title = "活动开始时间", name = PExplosiveActivityDetail.ACTIVITY_RANGE_BEGIN_DATE, fieldClass = Date.class)
  public static final String ACTIVITY_RANGE_BEGIN_DATE = "beginDate";
  @Column(title = "活动结束时间", name = PExplosiveActivityDetail.ACTIVITY_RANGE_END_DATE, fieldClass = Date.class)
  public static final String ACTIVITY_RANGE_END_DATE = "endDate";
  @Column(title = "订货量", name = PExplosiveActivityDetail.TOTAL_SIGN_QTY, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String TOTAL_SIGN_QTY = "totalSignQty";

  public static final String[] COLUMNS = { PEntity.UUID, TENANT,ORG_ID, ACTIVITY_STATE, ACTIVITY_UUID, ACTIVITY_CODE, ACTIVITY_NAME, STORE_EXAMPLE_UUID, QPC, ENTITY_TYPE,
          STORE_EXAMPLE_CODE, STORE_EXAMPLE_NAME, ENTITY_UUID, ENTITY_CODE, ENTITY_NAME, CATEGORY_ID, CATEGORY_CODE, ALC_QPC, ALC_UNIT,
          CATEGORY_NAME, ACTIVITY_RANGE_BEGIN_DATE, ACTIVITY_RANGE_END_DATE, SPECIFICATION, MANUFACTORY, MEASUREUNIT, PRICE, SKU_ID, TOTAL_SIGN_QTY, STORE_COUNT};

  public static Map<String, Object> toFieldValues(ExplosiveActivityDetail entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString());
    fvm.put(ORG_ID,entity.getOrgId());
    fvm.put(TOTAL_SIGN_QTY, entity.getTotalSignQty());
    fvm.put(STORE_COUNT, entity.getStoreCount());
    fvm.put(TENANT, entity.getTenant());
    if (entity.getActivityState() != null) {
      fvm.put(ACTIVITY_STATE, entity.getActivityState());
    }
    if (entity.getStoreExample() != null) {
      fvm.put(STORE_EXAMPLE_UUID, entity.getStoreExample().getUuid());
      fvm.put(STORE_EXAMPLE_CODE, entity.getStoreExample().getCode());
      fvm.put(STORE_EXAMPLE_NAME, entity.getStoreExample().getName());
    }
    if (entity.getActivity() != null) {
      fvm.put(ACTIVITY_UUID, entity.getActivity().getUuid());
      fvm.put(ACTIVITY_CODE, entity.getActivity().getCode());
      fvm.put(ACTIVITY_NAME, entity.getActivity().getName());
    }
    if (entity.getEntity() != null) {
      fvm.put(ENTITY_UUID, entity.getEntity().getUuid());
      fvm.put(ENTITY_CODE, entity.getEntity().getCode());
      fvm.put(ENTITY_NAME, entity.getEntity().getName());
      if (entity.getEntity().getEntityType() != null) {
        fvm.put(ENTITY_TYPE, entity.getEntity().getEntityType().name());
      }
      fvm.put(SKU_ID, entity.getEntity().getSkuId());
      fvm.put(QPC, entity.getEntity().getQpc());
      fvm.put(ALC_QPC, entity.getEntity().getAlcQpc());
      fvm.put(ALC_UNIT, entity.getEntity().getAlcUnit());
      fvm.put(SPECIFICATION, entity.getEntity().getSpecification());
      fvm.put(MANUFACTORY, entity.getEntity().getManufactory());
      fvm.put(PRICE, entity.getEntity().getPrice());
      fvm.put(MEASUREUNIT, entity.getEntity().getMeasureUnit());
      if (entity.getEntity().getCategory() != null) {
        fvm.put(CATEGORY_ID, entity.getEntity().getCategory().getId());
        fvm.put(CATEGORY_CODE, entity.getEntity().getCategory().getCode());
        fvm.put(CATEGORY_NAME, entity.getEntity().getCategory().getName());
      }
    }
    DateRange dateRange = entity.getActivityRange();
    fvm.put(ACTIVITY_RANGE_BEGIN_DATE, dateRange.getBeginDate());
    fvm.put(ACTIVITY_RANGE_END_DATE, dateRange.getEndDate());
    return fvm;
  }

  public static class RowMapper extends com.hd123.rumba.commons.jdbc.entity.PStandardEntity.RowMapper<ExplosiveActivityDetail> {
    @Override
    public ExplosiveActivityDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
      ExplosiveActivityDetail target = new ExplosiveActivityDetail();
      target.setUuid(rs.getString(UUID));
      target.setTenant(rs.getString(TENANT));
      Category category = new Category();
      category.setId(rs.getString(CATEGORY_ID));
      category.setCode(rs.getString(CATEGORY_CODE));
      category.setName(rs.getString(CATEGORY_NAME));
      PomEntity pomEntity = new PomEntity();
      pomEntity.setCategory(category);
      pomEntity.setSkuId(rs.getString(SKU_ID));
      pomEntity.setQpc(rs.getBigDecimal(QPC));
      pomEntity.setPrice(rs.getBigDecimal(PRICE));
      pomEntity.setUuid(rs.getString(ENTITY_UUID));
      pomEntity.setAlcQpc(rs.getBigDecimal(ALC_QPC));
      pomEntity.setAlcUnit(rs.getString(ALC_UNIT));
      pomEntity.setName(rs.getString(ENTITY_NAME));
      pomEntity.setCode(rs.getString(ENTITY_CODE));
      pomEntity.setMeasureUnit(rs.getString(MEASUREUNIT));
      pomEntity.setManufactory(rs.getString(MANUFACTORY));
      pomEntity.setSpecification(rs.getString(SPECIFICATION));
      pomEntity.setEntityType(StringUtil.toEnum(rs.getString(ENTITY_TYPE), EntityType.class));
      target.setEntity(pomEntity);
      target.setStoreCount(rs.getLong(STORE_COUNT));
      target.setTotalSignQty(rs.getBigDecimal(TOTAL_SIGN_QTY));
      target.setActivityRange(new DateRange(rs.getTimestamp(ACTIVITY_RANGE_BEGIN_DATE), rs.getTimestamp(ACTIVITY_RANGE_END_DATE)));
      target.setStoreExample(new UCN(rs.getString(STORE_EXAMPLE_UUID), rs.getString(STORE_EXAMPLE_CODE), rs.getString(STORE_EXAMPLE_NAME)));
      target.setActivity(new UCN(rs.getString(ACTIVITY_UUID), rs.getString(ACTIVITY_CODE), rs.getString(ACTIVITY_NAME)));

      target.setActivityState(rs.getString(ACTIVITY_STATE));
      if (target.getActivityFrontState() == null) {
        target.setActivityFrontState(target.getActivityState());
      }
      DateRange dateRange = target.getActivityRange();
      Date date = new Date();
      if (target.getActivityState().equals(ExplosiveActivity.State.audited.name())) {
        if (dateRange.getBeginDate().compareTo(date) <= 0 && dateRange.getEndDate().compareTo(date) >= 0) {
          target.setActivityFrontState(ExplosiveActivity.FRONT_STATE_EFFECT);
        } else if (dateRange.getEndDate().compareTo(date) < 0) {
          target.setActivityFrontState(ExplosiveActivity.FRONT_STATE_EXPIRED);
        }
      }
      return target;
    }
  }
}
