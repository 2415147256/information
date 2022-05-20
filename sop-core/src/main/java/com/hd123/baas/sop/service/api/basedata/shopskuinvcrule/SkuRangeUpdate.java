/**
 *
 */
package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qyh
 *
 */
@Getter
@Setter
@ApiModel("门店商品库存规则商品修改参数")
public class SkuRangeUpdate implements Serializable {
    private static final long serialVersionUID = -6950030726306207905L;
    @ApiModelProperty(value = "是否移除商品")
    private boolean delete = false;
    @ApiModelProperty(value = "商品范围")
    private List<RsIdName> skus = new ArrayList<>();
}


