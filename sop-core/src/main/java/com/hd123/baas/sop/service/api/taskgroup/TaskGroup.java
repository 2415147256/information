package com.hd123.baas.sop.service.api.taskgroup;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskGroup extends TenantStandardEntity {

  private String name;
  /**
   * 提醒时间
   */
  private String remindTime;
  /**
   * 类型
   */
  private TaskGroupType type;

  /**
   * 描述
   */
  private String description;

  /**
   * 主题代码,系统自动生成主题代码（代码规则：两位月份+两位日期+四位随机数）
   */
  private String code;

  /**
   * 状态，已发布，未发布
   */
  private String state;
  /**
   * 组织id
   */
  private String orgId;

  // 巡检内容列表
  public List<TaskTemplate> templateList;

  @QueryEntity(TaskGroup.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = TaskGroup.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String TYPE = PREFIX + "type";

    @QueryField
    public static final String NAME = PREFIX + "name";

    @QueryField
    public static final String STATE = PREFIX + "state";

    @QueryField
    public static final String CODE = PREFIX + "code";

    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";

    @QueryOperation
    public static final String SHOP_EQUALS = PREFIX + "shop equals";

    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX + "Keyword like";
  }

}
