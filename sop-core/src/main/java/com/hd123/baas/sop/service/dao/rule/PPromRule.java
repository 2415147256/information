package com.hd123.baas.sop.service.dao.rule;

import com.hd123.baas.sop.service.api.pms.activity.PromActivityType;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
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
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.commons.json.JsonUtil;
import com.hd123.spms.commons.util.CollectionUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Fanluhao
 * @since 1.0
 */
@Entity
@Table(caption = PPromRule.TABLE_CAPTION, name = PPromRule.TABLE_NAME, indexes = {
        @Index(name = "idx_prom_rule_1", columnNames = { PPromRule.TENANT, PPromRule.BILL_NUMBER})
})
public class PPromRule extends PStandardEntity {
  public static final String TABLE_NAME = "sop_prom_rule";
  public static final String TABLE_CAPTION = "促销规则";

  @Column(title = "租户", name = PPromRule.TENANT, length = 40)
  public static final String TENANT = "tenant";
  @Column(title = "组织ID", name = PPromRule.ORG_ID, length = 40)
  public static final String ORG_ID = "orgId";
  @Column(title = "代码", name = PPromRule.BILL_NUMBER, length = 64)
  public static final String BILL_NUMBER = "billNumber";
  @Column(title = "名称", name = PPromRule.NAME, length = 80)
  public static final String NAME = "name";

  @Column(title = "发起组织id", name = PPromRule.STARTER_ORG_UUID, length = 38)
  public static final String STARTER_ORG_UUID = "starterOrgUuid";

  @Column(title = "促销模板uuid", name = PPromRule.TEMPLATE_UUID, length = 38)
  public static final String TEMPLATE_UUID = "templateUuid";
  @Column(title = "促销模板code", name = PPromRule.TEMPLATE_CODE, length = 64)
  public static final String TEMPLATE_CODE = "templateCode";
  @Column(title = "促销模板name", name = PPromRule.TEMPLATE_NAME, length = 80)
  public static final String TEMPLATE_NAME = "templateName";

  @Column(title = "所属活动uuid", name = PPromRule.ACTIVITY_UUID, length = 38)
  public static final String ACTIVITY_UUID = "activityUuid";
  @Column(title = "所属活动代码", name = PPromRule.ACTIVITY_CODE, length = 64)
  public static final String ACTIVITY_CODE = "activityCode";
  @Column(title = "所属活动名称", name = PPromRule.ACTIVITY_NAME, length = 80)
  public static final String ACTIVITY_NAME = "activityName";
  @Column(title = "所属活动类型", name = PPromRule.ACTIVITY_TYPE, length = 20)
  public static final String ACTIVITY_TYPE = "activityType";
  @Column(title = "状态", name = PPromRule.STATE, length = 20)
  public static final String STATE = "state";
  @Column(title = "全部门店", name = PPromRule.ALL_UNIT, fieldClass = Boolean.class)
  public static final String ALL_UNIT = "allUnit";

  @Column(title = "会员专享", name = PPromRule.ONLY_MEMBER, fieldClass = Boolean.class)
  public static final String ONLY_MEMBER = "onlyMember";
  @Column(title = "促销渠道", name = PPromRule.PROM_CHANNELS, length = 100)
  public static final String PROM_CHANNELS = "promChannels";

  @Column(title = "终止人id", name = PPromRule.STOP_ID, length = 32)
  public static final String STOP_ID = "stopId";
  @Column(title = "终止人名称", name = PPromRule.STOP_NAME, length = 80)
  public static final String STOP_NAME = "stopName";
  @Column(title = "终止人NS", name = PPromRule.STOP_NAMESPACE, length = 64)
  public static final String STOP_NAMESPACE = "stopNS";
  @Column(title = "终止时间", name = PPromRule.STOP_TIME, fieldClass = Date.class)
  public static final String STOP_TIME = "stopTime";

  @Lob
  @Column(title = "促销日期", name = PPromRule.DATE_RANGE_CONDITION, length = 5000)
  public static final String DATE_RANGE_CONDITION = "dateRangeCondition";
  @Lob
  @Column(title = "时段促销", name = PPromRule.TIME_PERIOD_CONDITION, length = 5000)
  public static final String TIME_PERIOD_CONDITION = "timePeriodCondition";
  @Lob
  @Column(title = "促销内容", name = PPromRule.PROMOTION, length = 5000)
  public static final String PROMOTION = "promotion";

  @Column(title = "促销说明", name = PPromRule.PROM_NOTE, length = 255)
  public static final String PROM_NOTE = "promNote";

  @Column(title = "促销类型", name = PPromRule.PROMOTION_PROMOTION_TYPE, length = 20)
  public static final String PROMOTION_PROMOTION_TYPE = "promotionType";
  @Column(title = "促销方式", name = PPromRule.PROMOTION_PROMOTION_MODE, length = 40)
  public static final String PROMOTION_PROMOTION_MODE = "promotionMode";
  @Column(title = "促销描述", name = PPromRule.PROMOTION_DESCRIPTION, length = 255)
  public static final String PROMOTION_DESCRIPTION = "description";

