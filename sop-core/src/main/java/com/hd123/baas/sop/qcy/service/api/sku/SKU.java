package com.hd123.baas.sop.qcy.service.api.sku;

import com.hd123.baas.sop.service.api.basedata.base.Description;
import com.hd123.baas.sop.service.api.basedata.base.GroupTag;
import com.hd123.baas.sop.service.api.basedata.base.InputCode;
import com.hd123.baas.sop.service.api.basedata.base.Parameter;
import com.hd123.baas.sop.service.api.basedata.base.ProductAttribute;
import com.hd123.baas.sop.service.api.basedata.base.Tag;
import com.hd123.baas.sop.service.api.basedata.brand.Brand;
import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.basedata.collocation.CollocationGroup;
import com.hd123.baas.sop.service.api.basedata.platformcategory.PlatformCategory;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author huangyaoting
 */
@Getter
@Setter
@ApiModel("SKU")
public class SKU extends Entity {
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
   * 级联商品分组标签
   */
  public static final String PART_GROUP_TAGS = "groupTags";
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
  public static final String PART_PARAMETER = "parameters";
  /**
   * 级联输入码
   */
  public static final String PART_INPUT_CODES = "inputCodes";

  /**
   * 口味组
   */
  public static final String PART_TASTE_GROUP = "tasteGroups";
  /**
   * 加料组
   */
  public static final String PART_COLLOCATION_GROUP = "collocationGroups";

  /**
   * 默认组织类型
   */
  public static final String ORG_TYPE = "-";
  /**
   * 默认组织id
   */
  public static final String ORG_ID = "-";

  @ApiModelProperty(value = "创建信息")
  private OperateInfo createInfo;
  @ApiModelProperty(value = "最后修改信息")
  private OperateInfo lastModifyInfo;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "ID")
  private String id;
  @ApiModelProperty(value = "goodsId")
  private String goodsId;
  @ApiModelProperty(value = "spuId")
  private String spuId;
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
  @ApiModelProperty(value = "包装规格浮动空间")
  private BigDecimal specFloatingSpace;
  @ApiModelProperty(value = "规格")
  private BigDecimal qpc;
  @ApiModelProperty(value = "规格说明")
  private String qpcStr;
  @ApiModelProperty(value = "商品统一代码")
  private String upc;
  @ApiModelProperty(value = "单位")
  private String unit;
  @ApiModelProperty(value = "重量")
  private BigDecimal weight;
  @ApiModelProperty("长度")
  private BigDecimal length;
  @ApiModelProperty("宽度")
  private BigDecimal width;
  @ApiModelProperty("高度")
  private BigDecimal height;
  @ApiModelProperty(value = "商品的规格名称")
  private String packingName;

  @ApiModelProperty(value = "参考进价")
  private BigDecimal purchasePrice;
  @ApiModelProperty(value = "参考市场价")
  private BigDecimal marketPrice;

  @ApiModelProperty(value = "主图")
  private String image;
  @ApiModelProperty(value = "副图")
  private List<String> images;
  @ApiModelProperty(value = "主视频")
  private String video;
  @ApiModelProperty(value = "是否合格, 可用于标记主图是否合格")
  private Boolean qualified;

  @ApiModelProperty(value = "品牌")
  private Brand brand;
  @ApiModelProperty(value = "分类")
  private Category category;

  @ApiModelProperty(value = "标签")
  private List<Tag> tags = new ArrayList<Tag>();
  @ApiModelProperty(value = "分组标签")
  private List<GroupTag> groupTags = new ArrayList<>();
  @ApiModelProperty(value = "描述")
  private String description;
  @ApiModelProperty(value = "图文详情")
  private List<Description> descriptions = new ArrayList<Description>();
  @ApiModelProperty(value = "商品特性")
  private List<ProductAttribute> attributes = new ArrayList<ProductAttribute>();
  @ApiModelProperty(value = "自定义属性")
  private List<Parameter> customFields = new ArrayList<Parameter>();

  @ApiModelProperty(value = "首次售卖时间")
  private Date firstSaleTime;
  @ApiModelProperty(value = "开始售卖时间")
  private Date startSellDate;
  @ApiModelProperty(value = "结束售卖时间")
  private Date endSellDate;

  @ApiModelProperty(value = "销售方式, 散装, 标准")
  private String saleType;
  @ApiModelProperty(value = "计价方式, 计数, 计重")
  private String valuationType;
  @ApiModelProperty(value = "经营方式, 经销，联营")
  private String businessType;

  @ApiModelProperty(value = "上下架状态")
  private Boolean enabled = Boolean.TRUE;
  @ApiModelProperty(value = "售卖状态")
  private Boolean saleStatus = Boolean.FALSE;

  @ApiModelProperty(value = "税率")
  private String taxRate;
  @ApiModelProperty(value = "税率分类码")
  private String taxSortCode;

  @ApiModelProperty(value = "输入码")
  private List<InputCode> inputCodes = new ArrayList<InputCode>();

  @ApiModelProperty(value = "平台分类")
  private PlatformCategory platformCategory;
  @ApiModelProperty(value = "会员价")
  private BigDecimal mbrPrice;

  @ApiModelProperty(value = "是否默认规格")
  private Boolean defaultSpecification;
  @ApiModelProperty(value = "是否多规格")
  private Boolean multiSpecification;
  @ApiModelProperty(value = "是否标签打印")
  private Boolean labelPrinting;

  @ApiModelProperty(value = "口味组")
  private List<SkuTasteGroup> tasteGroups = new ArrayList<>();
  @ApiModelProperty(value = "加料组列表")
  private List<CollocationGroup> collocationGroups = new ArrayList<>();
  @ApiModelProperty(value = "食用建议")
  private String foodSuggestion;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
  @ApiModelProperty(value = "商品销售属性")
  private List<ProductProperty> properties = new ArrayList<ProductProperty>();

  @ApiModelProperty(value = "sku类型：normal(普通)/combined(组合)")
  private String comboType;
  @ApiModelProperty(value = "包装费")
  public BigDecimal packingCharges;
}
