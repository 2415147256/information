package com.hd123.baas.sop.service.api.task;

import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class PlanSummary extends TenantEntity {
  private String plan;
  private String code;
  private String name;
  private Date startTime;
  private Date endTime;
  private String periodCode;
  private String period;

  private List<ShopTaskSummary> planTasks;

  @Getter
  @Setter
  public static class PPlanSummary extends PEntity {

    public static final String TENANT = "tenant";

    public static final String PLAN = "plan";

    public static final String CODE = "plan_code";

    public static final String NAME = "plan_name";

    public static final String START_TIME = "plan_start_time";

    public static final String END_TIME = "plan_end_time";

    public static final String PERIOD = "plan_period";

    public static final String PERIOD_CODE = "plan_period_code";

    public static String[] allColumns() {
      return toColumnArray(PEntity.allColumns(), TENANT, CODE, NAME, START_TIME, END_TIME, PERIOD, PERIOD_CODE,PLAN);
    }
  }

}
