package com.hd123.baas.sop.service.api.explosivev2;

import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author shenmin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExplosiveEntity extends UCN {

  @ApiModelProperty("商品ID")
  private String skuId;
  @ApiModelProperty("商品GID")
  private String skuGid;
  @ApiModelProperty("商品规格")
  private BigDecimal skuQpc;
  @ApiModelProperty("商品单位")
  private String skuUnit;
}
