package com.hd123.baas.sop.service.api.screen;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.MapToProperty;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

/**
 * (PriceScreenShop)实体类
 *
 * @author makejava
 * @since 2021-08-09 18:25:57
 */
@Getter
@Setter
public class PriceScreenShop {
  /** 唯一标识 */
  private String uuid;
  /** 租户 */
  private String tenant;
  /** 价格屏id */
  private String owner;
  /** 门店id */
  private String shop;
  /** 门店code */
  private String shopCode;
  /** 门店名称 */
  private String shopName;

  @QueryEntity(PriceScreenShop.class)
  public static class Queries {
    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(PriceScreenShop.class);

    @QueryField
    public static final String UUID = PREFIX.nameOf("uuid");
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
  }

  @SchemaMeta
  @MapToEntity(PriceScreenShop.class)
  public class PriceScreenShopSchema {
    @TableName
    public static final String TABLE_NAME = "price_screen_shop";

    public static final String TABLE_ALIAS = "_price_screen_shop";

    @ColumnName
    @MapToProperty(value = "uuid")
    public static final String UUID = "uuid";
    @ColumnName
    @MapToProperty(value = "tenant")
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "owner")
    public static final String OWNER = "owner";
    @ColumnName
    @MapToProperty(value = "shop")
    public static final String SHOP = "shop";
    @ColumnName
    @MapToProperty(value = "shopCode")
    public static final String SHOP_CODE = "shop_code";
    @ColumnName
    @MapToProperty(value = "shopName")
    public static final String SHOP_NAME = "shop_name";
  }

}
