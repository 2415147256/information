package com.hd123.baas.sop.remote.rsmkhpms.entity;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class BBasePricePromListReq implements Serializable {
  @ApiModelProperty(value = "促销单UUIDs")
  private List<String> uuids;
  @ApiModelProperty(value = "拓展属性 fetch_lines fetch_shops")
  private String[] fetchParts;
}
