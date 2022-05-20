package com.hd123.baas.sop.remote.rsmas.cat;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录门店")
public class CatShop implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("门店ID")
    private String shopId;

    public CatShop() {
    }

    public String getShopId() {
        return this.shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}