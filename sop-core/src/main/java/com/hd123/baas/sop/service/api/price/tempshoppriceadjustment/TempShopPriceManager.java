package com.hd123.baas.sop.service.api.price.tempshoppriceadjustment;

import java.math.BigDecimal;
import java.util.Date;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.Schemas;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/13.
 * 
 *         门店价格池
 * 
 */
@Getter
@Setter
public class TempShopPriceManager extends TenantEntity {
  private String orgId;
  private String shop;
  private String shopCode;
  private String shopName;

  private String skuId;
  private String skuCode;
  private String skuGid;
  private String skuName;
  private BigDecimal skuQpc;

  // 基础到店价
  private BigDecimal basePrice;
  // 促销信息来源
  private String promotionSource;
  // 实际到店价。如果没有促销价，则存null
  private BigDecimal shopPrice;
  // 当前生效日期。非生效开始时间。当对应的价格标识变化时，表示生效开始时间
  private Date effectiveDate;
  // 生效结束时间。仅对到店促销价生效
  private Date effectiveEndDate; // TODO

  @SchemaMeta
  @MapToEntity(TempShopPriceManager.class)
  public static class Schema extends Schemas.Entity {
    @TableName
    public static final String TABLE_NAME = "temp_shop_price_manager";

    public static final String TABLE_ALIAS = "_temp_shop_price_manager";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    public static final String ORG_ID = "org_id";
    @ColumnName
    public static final String SHOP = "shop";
    @ColumnName
    public static final String SHOP_CODE = "shop_code";
    @ColumnName
    public static final String SHOP_NAME = "shop_name";
    @ColumnName
    public static final String SKU_ID = "sku_id";
    @ColumnName
    public static final String SKU_GID = "sku_gid";
    @ColumnName
    public static final String SKU_CODE = "sku_code";
    @ColumnName
    public static final String SKU_NAME = "sku_name";
    @ColumnName
    public static final String SKU_QPC = "sku_qpc";
    @ColumnName
    public static final String BASE_PRICE = "base_price";
    @ColumnName
    public static final String PROMOTION_SOURCE = "promotion_source";
    @ColumnName
    public static final String SHOP_PRICE = "shop_price";
    @ColumnName
    public static final String EFFECTIVE_DATE = "effective_date";
    @ColumnName
    public static final String EFFECTIVE_END_DATE = "effective_end_date";

  }

  @QueryEntity(TempShopPriceManager.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = TempShopPriceManager.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String SHOP = PREFIX + "shop";
    @QueryField
    public static final String EFFECTIVE_DATE = PREFIX + "effectiveDate";
    @QueryField
    public static final String EFFECTIVE_END_DATE = PREFIX + "effectiveEndDate";
    @QueryField
    public static final String SKU_ID = PREFIX + "skuId";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
  }

}
