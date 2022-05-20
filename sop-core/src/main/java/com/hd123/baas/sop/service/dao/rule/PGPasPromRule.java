package com.hd123.baas.sop.service.dao.rule; /**
 * <p>
 * 项目名: sop-pms-parent
 * 文件名: PGPasPromRule.java
 * 模块说明:
 * 修改历史:
 * 2020年10月28日 - wushuaijun- 创建
 */

import com.hd123.baas.sop.service.api.pms.gpas.rule.GPasPromRule;
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
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.commons.json.JsonUtil;
import com.hd123.spms.commons.util.CollectionUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Entity
@Table(caption = PGPasPromRule.TABLE_CAPTION, name = PGPasPromRule.TABLE_NAME, indexes = {
        @Index(name = "idx_g_pas_prom_rule_1", columnNames = { PGPasPromRule.TENANT, PGPasPromRule.BILL_NUMBER}),
})
public class PGPasPromRule extends PStandardEntity {
  public static final String TABLE_NAME = "sop_gpas_prom_rule";
  public static final String TABLE_CAPTION = "批发订货促销规则单";

  @Column(title = "租户", name = PGPasPromRule.TENANT, length = 38)
  public static final String TENANT = "tenant";

  @Column(title = "批发订货促销规则编号", name = PGPasPromRule.BILL_NUMBER, length = 38)
  public static final String BILL_NUMBER = "billNumber";

  @Column(title = "名称", name = PGPasPromRule.NAME, length = 64)
  public static final String NAME = "name";

  @Column(title = "会员专享", name = PGPasPromRule.ONLY_MEMBER, fieldClass = Boolean.class)
  public static final String ONLY_MEMBER = "onlyMember";
  @Column(title = "促销渠道", name = PGPasPromRule.PROM_CHANNELS, length = 100)
  public static final String PROM_CHANNELS = "promChannels";

  @Column(title = "营销物料费", name = PGPasPromRule.MATERIEL_AMOUNT, fieldClass = BigDecimal.class)
  public static final String MATERIEL_AMOUNT = "materielAmount";

  @Column(title = "状态", name = PGPasPromRule.STATE, length = 32)
  public static final String STATE = "state";

  @Column(title = "是否适用与全部门店", name = PGPasPromRule.JOIN_UNITS_ALL_UNIT, fieldClass = Boolean.class)
  public static final String JOIN_UNITS_ALL_UNIT = "allUnit";

  @Column(title = "促销类型", name = PGPasPromRule.PROMOTION_PROMOTION_TYPE, length = 20)
  public static final String PROMOTION_PROMOTION_TYPE = "promotionType";
  @Column(title = "促销方式", name = PGPasPromRule.PROMOTION_PROMOTION_MODE, length = 40)
  public static final String PROMOTION_PROMOTION_MODE = "promotionMode";
  @Column(title = "促销描述", name = PGPasPromRule.PROMOTION_DESCRIPTION, length = 255)
  public static final String PROMOTION_DESCRIPTION = "description";

