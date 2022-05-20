/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	HEADING SOP AS SERVICE 文件名：	RsShopSku.java 模块说明： 修改历史：
 * <p>
 * 2021年1月6日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.shopsku;

import com.hd123.baas.sop.remote.rsmas.RsIdCodeName;
import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import com.hd123.baas.sop.remote.rsmas.RsTag;
import com.hd123.baas.sop.remote.rsmas.collocation.RsCollocationGroup;
import com.hd123.baas.sop.remote.rsmas.sku.RsSellingTime;
import com.hd123.baas.sop.remote.rsmas.sku.RsSku;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel("门店商品")
public class RsShopSku extends RsMasEntity {
  private static final long serialVersionUID = -2603601805577352943L;

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "门店")
  private RsIdCodeName shop;
  @ApiModelProperty(value = "商品")
  private RsSku sku;
  @ApiModelProperty(value = "是否上架")
  private Boolean enabled;
  @ApiModelProperty(value = "是否可售卖")
  private Boolean saleStatus;
  @ApiModelProperty(value = "标签")
  private List<RsTag> tags = new ArrayList<RsTag>();
  @ApiModelProperty(value = "销售时间")
  private RsSellingTime sellingTime;
  @ApiModelProperty(value = "加料组")
  private List<RsCollocationGroup> collocationGroups = new ArrayList<RsCollocationGroup>();
  @ApiModelProperty(value = "口味组")
  private List<RsShopSkuTasteGroup> tasteGroups = new ArrayList<>();
  @ApiModelProperty(value = "售价", example = "12.25")
  private BigDecimal price;
  @ApiModelProperty(value = "会员价", example = "15.25")
  private BigDecimal mbrPrice;
  @ApiModelProperty(value = "售罄标识")
  private Boolean saleOut;
}
