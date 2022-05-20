package com.hd123.baas.sop.service.api.task;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Getter
@Setter
public class ShopTask extends TenantStandardEntity {

  // 门店统计UUID
  private String owner;
  // 组织id
  private String orgId;

  // 任务组id
  private String shopTaskGroup;

  // 执行人门店ID
  private String shop;
  // 执行人门店号
  private String shopCode;
  // 执行人门店名称
  private String shopName;

  // 创建人门店ID
  private String creatorShop;
  // 创建人门店号
  private String creatorShopCode;
  // 创建人门店名称
  private String creatorShopName;

  // 分组id
  private String taskGroup;
  // 分组名称
  private String groupName;

  // 计划id
  private String plan;
  // 计划代码
  private String planCode;
  // 计划名称
  private String planName;
  // 计划类型
  private String planType;
  // 计划周期
  private String planPeriod;
  // 计划周期编号
  private String planPeriodCode;
  // 计划开始时间
  private Date planStartTime;
  // 计划结束时间
  private Date planEndTime;

  // 计划执行时间(日期)
  private Date planTime;
  private OperateInfo finishInfo;
  // 完成设备
  private String finishAppId;
  // 状态
  private ShopTaskState state = ShopTaskState.UNFINISHED;
  // 提醒时间
  private Date remindTime;

  // 任务名称
  private String name;
  private boolean wordNeeded;
  private boolean imageNeeded;
  private boolean videoNeeded;
  private String description;
  private String templateCls;

  // 任务反馈，一般是一个json
  private String feedback;

  // 普通任务-附件列表
  private String attachFiles;
  // 普通任务-任务内容
  private String comment;
  // 普通任务-是否审核
  private Boolean audit;
  // 普通任务-任务类型
  private String assignType;
  // 抢单时间
  private Date grabOrderTime;
  //领取时间
  private Date started;

  /**
   * 排序
   */
  private int sort;

  // 总分
  private BigDecimal point = BigDecimal.ZERO;
  // 得分
  private BigDecimal score = BigDecimal.ZERO;
  // 任务记录
  private List<ShopTaskLog> logs;
  // 执行人信息
  private String operatorId;
  private String operatorName;
  private String operatorPositionCode;
  private String operatorPositionName;

  @QueryEntity(ShopTask.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = ShopTask.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";

    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";

    @QueryField
    public static final String NAME = PREFIX + "name";

    @QueryField
    public static final String TASK_GROUP = PREFIX + "taskGroup";

    @QueryField
    public static final String PLAN_CODE = PREFIX + "planCode";

    @QueryField
    public static final String TEMPLATE_CLS = PREFIX + "templateCls";


    @QueryField
    public static final String PLAN_PERIOD_CODE = PREFIX + "planPeriodCode";

    @QueryField
    public static final String PLAN_TYPE = PREFIX + "planType";

    @QueryField
    public static final String PLAN_TIME = PREFIX + "planTime";

    @QueryField
    public static final String STATE = PREFIX + "state";

    @QueryField
    public static final String SORT = PREFIX + "sort";

    @QueryField
    public static final String SHOP = PREFIX + "shop";
    @QueryField
    public static final String SHOP_NAME = PREFIX + "shopName";
    @QueryField
    public static final String SHOP_CODE = PREFIX + "shopCode";
    @QueryField
    public static final String OPERATOR_ID = PREFIX + "operatorId";
    @QueryField
    public static final String OPERATOR_POSITION_NAME = PREFIX + "operatorPositionName";
    @QueryField
    public static final String OPERATOR_POSITION_CODE = PREFIX + "operatorPositionCode";
    @QueryField
    public static final String ASSIGN_TYPE = PREFIX + "assignType";
    @QueryField
    public static final String AUDIT = PREFIX + "audit";
    @QueryField
    public static final String started = PREFIX + "started";
    @QueryField
    public static final String CREATOR_SHOP = PREFIX + "creatorShop";
    @QueryField
    public static final String CREATOR_SHOP_CODE = PREFIX + "creatorShopCode";
    @QueryField
    public static final String CREATOR_SHOP_NAME = PREFIX + "creatorShopName";
    @QueryOperation
    public static final String SHOP_KEYWORD_LIKE = PREFIX + "shopKeyword like";

    @QueryOperation
    public static final String TYPE_EQUALS = PREFIX + "type equals";

    @QueryOperation
    public static final String PLAN_KEYWORD_LIKE = PREFIX + "planKeyword like";

    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX + "keyword like";

    @QueryOperation
    public static final String EFFECT_TIME_BTW = PREFIX + "effectTime btw";

    @QueryOperation
    public static final String TRANSFERSTATE = PREFIX + "transferstate";

    @QueryOperation
    public static final String OWNERSHIP = PREFIX + "ownership";
  }

}
