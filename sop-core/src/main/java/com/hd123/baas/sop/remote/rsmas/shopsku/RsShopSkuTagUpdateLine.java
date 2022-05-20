package com.hd123.baas.sop.remote.rsmas.shopsku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lina
 */
@Getter
@Setter
@ApiModel("更新门店商品标签请求明细")
public class RsShopSkuTagUpdateLine {
  @ApiModelProperty(value = "标签名称")
  private String name;
  @ApiModelProperty(value = "标签颜色")
  private String color;

  @ApiModelProperty(value = "序号")
  private Integer lineNo;
}
