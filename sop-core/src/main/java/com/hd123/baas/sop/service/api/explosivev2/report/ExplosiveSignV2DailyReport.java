package com.hd123.baas.sop.service.api.explosivev2.report;

import com.hd123.baas.sop.service.api.TenantEntity;
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

import java.math.BigDecimal;
import java.util.Date;

/**
 * 爆品活动每日报表(ExplosiveSignV2DailyReport)实体类
 *
 * @author liuhaoxin
 * @since 2021-12-07 18:13:10
 */
@Getter
@Setter
public class ExplosiveSignV2DailyReport extends TenantEntity {

  /** 组织ID */
  private String orgId;
  /** 活动ID */
  private String explosiveId;
  /** 活动名称 */
  private String explosiveName;
  /** 门店id */
  private String shopId;
  /** 门店code */
  private String shopCode;
  /** 门店名称 */
  private String shopName;
  /** 门店id */
  private String skuId;
  /** 商品GID */
  private String skuGid;
  /** 商品code */
  private String skuCode;
  /** 商品名称 */
  private String skuName;
  /** 商品规格 */
  private BigDecimal skuQpc;
  /** 商品单位 */
  private String skuUnit;
  /** 订货价 */
  private BigDecimal inPrice;
  /** 限量 */
  private BigDecimal limitQty;
  /** 起订量 */
  private BigDecimal minQty;
  /** 描述 */
  private String remark;
  /** 业务日期 */
  private Date businessDate;
  /** 报名数 */
  private BigDecimal signQty = BigDecimal.ZERO;
  /** 订货数 */
  private BigDecimal orderQty = BigDecimal.ZERO;
  /** 配货数 */
  private BigDecimal shippedQty = BigDecimal.ZERO;
  /** 创建时间 */
  private Date created;
  /** 最后修改时间 */
  private Date lastmodified;

  @QueryEntity(ExplosiveSignV2DailyReport.class)
  public static class Queries extends QueryFactors.Entity {
    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(ExplosiveSignV2DailyReport.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String EXPLOSIVE_ID = PREFIX.nameOf("explosiveId");
    @QueryField
    public static final String SHOP_ID = PREFIX.nameOf("shopId");
    @QueryField
    public static final String BUSINESS_DATE = PREFIX.nameOf("businessDate");
    @QueryField
    public static final String SKU_ID = PREFIX.nameOf("skuId");
    @QueryField
    public static final String SKU_CODE = PREFIX.nameOf("skuCode");

    @QueryOperation
    public static final String SHOP_KEYWORD_LIKE = PREFIX.nameOf("shopKeyword like");
    @QueryOperation
    public static final String BUSINESS_DATE_BTW = PREFIX + "businessDate btw";
  }

  @SchemaMeta
  @MapToEntity(ExplosiveSignV2DailyReport.class)
  public class Schema extends Schemas.Entity {
    @TableName
    public static final String TABLE_NAME = "explosive_sign_v2_daily_report";

    public static final String TABLE_ALIAS = "_explosive_sign_v2_daily_report";

    @ColumnName
    @MapToProperty(value = "tenant")
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "orgId")
    public static final String ORG_ID = "org_id";
    @ColumnName
    @MapToProperty(value = "explosiveId")
    public static final String EXPLOSIVE_ID = "explosive_id";
    @ColumnName
    @MapToProperty(value = "explosiveName")
    public static final String EXPLOSIVE_NAME = "explosive_name";
    @ColumnName
    @MapToProperty(value = "shopId")
    public static final String SHOP_ID = "shop_id";
    @ColumnName
    @MapToProperty(value = "shopCode")
    public static final String SHOP_CODE = "shop_code";
    @ColumnName
    @MapToProperty(value = "shopName")
    public static final String SHOP_NAME = "shop_name";
    @ColumnName
    @MapToProperty(value = "skuId")
    public static final String SKU_ID = "sku_id";
    @ColumnName
    @MapToProperty(value = "skuGid")
    public static final String SKU_GID = "SKU_GID";
    @ColumnName
    @MapToProperty(value = "skuCode")
    public static final String SKU_CODE = "sku_code";
    @ColumnName
    @MapToProperty(value = "skuName")
    public static final String SKU_NAME = "sku_name";
    @ColumnName
    @MapToProperty(value = "skuQpc")
    public static final String SKU_QPC = "sku_qpc";
    @ColumnName
    @MapToProperty(value = "skuUnit")
    public static final String SKU_UNIT = "sku_unit";
    @ColumnName
    @MapToProperty(value = "inPrice")
    public static final String IN_PRICE = "in_price";
    @ColumnName
    @MapToProperty(value = "limitQty")
    public static final String LIMIT_QTY = "limit_qty";
    @ColumnName
    @MapToProperty(value = "minQty")
    public static final String MIN_QTY = "min_qty";
    @ColumnName
    @MapToProperty(value = "remark")
    public static final String REMARK = "remark";
    @ColumnName
    @MapToProperty(value = "businessDate")
    public static final String BUSINESS_DATE = "business_date";
    @ColumnName
    @MapToProperty(value = "signQty")
    public static final String SIGN_QTY = "sign_qty";
    @ColumnName
    @MapToProperty(value = "orderQty")
    public static final String ORDER_QTY = "order_qty";
    @ColumnName
    @MapToProperty(value = "shippedQty")
    public static final String SHIPPED_QTY = "shipped_qty";
    @ColumnName
    @MapToProperty(value = "created")
    public static final String CREATED = "created";
    @ColumnName
    @MapToProperty(value = "lastmodified")
    public static final String LASTMODIFIED = "lastModified";
  }

}
