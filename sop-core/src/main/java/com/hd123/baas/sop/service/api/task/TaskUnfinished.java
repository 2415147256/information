package com.hd123.baas.sop.service.api.task;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author guyahui
 * @Since
 */
@Setter
@Getter
public class TaskUnfinished implements Serializable {

  private static final long serialVersionUID = 9061033559724171693L;
  private String tenant;
  // 计划ID
  private String plan;
  private String planName;
  private Date planStartTime;
  private Date planEndTime;
  private String planPeriod;
  private String planType;

  @QueryEntity(TaskUnfinished.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = TaskUnfinished.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String PLAN = PREFIX + "plan";
    @QueryField
    public static final String PLAN_NAME = PREFIX + "planName";
    @QueryField
    public static final String OPERATOR_ID = PREFIX + "operatorId";

    @QueryOperation
    public static final String HANDLE_IN = PREFIX + "handler in";
  }

}
