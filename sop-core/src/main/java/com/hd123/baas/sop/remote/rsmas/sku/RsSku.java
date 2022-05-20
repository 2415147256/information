package com.hd123.baas.sop.remote.rsmas.sku;

import com.hd123.baas.sop.remote.rsmas.groupTag.RsGroupTag;
import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import com.hd123.baas.sop.remote.rsmas.RsTag;
import com.hd123.baas.sop.remote.rsmas.brand.RsBrand;
import com.hd123.baas.sop.remote.rsmas.category.RsCategory;
import com.hd123.baas.sop.remote.rsmas.goods.RsInputCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RsSku extends RsMasEntity {
  private static final long serialVersionUID = -1909006380691607440L;

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "goodsId")
  private String goodsId;
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
  @ApiModelProperty(value = "规格")
  private BigDecimal qpc;
  @ApiModelProperty(value = "规格说明")
  private String qpcStr;
  @ApiModelProperty(value = "单位")
  private String unit;
  @ApiModelProperty(value = "重量")
  private BigDecimal weight;
  @ApiModelProperty(value = "商品的规格名称")
  private String packingName;
  @ApiModelProperty(value = "商品统一代码")
  private String upc;

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
  private RsBrand brand;
  @ApiModelProperty(value = "分类")
  private RsCategory category;
  @ApiModelProperty(value = "标签")
  private List<RsTag> tags = new ArrayList<RsTag>();
  @ApiModelProperty(value = "分组标签")
  private List<RsGroupTag> groupTags = new ArrayList<RsGroupTag>();
  @ApiModelProperty(value = "口味组")
  private List<RsSkuTasteGroup> tasteGroups = new ArrayList<RsSkuTasteGroup>();
  @ApiModelProperty(value = "搭配组")
  private List<RsSkuCollocationGroup> collocationGroups = new ArrayList<>();

  @ApiModelProperty(value = "商品销售属性")
  private List<RsProductProperty> properties = new ArrayList<RsProductProperty>();
  @ApiModelProperty(value = "自定义属性")
  private List<RsSkuParameter> customFields = new ArrayList<>();

  @ApiModelProperty(value = "销售方式, 散装, 标准")
  private String saleType;
  @ApiModelProperty(value = "计价方式, 计数, 计重")
  private String valuationType;
  @ApiModelProperty(value = "经营方式, 经销，联营")
  private String businessType;

  @ApiModelProperty(value = "上下架状态")
  private Boolean enabled = Boolean.TRUE;
  @ApiModelProperty(value = "是否可售卖")
  private Boolean saleStatus = Boolean.FALSE;

  @ApiModelProperty(value = "sku类型，普通/组合")
  private String comboType;

  @ApiModelProperty(value = "组合商品类型，同品/异品")
  private Boolean isDiffCombo;

  @ApiModelProperty(value = "输入码")
  private List<RsInputCode> inputCodes = new ArrayList<RsInputCode>();

  @ApiModelProperty(value = "会员价")
  private BigDecimal mbrPrice;

  @ApiModelProperty(value = "是否默认规格")
  private Boolean defaultSpecification;

  @ApiModelProperty(value = "是否多规格")
  private Boolean multiSpecification;

  @ApiModelProperty(value = "食用建议")
  private String foodSuggestion;

  @ApiModelProperty(value = "是否标签打印")
  private Boolean labelPrinting;

  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;

}
