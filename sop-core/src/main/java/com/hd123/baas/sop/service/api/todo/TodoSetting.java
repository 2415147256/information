package com.hd123.baas.sop.service.api.todo;

import com.hd123.rumba.commons.biz.query.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TodoSetting extends TodoScene {

  /**
   * 设置id
   */
  private String id;
  /**
   * 设置名称
   */
  private String settingName;
  /**
   * 状态（下架/上架）
   */
  private ShelfStateEnum state;
  /**
   * 描述
   */
  private String description;
  /**
   * 紧急程度（0：紧急，1：重要，2次要）
   */
  private Integer level;
  /**
   * 开始处理日期
   */
  private Date start;
  /**
   * 结束处理日期
   */
  private Date end;
  /**
   * 按钮文案
   */
  private String ext;
  /**
   * 关联场景id
   */
  private String sceneId;
  /**
   * 标签
   */
  private String tag;
  /**
   * 待办种类
   */
  private CategoryEnum category;
  /**
   * 岗位类型
   */
  private String positionType;

  @QueryEntity(TodoSetting.class)
  public static class Queries extends QueryFactors.Entity {
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(TodoSetting.class);
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String SCENE_ID = PREFIX.nameOf("sceneId");
    @QueryOperation
    public static final String SCENE_NAME_LIKE = PREFIX.nameOf("sceneName like");
    @QueryOperation
    public static final String SOURCE_NAME_EQUALS = PREFIX.nameOf("sourceName equals");
    @QueryField
    public static final String TYPE = PREFIX.nameOf("type");
    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX.nameOf("keyword like");
    @QueryField
    public static final String STATE = PREFIX.nameOf("state");
    @QueryField
    public static final String CREATED = PREFIX + "createInfo.time";
    @QueryField
    public static final String UUID = PREFIX + "uuid";

  }
}
