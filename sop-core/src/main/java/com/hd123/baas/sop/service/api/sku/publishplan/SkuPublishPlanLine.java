package com.hd123.baas.sop.service.api.sku.publishplan;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.mpa.api.common.ObjectNodeUtil;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.MapToProperty;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.util.converter.Converter;
import com.hd123.rumba.commons.util.converter.ConverterBuilder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


/**
 * 商品上下架方案行(SkuPublishPlanLine)实体类
 *
 * @author liuhaoxin
 * @since 2021-11-24 18:38:31
 */
@Getter
@Setter
public class SkuPublishPlanLine extends TenantEntity {

  public static class Ext {
    // 近5日日均叫货量
    public static final String AVG_ORDER_QTY = "avg_order_qty";
    // 库存总数
    public static final String WRH_TOTAL_QTY = "wrh_total_qty";
    // 可用库存件数
    public static final String WRH_QTY = "wrh_qty";
    // 在途库存件数
    public static final String WRH_SHIPPING_QTY = "wrh_shipping_qty";
  }

  public static Converter<SkuPublishPlanLine, SkuPublishPlanLine> converter = ConverterBuilder
      .newBuilder(SkuPublishPlanLine.class, SkuPublishPlanLine.class)
      .build();

  /** 方案ID */
  private String owner;
  /** 商品ID */
  private String skuId;
  /** 商品代码 */
  private String skuCode;
  /** 商品GID */
  private String skuGid;
  /** 商品名称 */
  private String skuName;
  /** 商品规格 */
  private BigDecimal skuQpc;
  /** 商品单位 */
  private String skuUnit;
  /** 商品产地 */
  private String skuOrigin;
  /** 销售单价-到仓 */
  private BigDecimal priceByWrh = BigDecimal.ZERO;
  /** 销售规格价-到仓 */
  private BigDecimal specPriceByWrh = BigDecimal.ZERO;
  /** 销售单价-到店 */
  private BigDecimal priceByShop = BigDecimal.ZERO;
  /** 销售规格价-到店 */
  private BigDecimal specPriceByShop = BigDecimal.ZERO;
  /** 叫货上限 */
  private BigDecimal limitQty = BigDecimal.ZERO;
  /** 商品助记码 */
  public String pyCode;
  /** 是否配货规格，取值 0-否 1-是 2-默认值，值为2时才是默认的配货规格 */
  public Integer du;
  /** 备注 */
  private String remark;
  /** 额外信息 */
  private ObjectNode ext = ObjectNodeUtil.createObjectNode();
  /** 箱规描述 */
  private String qpcDesc;

  @QueryEntity(SkuPublishPlanLine.class)
  public static class Queries {
    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(SkuPublishPlanLine.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String OWNER = PREFIX.nameOf("owner");
    @QueryField
    public static final String SKU_NAME = PREFIX.nameOf("skuName");
    @QueryField
    public static final String SPEC_PRICE_BY_WRH = PREFIX.nameOf("specPriceByWrh");
    @QueryField
    public static final String SPEC_PRICE_BY_SHOP = PREFIX.nameOf("specPriceByShop");
    @QueryField
    public static final String PRICE_BY_WRH = PREFIX.nameOf("priceByWrh");
    @QueryField
    public static final String PRICE_BY_SHOP = PREFIX.nameOf("priceByShop");
    @QueryField
    public static final String LIMIT_QTY = PREFIX.nameOf("limitQty");
    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX.nameOf("keyword like");
  }

  @SchemaMeta
  @MapToEntity(SkuPublishPlanLine.class)
  public class Schema {
    @TableName
    public static final String TABLE_NAME = "sku_publish_plan_line";

    public static final String TABLE_ALIAS = "_sku_publish_plan_line";

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
    @MapToProperty(value = "skuId")
    public static final String SKU_ID = "sku_id";
    @ColumnName
    @MapToProperty(value = "skuCode")
    public static final String SKU_CODE = "sku_code";
    @ColumnName
    @MapToProperty(value = "skuGid")
    public static final String SKU_GID = "sku_gid";
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
    @MapToProperty(value = "skuOrigin")
    public static final String SKU_ORIGIN = "sku_origin";
    @ColumnName
    @MapToProperty(value = "priceByWrh")
    public static final String PRICE_BY_WRH = "price_by_wrh";
    @ColumnName
    @MapToProperty(value = "specPriceByWrh")
    public static final String SPEC_PRICE_BY_WRH = "spec_price_by_wrh";
    @ColumnName
    @MapToProperty(value = "priceByShop")
    public static final String PRICE_BY_SHOP = "price_by_shop";
    @ColumnName
    @MapToProperty(value = "specPriceByShop")
    public static final String SPEC_PRICE_BY_SHOP = "spec_price_by_shop";
    @ColumnName
    @MapToProperty(value = "limitQty")
    public static final String LIMIT_QTY = "limit_qty";
    @ColumnName
    @MapToProperty(value = "pyCode")
    public static final String SKU_PY_CODE = "sku_py_code";
    @ColumnName
    @MapToProperty(value = "du")
    public static final String SKU_DU = "sku_du";
    @ColumnName
    @MapToProperty(value = "remark")
    public static final String REMARK = "remark";
    @ColumnName
    @MapToProperty(value = "ext")
    public static final String EXT = "ext";
  }

}
