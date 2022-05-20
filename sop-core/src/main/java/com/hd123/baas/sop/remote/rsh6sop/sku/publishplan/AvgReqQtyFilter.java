package com.hd123.baas.sop.remote.rsh6sop.sku.publishplan;

import java.util.List;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日均叫货量查询对象
 * 
 * @author liuhaoxin
 * @since 2021-11-29
 */
@Data
@ApiModel(description = "日均叫货量查询对象")
public class AvgReqQtyFilter {

  @ApiModelProperty(value = "近…天，默认5", example = "5")
  private Integer daysEquals;
  @ApiModelProperty(value = "商品GID在…中")
  private List<Integer> gdGidIn;
  @NotBlank
  @ApiModelProperty(value = "组织GID等于", required = true)
  private Integer orgGidEquals;
  @NotBlank
  @ApiModelProperty(value = "类型等于，0-门店叫货，1-公司间叫货", required = true)
  private Integer typeEquals = 0;
}
