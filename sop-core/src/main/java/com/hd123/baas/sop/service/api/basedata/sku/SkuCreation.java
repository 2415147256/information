package com.hd123.baas.sop.service.api.basedata.sku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "销售商品新建信息")
public class SkuCreation extends SkuUpdate{
  @ApiModelProperty(value = "ERP商品GID")
  public String goodsGid;
  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "PLU码，称重商品才有")
  public String plu;
  @ApiModelProperty(value = "输入码")
  private String inputCode;
  @ApiModelProperty(value = "是否必选")
  private Boolean required;
  @ApiModelProperty(value = "H6商品类型")
  public String h6GoodsType;
  @ApiModelProperty(value = "箱规描述",required = false)
  public String qpcDesc;
}
