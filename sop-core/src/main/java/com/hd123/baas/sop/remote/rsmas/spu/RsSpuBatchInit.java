package com.hd123.baas.sop.remote.rsmas.spu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lina
 */
@Getter
@Setter
@ApiModel("SPU初始化")
public class RsSpuBatchInit implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "明细")
  private List<RsSpuBatchInitLine> lines = new ArrayList<>();

}
