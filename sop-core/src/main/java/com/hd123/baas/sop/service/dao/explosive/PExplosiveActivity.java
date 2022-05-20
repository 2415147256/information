package com.hd123.baas.sop.service.dao.explosive;

import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivity;
import com.hd123.baas.sop.service.dao.PStandardEntity;
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
import java.util.Map;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Entity
@Table(caption = PExplosiveActivity.TABLE_CAPTION, name = PExplosiveActivity.TABLE_NAME, indexes = {
        @Index(name = "idx_explosive_activity_1", columnNames = { PExplosiveActivity.TENANT, PExplosiveActivity.BILL_NUMBER})
})
public class PExplosiveActivity extends PStandardEntity {
  public static final String TABLE_NAME = "sop_explosive_activity";
  public static final String TABLE_CAPTION = "爆品活动";

  @Column(title = "租户", name = PExplosiveActivity.TENANT, length = 38)
  public static final String TENANT = "tenant";

  @Column(title = "组织ID", name = PExplosiveActivity.ORG_ID, length = 38)
  public static final String ORG_ID = "orgId";

  @Column(title = "活动编号", name = PExplosiveActivity.BILL_NUMBER, length = 32)
  public static final String BILL_NUMBER = "billNumber";

  @Column(title = "名称", name = PExplosiveActivity.NAME, length = 64)
  public static final String NAME = "name";

  @Column(title = "状态", name = PExplosiveActivity.STATE, length = 32)
  public static final String STATE = "state";

  @Column(title = "是否适用与全部门店", name = PExplosiveActivity.JOIN_UNITS_ALL_UNIT, fieldClass = Boolean.class)
  public static final String JOIN_UNITS_ALL_UNIT = "allUnit";

  @Column(title = "会员专享", name = PExplosiveActivity.ONLY_MEMBER, fieldClass = Boolean.class)
  public static final String ONLY_MEMBER = "onlyMember";

  @Column(title = "促销渠道", name = PExplosiveActivity.PROM_CHANNELS, length = 100)
  public static final String PROM_CHANNELS = "promChannels";

  @Column(title = "营销物料费", name = PExplosiveActivity.MATERIEL_AMOUNT, fieldClass = BigDecimal.class)
  public static final String MATERIEL_AMOUNT = "materielAmount";

  @Column(title = "促销开始时间", name = PExplosiveActivity.DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE = "beginDate";

  @Column(title = "促销结束时间", name = PExplosiveActivity.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_END_DATE = "endDate";
  @Lob
  @Column(title = "促销日期", name = PExplosiveActivity.DATE_RANGE_CONDITION)
  public static final String DATE_RANGE_CONDITION = "dateRangeCondition";
  @Lob
  @Column(title = "时段促销", name = PExplosiveActivity.TIME_PERIOD_CONDITION)
  public static final String TIME_PERIOD_CONDITION = "timePeriodCondition";

  @Column(title = "促销说明", name = PExplosiveActivity.PROM_NOTE, length = 255)
  public static final String PROM_NOTE = "promNote";

  @Column(title = "报名开始时间", name = PExplosiveActivity.SIGN_RANGE_BEGIN_DATE, fieldClass = Date.class)
  public static final String SIGN_RANGE_BEGIN_DATE = "signBeginDate";

  @Column(title = "报名结束时间", name = PExplosiveActivity.SIGN_RANGE_END_DATE, fieldClass = Date.class)
  public static final String SIGN_RANGE_END_DATE = "signEndDate";

  @Column(title = "促销原因", name = PExplosiveActivity.MARKET_REASON, length = 64)
  public static final String MARKET_REASON = "market_reason";

  public static final String[] COLUMNS = ArrayUtils.addAll(
          PStandardEntity.COLUMNS, MATERIEL_AMOUNT,
          TENANT,ORG_ID, BILL_NUMBER, NAME, ONLY_MEMBER, PROM_CHANNELS, STATE, JOIN_UNITS_ALL_UNIT,
          DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, DATE_RANGE_CONDITION, TIME_PERIOD_CONDITION,
          PROM_NOTE, SIGN_RANGE_BEGIN_DATE, SIGN_RANGE_END_DATE, MARKET_REASON);

  public static Map<String, Object> toFieldValues(ExplosiveActivity entity) {
    Map<String, Object> fvm = Utils.toFieldValues(entity);
    fvm.put(TENANT, entity.getTenant());
    fvm.put(ORG_ID,entity.getOrgId());
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
    fvm.put(PROM_NOTE, entity.getPromNote());
    if (entity.getSignRange() != null) {
      fvm.put(SIGN_RANGE_BEGIN_DATE, entity.getSignRange().getBeginDate());
      fvm.put(SIGN_RANGE_END_DATE, entity.getSignRange().getEndDate());

    }
    fvm.put(MARKET_REASON, entity.getMarketReason());
    return fvm;
  }

  public static class RowMapper extends com.hd123.rumba.commons.jdbc.entity.PStandardEntity.RowMapper<ExplosiveActivity> {

    @Override
    public ExplosiveActivity mapRow(ResultSet rs, int i) throws SQLException {
      ExplosiveActivity target = new ExplosiveActivity();
      Utils.mapRow(rs, target);

      target.setTenant(rs.getString(TENANT));
      target.setOrgId(rs.getString(ORG_ID));
      target.setBillNumber(rs.getString(BILL_NUMBER));
      target.setName(rs.getString(NAME));
      target.setOnlyMember(rs.getBoolean(ONLY_MEMBER));
      target.setPromNote(rs.getString(PROM_NOTE));
      target.setPromChannels(CollectionUtil.toList(rs.getString(PROM_CHANNELS)));
      target.setMaterielAmount(rs.getBigDecimal(MATERIEL_AMOUNT));
      target.setState(StringUtil.toEnum(rs.getString(STATE), ExplosiveActivity.State.class));
      target.setDateRangeCondition(JsonUtil.jsonToObject(rs.getString(DATE_RANGE_CONDITION), DateRangeCondition.class));
      target.setTimePeriodCondition(JsonUtil.jsonToObject(rs.getString(TIME_PERIOD_CONDITION), TimePeriodCondition.class));

      PromotionJoinUnits promotionJoinUnits = new PromotionJoinUnits();
      promotionJoinUnits.setAllUnit(rs.getBoolean(JOIN_UNITS_ALL_UNIT));
      target.setJoinUnits(promotionJoinUnits);

      if (target.getFrontState() == null) {
        target.setFrontState(target.getState().name());
      }
      DateRange dateRange = target.getDateRangeCondition().getDateRange();
      Date date = new Date();
      if (target.getState().equals(ExplosiveActivity.State.audited)) {
        if (dateRange.getBeginDate().compareTo(date) <= 0 && dateRange.getEndDate().compareTo(date) >= 0) {
          target.setFrontState(ExplosiveActivity.FRONT_STATE_EFFECT);
        } else if (dateRange.getEndDate().compareTo(date) < 0) {
          target.setFrontState(ExplosiveActivity.FRONT_STATE_EXPIRED);
        }
      }
      target.setSignRange(new DateRange(rs.getTimestamp(SIGN_RANGE_BEGIN_DATE),rs.getTimestamp(SIGN_RANGE_END_DATE)));
      target.setMarketReason(rs.getString(MARKET_REASON));
      return target;
    }
  }

}
