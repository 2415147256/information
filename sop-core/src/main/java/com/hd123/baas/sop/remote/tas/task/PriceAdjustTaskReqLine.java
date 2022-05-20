package com.hd123.baas.sop.remote.tas.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 外部服务-任务创建请求明细
 *
 * @since 1.3.0
 */
@Setter
@Getter
@ApiModel("外部服务-任务创建请求line")
public class PriceAdjustTaskReqLine {
  @ApiModelProperty(required = true, value = "业务单据明细价签实体JSON： {\"明细ID\":\"XXX\",\"商品GID\":\"XXX\",\"商品代码\":\"XXX\",\"商品条码\":\"XXX\",\"商品名称\":\"XXX\",\"商品规格\":\"XXX\",\"商品规格文本\":\"XXX\",\"商品分类ID\":\"XXX\",\"商品分类代码\":\"XXX\",\"商品分类名称\":\"XXX\",\"调整前价格\":\"XXX\",\"调整后价格\":\"XXX\",\"商品售价单位\":\"skuPriceUnit\"}，业务单据明细畅销品缺货订货实体JSON： {\"行号\":\"XXX\",\"商品GID\":\"XXX\",\"商品代码\":\"XXX\",\"商品条码\":\"XXX\",\"商品名称\":\"XXX\",\"商品规格\":\"XXX\",\"商品规格文本\":\"XXX\",\"商品售价单位\":\"skuPriceUnit\",\"商品分类ID\":\"XXX\",\"商品分类代码\":\"XXX\",\"商品分类名称\":\"XXX\",\"订货数量\":\"XXX\",\"库存数量\":\"XXX\",\"安全库存\":\"XXX\"}",
      example = "业务单据价签明细实体JSON： {\"lineId\":\"XXX\",\"skuGid\":\"XXX\",\"skuCode\":\"XXX\",\"skuBarCode\":\"XXX\",\"skuName\":\"XXX\",\"skuQpc\":\"XXX\",\"skuQpcStr\":\"XXX\",\"catId\":\"XXX\",\"catCode\":\"XXX\",\"catName\":\"XXX\",\"fromPrice\":\"XXX\",\"toPrice\":\"XXX\",\"skuPriceUnit\":\"XXX\"},业务单据明细畅销品缺货订货实体JSON： {\"lineId\":\"XXX\",\"skuGid\":\"XXX\",\"skuCode\":\"XXX\",\"skuBarCode\":\"XXX\",\"skuName\":\"XXX\",\"skuQpc\":\"XXX\",\"skuQpcStr\":\"XXX\",\"skuPriceUnit\":\"XXX\",\"catId\":\"XXX\",\"catCode\":\"XXX\",\"catName\":\"XXX\",\"qty\":\"XXX\",\"invQty\":\"XXX\",\"safetyQty\":\"XXX\"}")
  private String json;
}
