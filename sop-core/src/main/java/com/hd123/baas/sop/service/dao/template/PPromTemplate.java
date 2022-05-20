package com.hd123.baas.sop.service.dao.template;

import com.hd123.baas.sop.service.api.pms.template.PromTemplate;
import com.hd123.baas.sop.service.dao.PStandardEntity;
import com.hd123.baas.sop.service.api.promotion.Promotion;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.PromotionType;
import com.hd123.baas.sop.service.api.promotion.condition.DateRangeCondition;
import com.hd123.baas.sop.service.api.promotion.condition.TimePeriodCondition;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Lob;
import com.hd123.devops.ebt.annotation.Table;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.spms.commons.json.JsonUtil;
import com.hd123.spms.commons.util.CollectionUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhuangwenting
 * @since 1.0
 */
@Entity
@Table(caption = PPromTemplate.TABLE_CAPTION, name = PPromTemplate.TABLE_NAME, indexes = {
        @Index(name = "idx_prom_template_1", columnNames = { PPromTemplate.TENANT, PPromTemplate.CODE})
})
public class PPromTemplate extends PStandardEntity {

  public static final String TABLE_NAME = "sop_prom_template";
  public static final String TABLE_CAPTION = "促销模板";

  @Column(title = "租户", name = PPromTemplate.TENANT, length = 80)
  public static final String TENANT = "tenant";
  @Column(title = "组织ID", name = PPromTemplate.ORG_ID, length = 80)
  public static final String ORG_ID = "orgId";
  @Column(title = "模板代码", name = PPromTemplate.CODE, length = 64)
  public static final String CODE = "code";
  @Column(title = "名称", name = PPromTemplate.NAME, length = 128)
  public static final String NAME = "name";
  @Column(title = "系统预定义", name = PPromTemplate.PREDEFINE, fieldClass = Boolean.class)
  public static final String PREDEFINE = "predefine";
  @Column(title = "模板说明", name = PPromTemplate.REMARK, length = 128)
  public static final String REMARK = "remark";

  @Column(title = "会员专享", name = PPromTemplate.ONLY_MEMBER, fieldClass = Boolean.class)
  public static final String ONLY_MEMBER = "onlyMember";
  @Column(title = "促销渠道", name = PPromTemplate.PROM_CHANNELS, length = 100)
  public static final String PROM_CHANNELS = "promChannels";

  @Column(title = "是否全部门店", name = PPromTemplate.ALL_UNIT, fieldClass = Boolean.class)
  public static final String ALL_UNIT = "allUnit";

  @Lob
  @Column(title = "促销日期", name = PPromTemplate.DATE_RANGE_CONDITION, length = 5000)
  public static final String DATE_RANGE_CONDITION = "dateRangeCondition";
  @Lob
  @Column(title = "时段促销", name = PPromTemplate.TIME_PERIOD_CONDITION, length = 1024)
  public static final String TIME_PERIOD_CONDITION = "timePeriodCondition";
  @Lob
  @Column(title = "促销内容", name = PPromTemplate.PROMOTION, length = 5000)
  public static final String PROMOTION = "promotion";
  @Column(title = "促销内容-促销类型", name = PPromTemplate.PROMOTION_PROMOTION_TYPE, length = 20)
  public static final String PROMOTION_PROMOTION_TYPE = "promotionType";
  @Column(title = "促销内容-促销方式", name = PPromTemplate.PROMOTION_MODE, length = 64)
  public static final String PROMOTION_MODE = "promotionMode";
  @Column(title = "促销内容-促销描述", name = PPromTemplate.DESCRIPTION, length = 128)
  public static final String DESCRIPTION = "description";
  @Column(title = "促销说明", name = PPromTemplate.PROM_NOTE, length = 255)
  public static final String PROM_NOTE = "promNote";

