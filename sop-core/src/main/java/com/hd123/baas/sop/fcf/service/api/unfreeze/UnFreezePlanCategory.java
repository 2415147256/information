package com.hd123.baas.sop.fcf.service.api.unfreeze;

import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel
public class UnFreezePlanCategory {

  @ApiModelProperty(value = "类别明细主键", example = "c008360c-123a-4ca5-9cd2-43205bfed7aa")
  private String uuid;

  @ApiModelProperty
  private UCN categoryInfo;

  @ApiModelProperty(value = "建议的品项件数(数量)", example = "100")
  private BigDecimal suggestQty;
  @ApiModelProperty(value = "已处理的品项件量(数量)", example = "10")
  private BigDecimal confirmedQty;

  @ApiModelProperty(value = "待办品项数", example = "2")
  private BigDecimal unConfirmCount;
  @ApiModelProperty(value = "待办品项件数(数量)", example = "24")
  private BigDecimal unConfirmQty;

  @ApiModelProperty(value = "前四周平均销量", example = "20")
  private BigDecimal avgLast4weekSale;

  @ApiModelProperty("类目商品明细")
  private List<UnFreezePlanGoods> goods = new ArrayList<>();
}
