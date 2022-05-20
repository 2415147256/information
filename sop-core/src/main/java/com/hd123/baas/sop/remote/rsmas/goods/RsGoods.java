package com.hd123.baas.sop.remote.rsmas.goods;

import com.hd123.baas.sop.remote.rsmas.RsDescription;
import com.hd123.baas.sop.remote.rsmas.RsTag;
import com.hd123.baas.sop.remote.rsmas.brand.RsBrand;
import com.hd123.baas.sop.remote.rsmas.category.RsCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RsGoods implements Serializable {
  private static final long serialVersionUID = 1L;


  /**
   * 级联副图
   */
  public static final String PART_IMAGES = "images";
  /**
   * 级联品牌
   */
  public static final String PART_BRAND = "brand";
  /**
   * 级联分类
   */
  public static final String PART_CATEGORY = "category";
  /**
   * 级联标签
   */
  public static final String PART_TAGS = "tags";
  /**
   * 级联图文详情
   */
  public static final String PART_DESCRIPTIONS = "descriptions";
  /**
   * 级联商品特性
   */
  public static final String PART_ATTRIBUTES = "attributes";
  /**
   * 级联自定义属性
   */
  public static final String PART_CUSTOM_FIELDS = "customFields";
  /**
   * 级联输入码
   */
  public static final String PART_INPUT_CODES = "inputCodes";
  /**
   * 级联规格
   */
  public static final String PART_QPC = "qpc";

  @ApiModelProperty(value = "id")
  private String id;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "类型")
  private String type;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "标题")
  private String title;
  @ApiModelProperty(value = "副标题")
  private String subTitle;

  @ApiModelProperty(value = "包装规格")
  private String spec;
  @ApiModelProperty(value = "重量")
  private BigDecimal weight;
  @ApiModelProperty("长度")
  private BigDecimal length;
  @ApiModelProperty("宽度")
  private BigDecimal width;
  @ApiModelProperty("高度")
  private BigDecimal height;

  @ApiModelProperty(value = "参考进价")
  private BigDecimal purchasePrice;
  @ApiModelProperty(value = "参考市场价")
  private BigDecimal marketPrice;

  @ApiModelProperty(value = "封面图")
  private String image;
  @ApiModelProperty(value = "视频")
  private String video;

  @ApiModelProperty(value = "来源记录")
  private String sourcePlatform;

  @ApiModelProperty(value = "开始售卖时间")
  private Date startSellDate;
  @ApiModelProperty(value = "结束售卖时间")
  private Date endSellDate;
  @ApiModelProperty(value = "销售方式, 散装, 标准")
  private String saleType;
  @ApiModelProperty(value = "计价方式, 计数, 计重")
  private String valuationType;

  @ApiModelProperty(value = "税率")
  private String taxRate;
  @ApiModelProperty(value = "税率分类码")
  private String taxSortCode;

  @ApiModelProperty(value = "品牌")
  private RsBrand brand;
  @ApiModelProperty(value = "分类")
  private RsCategory category;

  @ApiModelProperty(value = "输入码")
  private List<RsInputCode> inputCodes = new ArrayList<RsInputCode>();
  @ApiModelProperty(value = "规格")
  private List<RsGoodsQpc> qpcs = new ArrayList<>();
  @ApiModelProperty(value = "副图")
  private List<String> images;
  @ApiModelProperty(value = "标签")
  private List<RsTag> tags = new ArrayList<RsTag>();
  @ApiModelProperty(value = "详情")
  private List<RsDescription> descriptions = new ArrayList<RsDescription>();
  @ApiModelProperty(value = "商品特性")
  private List<RsProductAttribute> attributes = new ArrayList<RsProductAttribute>();
  @ApiModelProperty(value = "自定义属性")
  private List<RsParameter> customFields = new ArrayList<RsParameter>();

  @ApiModelProperty(value = "商品状态")
  private String state;
  @ApiModelProperty(value = "是否合格, 可用于标记主图是否合格")
  private Boolean qualified;
}