  @Column(title = "促销开始日期", name = PPromTemplate.DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE = "startDate";
  @Column(title = "促销开始日期", name = PPromTemplate.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_END_DATE = "endDate";

  @Column(title = "字段控制", name = PPromTemplate.FIELD_CONTROLS, length = 1024)
  public static final String FIELD_CONTROLS = "fieldControls";

  public static final String[] COLUMNS = ArrayUtils.addAll(
          PStandardEntity.COLUMNS,ORG_ID, TENANT, CODE, NAME, PREDEFINE, PROM_NOTE, ALL_UNIT,
          DATE_RANGE_CONDITION, TIME_PERIOD_CONDITION, ONLY_MEMBER, PROM_CHANNELS,
          // PROMOTION,
          PROMOTION_PROMOTION_TYPE, PROMOTION_MODE, DESCRIPTION, REMARK, DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, FIELD_CONTROLS);

  public static Map<String, Object> toFieldValues(PromTemplate entity) {
    Map<String, Object> fvm = Utils.toFieldValues(entity);
    fvm.put(TENANT, entity.getTenant());
    fvm.put(ORG_ID,entity.getOrgId());
    fvm.put(CODE, entity.getCode());
    fvm.put(NAME, entity.getName());
    fvm.put(PREDEFINE, entity.isPredefine());
    fvm.put(PROM_NOTE, entity.getPromNote());
    if (entity.getGrantUnits() != null) {
      fvm.put(ALL_UNIT, entity.getGrantUnits().getAllUnit());
    }
    fvm.put(ONLY_MEMBER, entity.getOnlyMember());
    fvm.put(REMARK, entity.getRemark());
    fvm.put(DATE_RANGE_CONDITION, JsonUtil.objectToJson(entity.getDateRangeCondition()));
    fvm.put(TIME_PERIOD_CONDITION, JsonUtil.objectToJson(entity.getTimePeriodCondition()));
    fvm.put(PROM_CHANNELS, CollectionUtil.toString(entity.getPromChannels()));
    if (entity.getPromotion() != null) {
      fvm.put(PROMOTION_PROMOTION_TYPE, entity.getPromotion().getPromotionType().name());
      fvm.put(PROMOTION_MODE, entity.getPromotion().getPromotionMode());
      fvm.put(DESCRIPTION, entity.getPromotion().getDescription());
      fvm.put(PROMOTION, JsonUtil.objectToJson(entity.getPromotion()));
    }
    if (entity.getDateRangeCondition() != null) {
      fvm.put(DATE_RANGE_CONDITION, JsonUtil.objectToJson(entity.getDateRangeCondition()));
      if (entity.getDateRangeCondition().getDateRange() != null) {
        fvm.put(DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, entity.getDateRangeCondition().getDateRange().getBeginDate());
        fvm.put(DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, entity.getDateRangeCondition().getDateRange().getEndDate());
      }
    }
    fvm.put(FIELD_CONTROLS, JsonUtil.objectToJson(entity.getFieldControls()));
    return fvm;
  }

  public static class RowMapper extends com.hd123.rumba.commons.jdbc.entity.PStandardEntity.RowMapper<PromTemplate> {
    private List<String> fetchParts;

    public RowMapper(List<String> fetchParts) {
      this.fetchParts = fetchParts;
    }

    @Override
    public PromTemplate mapRow(ResultSet rs, int i) throws SQLException {
      PromTemplate target = new PromTemplate();
      Utils.mapRow(rs, target);
      target.setUuid(rs.getString(UUID));
      target.setOrgId(rs.getString(ORG_ID));
      target.setTenant(rs.getString(TENANT));
      target.setCode(rs.getString(CODE));
      target.setName(rs.getString(NAME));
      target.setPredefine(rs.getBoolean(PREDEFINE));
      target.setPromNote(rs.getString(PROM_NOTE));
      if (StringUtils.isNoneBlank(rs.getString(ONLY_MEMBER))) {
        target.setOnlyMember(rs.getBoolean(ONLY_MEMBER));
      }
      target.setDateRangeCondition(JsonUtil.jsonToObject(rs.getString(DATE_RANGE_CONDITION), DateRangeCondition.class));
      target.setTimePeriodCondition(JsonUtil.jsonToObject(rs.getString(TIME_PERIOD_CONDITION), TimePeriodCondition.class));
      target.setRemark(rs.getString(REMARK));
      target.setFieldControls(JsonUtil.jsonToObject(rs.getString(FIELD_CONTROLS), Map.class));
      target.setPromChannels(CollectionUtil.toList(rs.getString(PROM_CHANNELS)));

      if (fetchParts != null && fetchParts.contains(PromTemplate.PARTS_PROMOTION)) {
        target.setPromotion(JsonUtil.jsonToObject(rs.getString(PROMOTION), Promotion.class));
      } else {
        Promotion promotion = new Promotion();
        promotion.setPromotionType(StringUtil.toEnum(rs.getString(PROMOTION_PROMOTION_TYPE), PromotionType.class));
        promotion.setPromotionMode(rs.getString(PROMOTION_MODE));
        target.setPromotion(promotion);
      }
      PromotionJoinUnits promotionJoinUnits = new PromotionJoinUnits();
      if (StringUtils.isNoneBlank(rs.getString(ALL_UNIT))) {
        promotionJoinUnits.setAllUnit(rs.getBoolean(ALL_UNIT));
      }
      target.setGrantUnits(promotionJoinUnits);
      return target;
    }
  }

}
