package com.hd123.baas.sop.remote.rsh6sop.sku.publishplan;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 方案下架对象
 *
 * @author liuhaoxin
 * @since 2021-11-29
 */
@Data
@ApiModel(description = "方案下架对象")
public class ShelveSchemeOff {

  @ApiModelProperty(value = "最后修改时间", example = "2021-12-01 12:00:01")
  private Date lstupdTime;
  @ApiModelProperty(value = "方案编码", example = "20211101")
  private String schemeNo;
  @ApiModelProperty(value = "顺序号，H6按此字段顺序加工", example = "202112021812510000")
  private Long sequenceNo;
}