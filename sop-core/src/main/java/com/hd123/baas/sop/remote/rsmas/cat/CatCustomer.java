package com.hd123.baas.sop.remote.rsmas.cat;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录客户")
public class CatCustomer implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("客户ID")
    private String customerId;

    public CatCustomer() {
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}