  @Column(title = "促销开始时间", name = PGPasPromRule.DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE = "beginDate";

  @Column(title = "促销结束时间", name = PGPasPromRule.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_END_DATE = "endDate";
  @Lob
  @Column(title = "促销日期", name = PGPasPromRule.DATE_RANGE_CONDITION)
  public static final String DATE_RANGE_CONDITION = "dateRangeCondition";
  @Lob
  @Column(title = "时段促销", name = PGPasPromRule.TIME_PERIOD_CONDITION)
  public static final String TIME_PERIOD_CONDITION = "timePeriodCondition";
  @Lob
  @Column(title = "促销设置", name = PGPasPromRule.PROMOTION)
  public static final String PROMOTION = "promotion";

  @Column(title = "促销说明", name = PGPasPromRule.PROM_NOTE, length = 255)
  public static final String PROM_NOTE = "promNote";

  public static final String[] COLUMNS = ArrayUtils.addAll(
          PStandardEntity.COLUMNS, MATERIEL_AMOUNT,
          TENANT, BILL_NUMBER, NAME, ONLY_MEMBER, PROM_CHANNELS, STATE, JOIN_UNITS_ALL_UNIT,
          DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, DATE_RANGE_CONDITION, TIME_PERIOD_CONDITION,
          PROMOTION_PROMOTION_TYPE, PROMOTION_PROMOTION_MODE, PROMOTION_DESCRIPTION,
          // PROMOTION,
          PROM_NOTE);

  public static Map<String, Object> toFieldValues(GPasPromRule entity) {
    Map<String, Object> fvm = Utils.toFieldValues(entity);
    fvm.put(TENANT, entity.getTenant());
    fvm.put(BILL_NUMBER, entity.getBillNumber());
    fvm.put(NAME, entity.getName());
    fvm.put(STATE, entity.getState().name());
    fvm.put(MATERIEL_AMOUNT, entity.getMaterielAmount());
    fvm.put(PROM_CHANNELS, CollectionUtil.toString(entity.getPromChannels()));
    if (entity.getJoinUnits() != null) {
      fvm.put(JOIN_UNITS_ALL_UNIT, entity.getJoinUnits().getAllUnit());
    }
    fvm.put(ONLY_MEMBER, entity.getOnlyMember());
    if (entity.getDateRangeCondition() != null) {
      fvm.put(DATE_RANGE_CONDITION, JsonUtil.objectToJson(entity.getDateRangeCondition()));
      if (entity.getDateRangeCondition().getDateRange() != null) {
        fvm.put(DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, entity.getDateRangeCondition().getDateRange().getBeginDate());
        fvm.put(DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, entity.getDateRangeCondition().getDateRange().getEndDate());
      }
    }
    if (entity.getPromotion() != null) {
      fvm.put(PROMOTION, JsonUtil.objectToJson(entity.getPromotion()));
      fvm.put(PROMOTION_PROMOTION_TYPE, entity.getPromotion().getPromotionType() == null ? null : entity.getPromotion().getPromotionType().toString());
      fvm.put(PROMOTION_PROMOTION_MODE, entity.getPromotion().getPromotionMode());
      fvm.put(PROMOTION_DESCRIPTION, entity.getPromotion().getDescription());
    }
    fvm.put(TIME_PERIOD_CONDITION, JsonUtil.objectToJson(entity.getTimePeriodCondition()));
    fvm.put(PROM_NOTE, entity.getPromNote());
    return fvm;
  }

  public static class RowMapper extends com.hd123.rumba.commons.jdbc.entity.PStandardEntity.RowMapper<GPasPromRule> {
    private List<String> fetchParts;

    public RowMapper(List<String> fetchParts) {
      this.fetchParts = fetchParts;
    }

    @Override
    public GPasPromRule mapRow(ResultSet rs, int i) throws SQLException {
      GPasPromRule target = new GPasPromRule();
      Utils.mapRow(rs, target);
      target.setTenant(rs.getString(TENANT));
      target.setBillNumber(rs.getString(BILL_NUMBER));
      target.setName(rs.getString(NAME));
      target.setOnlyMember(rs.getBoolean(ONLY_MEMBER));
      target.setPromChannels(CollectionUtil.toList(rs.getString(PROM_CHANNELS)));
      target.setMaterielAmount(rs.getBigDecimal(MATERIEL_AMOUNT));
      target.setState(StringUtil.toEnum(rs.getString(STATE), GPasPromRule.State.class));
      PromotionJoinUnits promotionJoinUnits = new PromotionJoinUnits();
      promotionJoinUnits.setAllUnit(rs.getBoolean(JOIN_UNITS_ALL_UNIT));
      target.setJoinUnits(promotionJoinUnits);
      target.setDateRangeCondition(JsonUtil.jsonToObject(rs.getString(DATE_RANGE_CONDITION), DateRangeCondition.class));
      target.setTimePeriodCondition(JsonUtil.jsonToObject(rs.getString(TIME_PERIOD_CONDITION), TimePeriodCondition.class));
      DateRange dateRange = target.getDateRangeCondition().getDateRange();
      Date date = new Date();
      if (target.getFrontState() == null) {
        target.setFrontState(target.getState().name());
      }
      if (target.getState().equals(GPasPromRule.State.audited)) {
        if (dateRange.getBeginDate().compareTo(date) <= 0 && dateRange.getEndDate().compareTo(date) >= 0) {
          target.setFrontState(GPasPromRule.FRONT_STATE_EFFECT);
        } else if (dateRange.getEndDate().compareTo(date) < 0) {
          target.setFrontState(GPasPromRule.FRONT_STATE_EXPIRED);
        }
      }
      if (fetchParts != null && fetchParts.contains(GPasPromRule.PARTS_PROMOTION)) {
        target.setPromotion(JsonUtil.jsonToObject(rs.getString(PROMOTION), Promotion.class));
      } else {
        Promotion promotion = new Promotion();
        promotion.setPromotionType(StringUtil.toEnum(rs.getString(PROMOTION_PROMOTION_TYPE), PromotionType.class));
        promotion.setPromotionMode(rs.getString(PROMOTION_PROMOTION_MODE));
        promotion.setDescription(rs.getString(PROMOTION_DESCRIPTION));
        target.setPromotion(promotion);
      }
      target.setPromNote(rs.getString(PROM_NOTE));
      return target;
    }
  }

}
