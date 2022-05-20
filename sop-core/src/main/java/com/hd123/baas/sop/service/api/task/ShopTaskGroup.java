package com.hd123.baas.sop.service.api.task;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Getter
@Setter
public class ShopTaskGroup extends TenantStandardEntity {

  // 组织id
  private String orgId;
  private String shop;
  private String shopCode;
  private String shopName;

  private String taskGroup;
  private String groupName;
  private TaskGroupType type;
  // 状态
  private ShopTaskGroupState state;

  // 提醒时间
  private Date remindTime;
  // 日期
  private Date planTime;
  //完成设备
  private String finishAppid;
  private OperateInfo finishInfo;

  /**
   * 最早完成时间
   */
  public Date earliestFinishTime;

  @QueryEntity(ShopTaskGroup.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = ShopTaskGroup.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String TYPE = PREFIX + "type";

    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";

    @QueryField
    public static final String TASK_GROUP = PREFIX + "taskGroup";

    @QueryField
    public static final String GROUP_NAME = PREFIX + "groupName";

    @QueryField
    public static final String PLAN_TIME = PREFIX + "planTime";

    @QueryField
    public static final String SHOP_NAME = PREFIX + "shopName";
    @QueryField
    public static final String SHOP_CODE = PREFIX + "shopCode";
    @QueryField
    public static final String STATE = PREFIX + "state";

    @QueryOperation
    public static final String SHOP_KEYWORD_LIKE = PREFIX + "shopKeyword like";

  }

}