  @Column(title = "开始日期", name = PPromRule.DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE = "beginDate";
  @Column(title = "结束日期", name = PPromRule.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, fieldClass = Date.class)
  public static final String DATE_RANGE_CONDITION_DATE_RANGE_END_DATE = "endDate";

  @Column(title = "字段控制", name = PPromRule.FIELD_CONTROLS, length = 1024)
  public static final String FIELD_CONTROLS = "fieldControls";

  @Column(title = "促销原因", name = PPromRule.MARKET_REASON, length = 64)
  public static final String MARKET_REASON = "market_reason";

  public static final String[] COLUMNS = ArrayUtils.addAll(
          PStandardEntity.COLUMNS, TENANT,ORG_ID, NAME, BILL_NUMBER, STARTER_ORG_UUID, TEMPLATE_UUID, TEMPLATE_CODE,
          TEMPLATE_NAME, ACTIVITY_UUID, ACTIVITY_CODE, ACTIVITY_NAME, ACTIVITY_TYPE,
          ONLY_MEMBER, DATE_RANGE_CONDITION, TIME_PERIOD_CONDITION,
          STOP_ID, STOP_NAME, STOP_NAMESPACE, STOP_TIME, ALL_UNIT, STATE, PROM_CHANNELS,
          // PROMOTION,
          PROMOTION_PROMOTION_TYPE, PROMOTION_PROMOTION_MODE, PROMOTION_DESCRIPTION,
          DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, DATE_RANGE_CONDITION_DATE_RANGE_END_DATE,
          PROM_NOTE, FIELD_CONTROLS, MARKET_REASON);

  public static Map<String, Object> toFieldValues(PromRule entity) {
    Map<String, Object> fvm = Utils.toFieldValues(entity);
    fvm.put(ORG_ID,entity.getOrgId());
    fvm.put(TENANT, entity.getTenant());
    fvm.put(NAME, entity.getName());
    fvm.put(BILL_NUMBER, entity.getBillNumber());
    fvm.put(STATE, entity.getState() == null ? null : entity.getState().toString());
    fvm.put(STARTER_ORG_UUID, entity.getStarterOrgUuid());
    if (entity.getStopInfo() != null) {
      if (entity.getStopInfo().getOperator() != null) {
        fvm.put(STOP_ID, entity.getStopInfo().getOperator().getId());
        fvm.put(STOP_NAME, entity.getStopInfo().getOperator().getFullName());
        fvm.put(STOP_NAMESPACE, entity.getStopInfo().getOperator().getNamespace());
      }
      fvm.put(STOP_TIME, entity.getStopInfo().getTime());
    }

    if (entity.getJoinUnits() != null) {
      fvm.put(ALL_UNIT, entity.getJoinUnits().getAllUnit());
    }

    if (entity.getTemplate() != null) {
      fvm.put(TEMPLATE_UUID, entity.getTemplate().getUuid());
      fvm.put(TEMPLATE_CODE, entity.getTemplate().getCode());
      fvm.put(TEMPLATE_NAME, entity.getTemplate().getName());
    }
    if (entity.getActivity() != null) {
      fvm.put(ACTIVITY_UUID, entity.getActivity().getUuid());
      fvm.put(ACTIVITY_CODE, entity.getActivity().getCode());
      fvm.put(ACTIVITY_NAME, entity.getActivity().getName());
    }
    fvm.put(ACTIVITY_TYPE, entity.getActivityType() != null ? entity.getActivityType().name() : "");
    fvm.put(ONLY_MEMBER, entity.getOnlyMember());
    fvm.put(PROM_CHANNELS, CollectionUtil.toString(entity.getPromChannels()));

    fvm.put(PROMOTION, JsonUtil.objectToJson(entity.getPromotion()));
    if (entity.getPromotion() != null) {
      fvm.put(PROMOTION_PROMOTION_TYPE, entity.getPromotion().getPromotionType() == null ? null : entity.getPromotion().getPromotionType().toString());
      fvm.put(PROMOTION_PROMOTION_MODE, entity.getPromotion().getPromotionMode());
      fvm.put(PROMOTION_DESCRIPTION, entity.getPromotion().getDescription());
    }
    fvm.put(DATE_RANGE_CONDITION, JsonUtil.objectToJson(entity.getDateRangeCondition()));
    if (entity.getDateRangeCondition() != null) {
      if (entity.getDateRangeCondition().getDateRange() != null) {
        fvm.put(DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, entity.getDateRangeCondition().getDateRange().getBeginDate());
        fvm.put(DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, entity.getDateRangeCondition().getDateRange().getEndDate());
      }
    }
    fvm.put(TIME_PERIOD_CONDITION, JsonUtil.objectToJson(entity.getTimePeriodCondition()));

    fvm.put(PROM_NOTE, entity.getPromNote());

    fvm.put(FIELD_CONTROLS, JsonUtil.objectToJson(entity.getFieldControls()));

    fvm.put(MARKET_REASON, entity.getMarketReason());
    return fvm;
  }

  public static class RowMapper extends com.hd123.rumba.commons.jdbc.entity.PStandardEntity.RowMapper<PromRule> {
    private List<String> fetchParts;

    public RowMapper() {
    }

    public RowMapper(List<String> fetchParts) {
      this.fetchParts = fetchParts;
    }

    @Override
    public PromRule mapRow(ResultSet rs, int i) throws SQLException {
      PromRule target = new PromRule();
      Utils.mapRow(rs, target);
      target.setName(rs.getString(NAME));
      target.setBillNumber(rs.getString(BILL_NUMBER));
      target.setTenant(rs.getString(TENANT));
      target.setOrgId(rs.getString(ORG_ID));
      target.setStarterOrgUuid(rs.getString(STARTER_ORG_UUID));
      if (StringUtils.isNoneBlank(rs.getString(STOP_TIME))) {
        target.setStopInfo(new OperateInfo(
                rs.getTimestamp(STOP_TIME),
                new Operator(rs.getString(STOP_NAMESPACE), rs.getString(STOP_ID), rs.getString(STOP_NAME))));
      }
      if (StringUtils.isNoneBlank(rs.getString(TEMPLATE_UUID))) {
        target.setTemplate(new UCN(rs.getString(TEMPLATE_UUID), rs.getString(TEMPLATE_CODE), rs.getString(TEMPLATE_NAME)));
      }
      if (StringUtils.isNoneBlank(rs.getString(ACTIVITY_UUID))) {
        target.setActivity(new UCN(rs.getString(ACTIVITY_UUID), rs.getString(ACTIVITY_CODE), rs.getString(ACTIVITY_NAME)));
      }
      target.setActivityType(StringUtil.toEnum(rs.getString(ACTIVITY_TYPE), PromActivityType.class));
      if (StringUtils.isNoneBlank(rs.getString(ONLY_MEMBER))) {
        target.setOnlyMember(rs.getBoolean(ONLY_MEMBER));
      }
      target.setDateRangeCondition(JsonUtil.jsonToObject(rs.getString(DATE_RANGE_CONDITION), DateRangeCondition.class));
      target.setTimePeriodCondition(JsonUtil.jsonToObject(rs.getString(TIME_PERIOD_CONDITION), TimePeriodCondition.class));
      target.setPromNote(rs.getString(PROM_NOTE));
      target.setPromChannels(CollectionUtil.toList(rs.getString(PROM_CHANNELS)));

      if (fetchParts != null && fetchParts.contains(PromTemplate.PARTS_PROMOTION)) {
        target.setPromotion(JsonUtil.jsonToObject(rs.getString(PROMOTION), Promotion.class));
      } else {
        Promotion promotion = new Promotion();
        promotion.setPromotionType(StringUtil.toEnum(rs.getString(PROMOTION_PROMOTION_TYPE), PromotionType.class));
        promotion.setPromotionMode(rs.getString(PROMOTION_PROMOTION_MODE));
        promotion.setDescription(rs.getString(PROMOTION_DESCRIPTION));
        target.setPromotion(promotion);
      }

      PromotionJoinUnits promotionJoinUnits = new PromotionJoinUnits();
      promotionJoinUnits.setAllUnit(ObjectUtils.defaultIfNull(rs.getBoolean(ALL_UNIT), false));
      target.setJoinUnits(promotionJoinUnits);

      target.setFieldControls(JsonUtil.jsonToObject(rs.getString(FIELD_CONTROLS), Map.class));

      target.setState(EnumUtils.getEnum(PromRule.State.class, rs.getString(STATE)));
      if (target.getState() != PromRule.State.stopped) {
        // 状态不是已终止时计算出前端状态
        if (target.getDateRangeCondition() != null && target.getDateRangeCondition().getDateRange() != null) {
          DateRange dateRange = target.getDateRangeCondition().getDateRange();
          Date now = new Date();
          if (dateRange.getBeginDate().after(now)) {
            target.setFrontState(PromRule.FRONT_STATE_INITIAL);
          } else if (dateRange.getEndDate().before(now)) {
            target.setFrontState(PromRule.FRONT_STATE_EXPIRED);
          } else {
            target.setFrontState(PromRule.FRONT_STATE_EFFECT);
          }
        } else {
          target.setFrontState(PromRule.FRONT_STATE_EXPIRED);
        }
      } else {
        target.setFrontState(PromRule.FRONT_STATE_STOPPED);
      }
      target.setMarketReason(rs.getString(MARKET_REASON));
      return target;
    }
  }

}
