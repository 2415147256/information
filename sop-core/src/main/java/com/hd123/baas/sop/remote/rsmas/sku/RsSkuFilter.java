/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	HEADING SOP AS SERVICE
 * 文件名：	RsSkuFilter.java
 * 模块说明：
 * 修改历史：
 * <p>
 * 2021年1月5日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.sku;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel("SKU查询条件")
public class RsSkuFilter extends RsMasFilter {
  private static final long serialVersionUID = -3831439359119329114L;

  @ApiModelProperty(value = "orgId等于")
  private String orgIdEq;
  @ApiModelProperty(value = "orgType等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "spuId等于")
  private String spuIdEq;
  @ApiModelProperty(value = "code等于")
  private String codeEq;
  @ApiModelProperty(value = "code在...范围内")
  private List<String> codeIn;
  @ApiModelProperty(value = "code不在...范围内")
  private List<String> codeNotIn;
  @ApiModelProperty(value = "代码类似于")
  private String codeLike;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "关键字等于，sku的name/sku的Title/ID/编码/条码/GoodID 的模糊查询")
  private String keyword;
  @ApiModelProperty(value = "sku的name/sku的Title的模糊查询")
  private String nameOrTitleLike;
  @ApiModelProperty(value = "sku类型等于")
  private String typeEq;
  @ApiModelProperty(value = "sku类型...范围内")
  private List<String> typeIn;
  @ApiModelProperty(value = "sku销售方式等于")
  private String saleTypeEq;
  @ApiModelProperty(value = "计价方式等于")
  private String valuationTypeEq;
  @ApiModelProperty(value = "sku售卖状态")
  private String saleStatusEq;
  @ApiModelProperty(value = "sku售卖状态在...之中")
  private List<String> saleStatusIn;
  @ApiModelProperty(value = "经营方式等于")
  private String businessTypeEq;
  @ApiModelProperty(value = "sku类型等于，普通/组合")
  private String comboTypeEq;
  @ApiModelProperty(value = "品牌名称类似于")
  private String brandNameLike;
  @ApiModelProperty(value = "合格标记等于(合格、不合格)...")
  private Boolean qualifiedEq;
  @ApiModelProperty(value = "主图是否为空")
  private Boolean image;
  @ApiModelProperty(value = "售卖时间起始于")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date firstSaleGt;
  @ApiModelProperty(value = "售卖时间截止于")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date firstSaleLt;
  @ApiModelProperty(value = "goodsId类似于")
  private String goodsIdLike;
  @ApiModelProperty(value = "Id类似于")
  private String idLike;
  @ApiModelProperty(value = "goodsId在...范围内")
  private List<String> goodsIdIn;
  @ApiModelProperty(value = "子商品SKUID类似于")
  private String comboSkuIdLike;
  @ApiModelProperty(value = "子商品包含")
  private List<String> comboSkuIdIn;
  @ApiModelProperty(value = "子商品编码包含")
  private List<String> skuCodeOrComboSkuCodeIn;
  @ApiModelProperty(value = "分类ID等于")
  private String categoryIdEq;
  @ApiModelProperty(value = "分类ID在...范围内")
  private List<String> categoryIdIn;
  @ApiModelProperty(value = "ID不在在...范围内")
  private List<String> idNotIn;
  @ApiModelProperty(value = "输入码类型等于")
  private String inputCodeTypeEq;
  @ApiModelProperty(value = "商品规格等于")
  private BigDecimal inputCodeQpcEq;
  @ApiModelProperty(value = "商品规格不等于")
  private BigDecimal inputCodeQpcNotEq;
  @ApiModelProperty(value = "商品条码等于")
  private String inputCodeCodeEq;
  @ApiModelProperty(value = "商品条码起始于")
  private String inputCodeCodeStartWith;
  @ApiModelProperty(value = "商品条码like")
  private String inputCodeCodeLikes;
  @ApiModelProperty(value = "商品条码在...范围内")
  private List<String> inputCodeCodeIn;
  @ApiModelProperty(value = "国际码等于")
  private String upcEq;
  @ApiModelProperty(value = "国际码在...范围内")
  private List<String> upcIn;
  @ApiModelProperty(value = "商品来源等于")
  private String sourcePlatformEq;
  @ApiModelProperty(value = "是否已关联spu")
  private Boolean containedBySpu;
  @ApiModelProperty(value = "编码/条码")
  private String codeOrInputCodeLike;
  @ApiModelProperty(value = "自营类型等于")
  private String proprietaryTypeEq;
  @ApiModelProperty(value = "自营类型不等于")
  private String proprietaryTypeNotEq;
  @ApiModelProperty(value = "口味组等于")
  private String tasteGroupIdEq;
  @ApiModelProperty(value = "加料组id等于")
  private String collocationGroupIdEq;

  @ApiModelProperty(value = "是否虚拟商品等于")
  private Boolean isVirtualEq;
  @ApiModelProperty(value = "是否多规格等于")
  private Boolean multiSpecificationEq;
  @ApiModelProperty("加料组名称类似于")
  private String tasteGroupNameLike;
  @ApiModelProperty("加料组名称类似于")
  private String collocationGroupNameLike;
}
