package com.hd123.baas.sop.service.api.screen;

import java.util.Date;
import java.util.List;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.MapToProperty;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.Schemas;

import lombok.Getter;
import lombok.Setter;

/**
 * (PriceScreen)实体类
 *
 * @author makejava
 * @since 2021-08-09 11:39:25
 */
@Getter
@Setter
public class PriceScreen extends StandardEntity {
  /** 内容方案名称 */
  private String contentName;
  /** 租户 */
  private String tenant;
  /** 组织ID */
  private String orgId;

  /** 生效开始时间 */
  private Date effectiveStartTime;
  /** 生效结束时间 */
  private Date effectiveEndTime;

  /** 是否全部门店 */
  private Boolean allShops;
  /** 内容信息json */
  private String content;
  /** 状态:INITIAL初始化 PUBLISHED已发布 TERMINATED 已终止 EXPIRED已过期 */
  private PriceScreenState state = PriceScreenState.CONFIRMED;

  /** 价格屏关联门店 */
  List<PriceScreenShop> shops;

  /** 结束状态 */
  private Integer deleted = 0;

  @QueryEntity(PriceScreen.class)
  public static class Queries extends QueryFactors.StandardEntity {
    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(PriceScreen.class);

    @QueryField
    public static final String UUID = PREFIX.nameOf("uuid");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String CONTENT_NAME = PREFIX.nameOf("contentName");
    @QueryField
    public static final String EFFECTIVE_START_TIME = PREFIX.nameOf("effectiveStartTime");
    @QueryField
    public static final String EFFECTIVE_END_TIME = PREFIX.nameOf("effectiveEndTime");
    @QueryField
    public static final String STATE = PREFIX.nameOf("state");
    @QueryField
    public static final String DELETED = PREFIX.nameOf("deleted");

    @QueryOperation
    public static final String SHOP_KEYWORD_LIKE = PREFIX.nameOf("shopKeyword like");
  }

  @SchemaMeta
  @MapToEntity(PriceScreen.class)
  public class PriceScreenSchema extends Schemas.StandardEntity {
    @TableName
    public static final String TABLE_NAME = "price_screen";

    public static final String TABLE_ALIAS = "_price_screen";


    @ColumnName
    @MapToProperty(value = "contentName")
    public static final String CONTENT_NAME = "content_name";
    @ColumnName
    @MapToProperty(value = "tenant")
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "orgId")
    public static final String ORG_ID = "org_id";
    @ColumnName
    @MapToProperty(value = "effectiveStartTime")
    public static final String EFFECTIVE_START_TIME = "effective_start_time";
    @ColumnName
    @MapToProperty(value = "effectiveEndTime")
    public static final String EFFECTIVE_END_TIME = "effective_end_time";
    @ColumnName
    @MapToProperty(value = "state")
    public static final String STATE = "state";
    @ColumnName
    @MapToProperty(value = "deleted")
    public static final String DELETED = "deleted";
    @ColumnName
    @MapToProperty(value = "allShops")
    public static final String ALL_SHOPS = "all_shops";
    @ColumnName
    @MapToProperty(value = "content")
    public static final String CONTENT = "content";
  }

}
