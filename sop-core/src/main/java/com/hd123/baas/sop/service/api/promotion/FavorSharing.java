package com.hd123.baas.sop.service.api.promotion;

import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("促销优惠分摊")
public class FavorSharing {
  @ApiModelProperty("促销费用承担方")
  private UCN targetUnit;
  @ApiModelProperty("承担比例")
  private BigDecimal rate;

  public enum TypeEnum {
    STORE, // 门店
    HEAD, // 总部
    SUPER // 督导
  }
}
