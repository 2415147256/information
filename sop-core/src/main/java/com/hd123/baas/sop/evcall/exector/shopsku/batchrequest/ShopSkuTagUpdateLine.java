package com.hd123.baas.sop.evcall.exector.shopsku.batchrequest;

import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSkuTagUpdateLine;
import com.hd123.rumba.commons.util.converter.Converter;
import com.hd123.rumba.commons.util.converter.ConverterBuilder;
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
public class ShopSkuTagUpdateLine {

  public static Converter<ShopSkuTagUpdateLine, RsShopSkuTagUpdateLine> FROM_SHOP_SKU_TAG_UPDATE_LINE = ConverterBuilder
      .newBuilder(ShopSkuTagUpdateLine.class, RsShopSkuTagUpdateLine.class).build();

  @ApiModelProperty(value = "标签名称")
  private String name;
  @ApiModelProperty(value = "标签颜色")
  private String color;

  @ApiModelProperty(value = "序号")
  private Integer lineNo;
}
