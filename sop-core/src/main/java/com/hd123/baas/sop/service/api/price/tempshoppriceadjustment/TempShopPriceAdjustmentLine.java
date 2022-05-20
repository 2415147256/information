package com.hd123.baas.sop.service.api.price.tempshoppriceadjustment;

import java.math.BigDecimal;

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
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class TempShopPriceAdjustmentLine extends TenantEntity {
  private String owner;
  private String skuId;
  private String skuGid;
  private BigDecimal skuQpc;
  private String skuCode;
  private String skuName;
  private BigDecimal baseShopPrice;

  @QueryEntity(TempShopPriceAdjustmentLine.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = TempShopPriceAdjustmentLine.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String OWNER = PREFIX + "owner";
  }

  @SchemaMeta
  @MapToEntity(TempShopPriceAdjustmentLine.class)
  public class Schema extends Schemas.Entity {
    @TableName
    public static final String TABLE_NAME = "temp_shop_price_adjustment_line";

    public static final String TABLE_ALIAS = "_temp_shop_price_adjustment_line";
    @ColumnName
    public static final String TENANT = "tenant";

    @ColumnName
    public static final String OWNER = "owner";
    @ColumnName
    public static final String SKU_ID = "sku_id";
    @ColumnName
    public static final String SKU_GID = "sku_gid";
    @ColumnName
    public static final String SKU_QPC = "sku_qpc";
    @ColumnName
    public static final String SKU_CODE = "sku_code";
    @ColumnName
    public static final String SKU_NAME = "sku_name";
    @ColumnName
    public static final String BASE_SHOP_PRICE = "base_shop_price";

  }
}
