package com.hd123.baas.sop.service.api.skutag;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import com.hd123.rumba.commons.jdbc.annotation.*;
import com.hd123.rumba.commons.jdbc.entity.Schemas;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class ShopTag extends StandardEntity {
  private String tenant;
  private String orgId;
  private String skuId;
  private String shop;
  private String shopCode;
  private String shopName;
  private String tagId;

  @SchemaMeta
  @MapToEntity(ShopTag.class)
  public static class Schema extends Schemas.StandardEntity {
    @TableName
    public static final String TABLE_NAME = "shop_sku_tag";

    public static final String TABLE_ALIAS = "_shop_sku_tag";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    public static final String ORG_ID = "org_id";

    @ColumnName
    @MapToProperty(value = "skuId")
    public static final String SKU_ID = "sku_id";

    @ColumnName
    @MapToProperty(value = "shop")
    public static final String SHOP = "shop";

    @ColumnName
    @MapToProperty(value = "shopCode")
    public static final String SHOP_CODE = "shop_code";

    @ColumnName
    @MapToProperty(value = "shopName")
    public static final String SHOP_NAME = "shop_name";

    @ColumnName
    @MapToProperty(value = "tagId")
    public static final String TAG_ID = "tag_id";

  }

  @QueryEntity(ShopTag.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = ShopTag.class.getName() + "::";

    @QueryOperation
    public static final String SHOP_KEYWORD_LIKE = PREFIX + "shopKeyword like";
  }
}
