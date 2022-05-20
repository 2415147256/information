package com.hd123.baas.sop.service.api.taskplan;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Getter
@Setter
public class TaskPlan extends TenantStandardEntity {
  /**
   * 组织id
   */
  private String orgId;
  /**
   * 任务计划名称
   */
  public String name;
  /**
   * 提醒时间 (表达式)
   */
  public String remindTime;
  /**
   * 任务计划描述
   */
  public String description;
  /**
   * 任务组id
   */
  public String taskGroup;
  /**
   * 是否需要文字反馈
   */
  public boolean wordNeeded;
  /**
   * 是否需要图片反馈
   */
  public boolean imageNeeded;
  /**
   * 固定任务模板名称
   */
  public String templateCls;
  /**
   * 执行时间（表达式）
   */
  public String planTime;

  /**
   * 任务计划状态（生效，未生效）
   */
  public TaskPlanState state;

  /**
   * 任务计划开始时间
   */
  public Date startDate;

  /**
   * 任务计划结束时间
   */
  public Date endDate;

  /**
   * 排序
   */
  public int sort;

  /**
   * 周期类型
   */
  public String cycle;

  /**
   * 计划有效期天数
   */
  private Integer validityDays;

  /**
   * 按周重复的具体星期几,如周一为1，周二为2...周日为7
   */
  private Integer dayOfWeek;

  /**
   * 按月重复的具体的日期，填写1~28
   */
  private Integer dayOfMonth;

  /**
   * 超时提醒时间，即计划开始后的第几天，计划周期为按周、按月时需要该值
   */
  private Integer delayDay;

  /**
   * 发布时间
   */
  private String publishDate;

  /**
   * taskGroups, 冗余taskGroup uuid和name 减少数据库查询次数
   */
  private String taskGroups;

  /**
   * 冗余shop对象
   */
  private String shops;

  /**
   * 计划代码
   */
  private String code;

  /**
   * 巡检明细生成方式
   */
  private String generateMode;
  /**
   * 门店设置
   */
  private String shopMode;

  /**
   * 超时提醒时间,任务计划为单次周期时使用
   */
  private Date remindDate;

  /**
   * 计划类型
   */
  private String type;

  /**
   * 任务发布时间，为了兼容之前的任务
   */
  private Date publishTaskDate;

  /**
   * 任务发布时间集合
   */
  private String publishTaskDateCollect;

  /**
   * 具体超时提醒时间，计划周期为按周、按月时需要该值,格式 HH:mm
   */
  private String remindDetailTime;

  /**
   * 巡检明细
   */
  private List<TaskPlanLine> lines;

  /**
   * 普通任务新增指派类型
   */
  private String assignType;

  /**
   * 任务计划来源
   */
  private String source;

  /**
   * 普通任务新增任务明细，task_plan_item
   */
  private List<TaskPlanItem> items;

  /**
   * 创建人shop
   */
  private String creatorShop;
  /**
   * 创建人门店代码
   */
  private String creatorShopCode;
  /**
   * 创建人门店名称
   */
  private String creatorShopName;

  /**
   * 提前下发时间，任务开始前的天数
   */
  private int advancePubDay = 0;
  /**
   * 提前下发时间，任务开始前的小时
   */
  private int advancePubHour = 0;
  /**
   * 临期提醒时间，任务结束前的天数
   */
  private int advanceEndDay = 0;
  /**
   * 临期提醒时间，任务结束前的小时
   */
  private int advanceEndHour = 0;

  @QueryEntity(TaskPlan.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = TaskPlan.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";

    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";

    @QueryField
    public static final String NAME = PREFIX + "name";

    @QueryField
    public static final String STATE = PREFIX + "state";

    @QueryField
    public static final String CODE = PREFIX + "code";

    @QueryField
    public static final String CYCLE = PREFIX + "cycle";

    @QueryField
    public static final String GENERATE_MODE = PREFIX + "generateMode";

    @QueryField
    public static final String SHOP_MODE = PREFIX + "shopMode";

    @QueryField
    public static final String TASK_GROUP = PREFIX + "taskGroup";

    @QueryField
    public static final String SORT = PREFIX + "sort";

    @QueryField
    public static final String TYPE = PREFIX + "type";

    @QueryField
    public static final String START_DATE = PREFIX + "startDate";

    @QueryField
    public static final String END_DATE = PREFIX + "endDate";

    @QueryField
    public static final String ASSIGN_TYPE = PREFIX + "assignType";

    @QueryOperation
    public static final String TYPE_EQUALS = PREFIX + "type equals";

    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX + "keyword like";

    @QueryOperation
    public static final String EFFECT_TIME_BTW = PREFIX + "effectTime btw";

    @QueryOperation
    public static final String SHOP_KEYWORD_LIKE = PREFIX + "shopKeyword like";
  }

}
