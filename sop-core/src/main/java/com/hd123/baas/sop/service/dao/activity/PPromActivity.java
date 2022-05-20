/**
 * <p>
 * 项目名: sop-pms-parent
 * 文件名: PPromActivity.java
 * 模块说明:
 * 修改历史:
 * 2020年10月28日 - wushuaijun- 创建
 */
package com.hd123.baas.sop.service.dao.activity;

import com.hd123.baas.sop.config.PromotionTarget;
import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.service.dao.PStandardEntity;
import com.hd123.baas.sop.service.api.promotion.Promotion;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
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
@Table(caption = PPromActivity.TABLE_CAPTION, name = PPromActivity.TABLE_NAME, indexes = {
        @Index(name = "idx_prom_activity_1", columnNames = { PPromActivity.TENANT, PPromActivity.BILL_NUMBER})
})
public class PPromActivity extends PStandardEntity {
  public static final String TABLE_NAME = "sop_prom_activity";
  public static final String TABLE_CAPTION = "促销活动";

  @Column(title = "租户", name = PPromActivity.TENANT, length = 38)
  public static final String TENANT = "tenant";

  @Column(title = "组织ID", name = PPromActivity.ORG_ID, length = 38)
  public static final String ORG_ID = "orgId";

    @Column(title = "活动编号", name = PPromActivity.BILL_NUMBER, length = 38)
  public static final String BILL_NUMBER = "billNumber";

  @Column(title = "名称", name = PPromActivity.NAME, length = 64)
  public static final String NAME = "name";

  @Column(title = "会员专享", name = PPromActivity.ONLY_MEMBER, fieldClass = Boolean.class)
  public static final String ONLY_MEMBER = "onlyMember";
  @Column(title = "促销渠道", name = PPromActivity.PROM_CHANNELS, length = 100)
  public static final String PROM_CHANNELS = "promChannels";

  @Column(title = "营销物料费", name = PPromActivity.MATERIEL_AMOUNT, fieldClass = BigDecimal.class)
  public static final String MATERIEL_AMOUNT = "materielAmount";

  @Column(title = "状态", name = PPromActivity.STATE, length = 32)
  public static final String STATE = "state";

  @Column(title = "是否适用与全部门店", name = PPromActivity.JOIN_UNITS_ALL_UNIT, fieldClass = Boolean.class)
  public static final String JOIN_UNITS_ALL_UNIT = "allUnit";

  @Column(title = "促销开始时间", name = PPromActivity.DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE = "beginDate";

  @Column(title = "促销结束时间", name = PPromActivity.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_END_DATE = "endDate";
  @Lob
  @Column(title = "促销日期", name = PPromActivity.DATE_RANGE_CONDITION)
  public static final String DATE_RANGE_CONDITION = "dateRangeCondition";
  @Lob
  @Column(title = "时段促销", name = PPromActivity.TIME_PERIOD_CONDITION)
  public static final String TIME_PERIOD_CONDITION = "timePeriodCondition";
  @Lob
  @Column(title = "促销设置", name = PPromActivity.PROMOTIONS)
  public static final String PROMOTIONS = "promotions";

  @Column(title = "促销说明", name = PPromActivity.PROM_NOTE, length = 255)
  public static final String PROM_NOTE = "promNote";

  @Column(title = "促销原因", name = PPromActivity.MARKET_REASON, length = 64)
  public static final String MARKET_REASON = "market_reason";

  @Column(title = "目标值", name = PPromActivity.PROMOTION_TARGETS, length = 256)
  public static final String PROMOTION_TARGETS = "promotion_targets";

  public static final String[] COLUMNS = ArrayUtils.addAll(
          PStandardEntity.COLUMNS, MATERIEL_AMOUNT,
          TENANT,ORG_ID, BILL_NUMBER, NAME, ONLY_MEMBER, PROM_CHANNELS, STATE, JOIN_UNITS_ALL_UNIT,
          DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, DATE_RANGE_CONDITION, TIME_PERIOD_CONDITION,
          // PROMOTIONS,
          PROM_NOTE, MARKET_REASON, PROMOTION_TARGETS);

  public static Map<String, Object> toFieldValues(PromActivity entity) {
    Map<String, Object> fvm = Utils.toFieldValues(entity);
    fvm.put(TENANT, entity.getTenant());
    fvm.put(ORG_ID, entity.getOrgId());
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
    fvm.put(TIME_PERIOD_CONDITION, JsonUtil.objectToJson(entity.getTimePeriodCondition()));
    fvm.put(PROMOTIONS, JsonUtil.objectToJson(entity.getPromotions()));
    fvm.put(PROM_NOTE, entity.getPromNote());
    fvm.put(MARKET_REASON, entity.getMarketReason());
    fvm.put(PROMOTION_TARGETS, JsonUtil.objectToJson(entity.getPromotionTargets()));
    return fvm;
  }

  public static class RowMapper extends com.hd123.rumba.commons.jdbc.entity.PStandardEntity.RowMapper<PromActivity> {
    private List<String> fetchParts;

    public RowMapper(List<String> fetchParts) {
      this.fetchParts = fetchParts;
    }

    @Override
    public PromActivity mapRow(ResultSet rs, int i) throws SQLException {
      PromActivity target = new PromActivity();
      Utils.mapRow(rs, target);
      target.setTenant(rs.getString(TENANT));
      target.setOrgId(rs.getString(ORG_ID));
      target.setBillNumber(rs.getString(BILL_NUMBER));
      target.setName(rs.getString(NAME));
      target.setOnlyMember(rs.getBoolean(ONLY_MEMBER));
      target.setPromChannels(CollectionUtil.toList(rs.getString(PROM_CHANNELS)));
      target.setMaterielAmount(rs.getBigDecimal(MATERIEL_AMOUNT));
      target.setState(StringUtil.toEnum(rs.getString(STATE), PromActivity.State.class));
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
      if (target.getState().equals(PromActivity.State.audited)) {
        if (dateRange.getBeginDate().compareTo(date) <= 0 && dateRange.getEndDate().compareTo(date) >= 0) {
          target.setFrontState(PromActivity.FRONT_STATE_EFFECT);
        } else if (dateRange.getEndDate().compareTo(date) < 0) {
          target.setFrontState(PromActivity.FRONT_STATE_EXPIRED);
        }
      }
      if (fetchParts != null && fetchParts.contains(PromActivity.PARTS_PROMOTION)) {
        target.setPromotions(JsonUtil.jsonToArrayList(rs.getString(PROMOTIONS), Promotion.class));
      }
      target.setPromNote(rs.getString(PROM_NOTE));
      target.setMarketReason(rs.getString(MARKET_REASON));
      target.setPromotionTargets(JsonUtil.jsonToArrayList(rs.getString(PROMOTION_TARGETS), PromotionTarget.class));
      return target;
    }
  }

}
