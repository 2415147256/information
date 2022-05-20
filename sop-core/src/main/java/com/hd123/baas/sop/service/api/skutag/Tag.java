package com.hd123.baas.sop.service.api.skutag;

import com.hd123.rumba.commons.jdbc.annotation.*;

import com.hd123.rumba.commons.jdbc.entity.Schemas;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class Tag {

  public static final String SOURCE_HD_POS = "HD_POS";
  /**
   * uuid
   */
  private int uuid;
  /**
   * 组织
   */
  private String orgId;
  /**
   * 租户id
   */
  private String tenant;
  /**
   * 名称
   */
  private String name;
  /** 代码 */
  private String code;
  /** 来源 */
  private String source;
  /** 来源ID */
  private String sourceId;
  /** 创建时间 */
  private Date created = new Date();

  @SchemaMeta
  @MapToEntity(Tag.class)
  public class Schema extends Schemas.Entity {
    @TableName
    public static final String TABLE_NAME = "tag";

    public static final String TABLE_ALIAS = "_tag";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty("orgId")
    public static final String ORG_ID = "org_id";

    @ColumnName
    @MapToProperty(value = "uuid")
    public static final String UUID = "uuid";

    @ColumnName
    @MapToProperty(value = "name")
    public static final String NAME = "name";

    @ColumnName
    @MapToProperty(value = "code")
    public static final String CODE = "code";

    @ColumnName
    @MapToProperty(value = "source")
    public static final String SOURCE = "source";

    @ColumnName
    @MapToProperty(value = "sourceId")
    public static final String SOURCE_ID = "source_id";

    @ColumnName
    @MapToProperty(value = "created")
    public static final String CREATED = "created";
  }
}
