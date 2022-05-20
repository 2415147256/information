package com.hd123.baas.sop.remote.rsmas.groupTag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RsSKUGroupTagLine
 * @Description:
 * @Author zhanzhijie
 * @Date 2022/4/15
 * @Version
 **/
@Getter
@Setter
@ApiModel("修改SKU分组标签明细")
public class RsSKUGroupTagLine implements Serializable {

  private static final long serialVersionUID = 17L;

  @ApiModelProperty(value = "SKUID", required = true)
  private String skuId;
  @ApiModelProperty(value = "分组标签列表")
  private List<RsSkuGroupTagSave> groupTags = new ArrayList<>();
}
