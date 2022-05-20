package com.hd123.baas.sop.service.api.todo;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoScene extends StandardEntity {

  /**
   * 租户
   */
  private String tenant;

  /**
   * 组织id
   */
  private String orgId;

  /**
   * 场景值
   */
  private String code;
  /**
   * 场景名称
   */
  private String name;
  /**
   * 场景类型
   */
  private String type;
  /**
   * 场景触发方
   */
  private SourceEnum source;
  /**
   * 场景携带参数
   */
  private String sourceExt;
  /**
   * 接收者类型（门店/账号）
   */
  private TargetTypeEnum target;
  /**
   * 接收者id/参数
   */
  private String targetExt;
  /**
   * 结束条件
   */
  private String finishCondition;
  /**
   * 是否被引用
   */
  private Boolean isUsed;
  /**
   * 跳转路径url
   */
  private String urlExt;

  @QueryEntity(TodoScene.class)
  public static class Queries extends QueryFactors.Entity {
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(TodoScene.class);
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX.nameOf("keyword like");
    @QueryField
    public static final String SOURCE = PREFIX.nameOf("source");
    @QueryField
    public static final String TYPE = PREFIX.nameOf("type");
    @QueryField
    public static final String CREATED = PREFIX + "createInfo.time";
//    @QueryField
//    public static final String CREATED = PREFIX.nameOf("created");
  }

}
