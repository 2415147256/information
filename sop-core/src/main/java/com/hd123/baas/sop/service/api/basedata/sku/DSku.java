package com.hd123.baas.sop.service.api.basedata.sku;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import com.hd123.baas.sop.remote.rsmas.RsMasQueryFactors;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryFieldPurpose;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lina
 */
@Getter
@Setter
public class DSku extends RsMasEntity {
  private static final long serialVersionUID = -4886047099034843815L;

  @ApiModelProperty(value = "ERP商品GID")
  public String goodsGid;
  @ApiModelProperty(value = "代码")
  public String code;
  @ApiModelProperty(value = "名称")
  public String name;
  @ApiModelProperty(value = "规格")
  public BigDecimal qpc;
  @ApiModelProperty(value = "单位")
  public String unit;
  @ApiModelProperty(value = "单位转化")
  public List<String> bom;
  @ApiModelProperty(value = "原价")
  public BigDecimal price;
  @ApiModelProperty(value = "后台分类ID")
  public String categoryId;
  @ApiModelProperty(value = "已删除")
  private Boolean deleted = false;
  @ApiModelProperty(value = "PLU码，称重商品才有")
  public String plu;
  @ApiModelProperty(value = "是否必选")
  private Boolean required;
  @ApiModelProperty(value = "输入码")
  private String inputCode;
  @ApiModelProperty(value = "H6商品类型")
  private String h6GoodsType;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "箱规描述",required = false)
  private String qpcDesc;
  @ApiModelProperty(value = "商品助记码", example = "sm")
  public String pyCode;
  @ApiModelProperty(value = "是否配货规格，取值 0-否 1-是 2-默认值，值为2时才是默认的配货规格。", example = "2")
  public Integer du;

  @QueryEntity(DSku.class)
  public static abstract class Queries extends RsMasQueryFactors.RsMasEntity {

    private static final String PREFIX = DSku.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String REQUIRED = PREFIX + "required";
    @QueryField
    public static final String GOODS_GID = PREFIX + "goodsGid";
    @QueryField
    public static final String H6_GOODS_TYPE = PREFIX + "h6GoodsType";
    @QueryField
    public static final String ID = PREFIX + "id";
    @QueryField
    public static final String QPC = PREFIX + "qpc";
    @QueryField
    public static final String CODE = PREFIX + "code";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String CATEGORY_ID = PREFIX + "categoryId";
    @QueryField
    public static final String PLU = PREFIX + "plu";
    @QueryField
    public static final String INPUT_CODE = PREFIX + "inputCode";
    @QueryField
    public static final String ORG_TYPE = PREFIX + "orgType";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField(fieldType = Integer.class)
    public static final String SKU_DU = PREFIX + "skuDu";

    @QueryField(purposes = QueryFieldPurpose.order)
    public static final String REQUIRED_ORDER = PREFIX + "required";

    @QueryField(purposes = QueryFieldPurpose.order)
    public static final String CODE_ORDER = PREFIX + "code";
  }

}