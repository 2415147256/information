package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("商品特性")
public class ProductAttribute {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("可选值")
    private List<String> options;

    public ProductAttribute() {
    }

    public String getName() {
        return this.name;
    }

    public List<String> getOptions() {
        return this.options;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}