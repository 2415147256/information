package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("商品参数")
public class Parameter {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("值")
    private String value;

    public Parameter() {
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
