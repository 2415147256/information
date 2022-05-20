package com.hd123.baas.sop.service.api.taskplan;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author guyahui
 * @date 2021/5/6 21:18
 */
@Getter
@Setter
public class TaskPlanLine extends TenantStandardEntity {

  /**
   * 任务计划ID
   */
  public String owner;

  /**
   * 任务计划项item
   */
  public String taskPlanItemId;
  /**
   * 门店ID
   */
  public String shop;
  /**
   * 门店代码
   */
  public String shopCode;
  /**
   * 门店名称
   */
  public String shopName;
  /**
   * 任务组ID
   */
  public String taskGroupId;
  /**
   * 主题code
   */
  public String taskGroupCode;
  /**
   * 任务组名称
   */
  public String taskGroupName;
  /**
   * 执行人ID
   */
  public String assigneeId;
  /**
   * 执行人名称
   */
  public String assignee;
  /**
   * 岗位代码
   */
  public String positionCode;
  /**
   * 岗位名称
   */
  public String positionName;

  @QueryEntity(TaskPlanLine.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = TaskPlanLine.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";

    @QueryField
    public static final String OWNER = PREFIX + "owner";

    @QueryField
    public static final String ASSIGNEE = PREFIX + "assignee";
  }

}
