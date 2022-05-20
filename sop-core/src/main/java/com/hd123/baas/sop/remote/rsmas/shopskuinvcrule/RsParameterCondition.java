package com.hd123.baas.sop.remote.rsmas.shopskuinvcrule;

import com.hd123.rumba.commons.biz.entity.Operator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author qyh
 */
@Getter
@Setter
@ApiModel
public class RsParameterCondition {

  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "操作人")
  private Operator operator;
  @ApiModelProperty(value = "值")
  private String[] values;

}
