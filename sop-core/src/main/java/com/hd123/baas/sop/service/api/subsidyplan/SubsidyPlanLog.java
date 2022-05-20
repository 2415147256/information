package com.hd123.baas.sop.service.api.subsidyplan;

import java.util.Date;

import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.MapToProperty;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.Schemas;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Setter
@Getter
public class SubsidyPlanLog extends Entity {
  /** 租户 */
  private String tenant;
  /** 计划id */
  private String owner;
  /** 调整的动作 */
  private String action;
  /** 调整的内容 */
  private String content;
  /** 创建时间 */
  private Date created;
  /** 创建人命名空间 */
  private String creatorNS;
  /** 创建人id */
  private String creatorId;
  /** 创建人姓名 */
  private String creatorName;

  @QueryEntity(SubsidyPlanLog.class)
  public static class Queries extends QueryFactors.Entity {

    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(SubsidyPlanLog.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String OWNER = PREFIX.nameOf("owner");
    @QueryField
    public static final String CREATED = PREFIX.nameOf("created");
  }

  @SchemaMeta
  @MapToEntity(SubsidyPlanLog.class)
  public class SubsidyPlanLogSchema extends Schemas.Entity {

    @TableName
    public static final String TABLE_NAME = "subsidy_plan_log";

    public static final String TABLE_ALIAS = "_subsidy_plan_log";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    public static final String OWNER = "owner";
    @ColumnName
    public static final String ACTION = "action";
    @ColumnName
    public static final String CONTENT = "content";
    @ColumnName
    @MapToProperty(value = "created")
    public static final String CREATED = "created";
    @ColumnName
    @MapToProperty(value = "creatorNs")
    public static final String CREATORNS = "creatorNs";
    @ColumnName
    @MapToProperty(value = "creatorId")
    public static final String CREATORID = "creatorId";
    @ColumnName
    @MapToProperty(value = "creatorName")
    public static final String CREATORNAME = "creatorName";
  }
}
