package com.hd123.baas.sop.remote.rsmas.platshopcategory;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("平台门店类目商品")
public class RsPlatShopCategorySku extends RsMasEntity {

  @ApiModelProperty("组织类型")
  private String orgType;
  @ApiModelProperty("组织id")
  private String orgId;
  @ApiModelProperty("门店id")
  private String shopId;
  @ApiModelProperty("平台id")
  private String platformId;

  @ApiModelProperty("平台门店类目ID")
  private String platShopCategoryId;
  @ApiModelProperty("商品SKUID")
  private String skuId;
  @ApiModelProperty("排序值")
  private Integer sort;

}