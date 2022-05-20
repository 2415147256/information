package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("输入码")
public class InputCode implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String TYPE_PINYIN = "PINYIN";
    public static final String TYPE_EAN = "EAN";
    public static final String TYPE_QR = "QR";
    @ApiModelProperty(
            value = "类型",
            required = true
    )
    private String type;
    @ApiModelProperty(
            value = "代码",
            required = true
    )
    private String code;
    @ApiModelProperty(
            value = "规格",
            required = false
    )
    private BigDecimal qpc;
    @ApiModelProperty(
            value = "规格说明",
            required = false
    )
    private String qpcStr;
    @ApiModelProperty(
            value = "单位",
            required = false
    )
    private String unit;
    @ApiModelProperty("重量")
    private BigDecimal weight;

    public InputCode() {
    }

    public String getType() {
        return this.type;
    }

    public String getCode() {
        return this.code;
    }

    public BigDecimal getQpc() {
        return this.qpc;
    }

    public String getQpcStr() {
        return this.qpcStr;
    }

    public String getUnit() {
        return this.unit;
    }

    public BigDecimal getWeight() {
        return this.weight;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setQpc(BigDecimal qpc) {
        this.qpc = qpc;
    }

    public void setQpcStr(String qpcStr) {
        this.qpcStr = qpcStr;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}

