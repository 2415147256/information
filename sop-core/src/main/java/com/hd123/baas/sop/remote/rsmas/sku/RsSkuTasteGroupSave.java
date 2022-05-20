package com.hd123.baas.sop.remote.rsmas.sku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lins
 */
@Getter
@Setter
@ApiModel("修改商品口味组")
public class RsSkuTasteGroupSave {
    @ApiModelProperty(value = "口味组id", required = true)
    private String groupId;
    @ApiModelProperty(value = "显示序号")
    private Integer showIndex;
}
