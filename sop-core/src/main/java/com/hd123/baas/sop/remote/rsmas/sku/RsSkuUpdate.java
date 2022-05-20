/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsSkuUpdate.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月11日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.sku;

import com.hd123.baas.sop.remote.rsmas.RsDescription;
import com.hd123.baas.sop.remote.rsmas.groupTag.RsGroupTag;
import com.hd123.baas.sop.remote.rsmas.RsTag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author hezhenhui
 *
 */

@Setter
@Getter
public class RsSkuUpdate {
  @ApiModelProperty(value = "id")
  private String id;
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
  @ApiModelProperty(value = "包装规格浮动空间")
  private BigDecimal specFloatingSpace;
  @ApiModelProperty(value = "规格")
  private BigDecimal qpc;
  @ApiModelProperty(value = "规格说明")
  private String qpcStr;
  @ApiModelProperty(value = "单位")
  private String unit;
  @ApiModelProperty(value = "国际码")
  private String upc;
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
  private String brandId;
  @ApiModelProperty(value = "分类")
  private String categoryId;

  @ApiModelProperty(value = "标签")
  private List<RsTag> tags;
  @ApiModelProperty(value = "分组标签")
  private List<RsGroupTag> groupTags;
  @ApiModelProperty(value = "描述")
  private String description;
  @ApiModelProperty(value = "图文详情")
  private List<RsDescription> descriptions;
  @ApiModelProperty(value = "商品特性")
  private List<RsSkuProductAttribute> attributes;
  @ApiModelProperty(value = "自定义属性")
  private List<RsSkuParameter> customFields;

  @ApiModelProperty(value = "商品销售属性")
  private List<RsSkuParameter> properties;

  @ApiModelProperty(value = "销售方式：散装, 标准")
  private String saleType;
  @ApiModelProperty(value = "计价方式：计数, 计重")
  private String valuationType;
  @ApiModelProperty(value = "经营方式：经销，联营")
  private String businessType;

  @ApiModelProperty(value = "首次售卖时间")
  private Date firstSaleTime;
  @ApiModelProperty(value = "开始售卖时间")
  private Date startSellDate;
  @ApiModelProperty(value = "结束售卖时间")
  private Date endSellDate;

  @ApiModelProperty(value = "上下架状态")
  private Boolean enabled;
  @ApiModelProperty(value = "已删除")
  private Boolean deleted;
  @ApiModelProperty(value = "是否可售卖")
  private String saleStatus;

  @ApiModelProperty(value = "税率")
  private String taxRate;
  @ApiModelProperty(value = "税率分类码")
  private String taxSortCode;

  @ApiModelProperty(value = "sku类型：normal(普通)/combined(组合)")
  private String comboType;

  @ApiModelProperty(value = "组合明细")
  private List<RsSkuComboCreation> items;

  @ApiModelProperty(value = "输入码")
  private List<RsSkuInputCode> inputCodes;

  @ApiModelProperty(value = "会员价")
  private BigDecimal mbrPrice;

  @ApiModelProperty(value = "是否虚拟商品")
  private Boolean isVirtual = Boolean.FALSE;

  @ApiModelProperty(value = "最小起订量")
  private BigDecimal minOrderQty = BigDecimal.ZERO;

  @ApiModelProperty(value = "是否默认规格")
  private Boolean defaultSpecification;

  @ApiModelProperty(value = "是否多规格")
  private Boolean multiSpecification;

  @ApiModelProperty(value = "食用建议")
  private String foodSuggestion;

  @ApiModelProperty(value = "口味组")
  private List<RsSkuTasteGroupSave> tasteGroups;
  
  @ApiModelProperty(value = "是否标签打印")
  private Boolean labelPrinting;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
}
