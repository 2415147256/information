package com.hd123.baas.sop.service.api.skutag;

import java.math.BigDecimal;

import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
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
public class SkuTagSummary extends Entity {
  private String tenant;
  private String skuId;
  private String orgId;
  private Integer shopNum;
  // 展示字段
  private BigDecimal skuQpc;
  private String skuName;
  private String skuCode;

  @SchemaMeta
  @MapToEntity(SkuTagSummary.class)
  public static class Schema extends Schemas.Entity {
    @TableName
    public static final String TABLE_NAME = "sku_tag_summary";

    public static final String TABLE_ALIAS = "_sku_tag_summary";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "orgId")
    public static final String ORG_ID = "org_id";

    @ColumnName
    @MapToProperty(value = "uuid")
    public static final String UUID = "uuid";

    @ColumnName
    @MapToProperty(value = "skuId")
    public static final String SKU_ID = "sku_id";
    @ColumnName
    @MapToProperty(value = "shopNum")
    public static final String SHOP_NUM = "shop_num";
    //不在数据表中
    public static final String SKU_QPC = "sku_qpc";
    public static final String SKU_NAME = "sku_name";
    public static final String SKU_CODE = "sku_code";

  }

  @QueryEntity(SkuTagSummary.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = SkuTagSummary.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";

    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";

    @QueryField
    public static final String SHOP_NUM = PREFIX + "shopNum";

    @QueryField
    public static final String SKU_ID = PREFIX + "skuId";


    @QueryOperation
    public static final String SKU_KEYWORD_LIKE = PREFIX + "skuKeyword like";

    @QueryOperation
    public static final String SHOP_NUM_NOT_NULL = PREFIX + "shopNumNotNull";
  }
}
