package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("品牌")
public class Brand {
    @ApiModelProperty(
            value = "组织类型",
            required = true
    )
    private String orgType;
    @ApiModelProperty(
            value = "组织id",
            required = true
    )
    private String orgId;
    @ApiModelProperty(
            value = "品牌id",
            required = true
    )
    private String id;
    @ApiModelProperty(
            value = "品牌代码",
            required = true
    )
    private String code;
    @ApiModelProperty(
            value = "品牌名称",
            required = true
    )
    private String name;
    @ApiModelProperty("上级品牌")
    private String upper;
    @ApiModelProperty("品牌路径")
    private String path;

    public Brand() {
    }

    public String getOrgType() {
        return this.orgType;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public String getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getUpper() {
        return this.upper;
    }

    public String getPath() {
        return this.path;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUpper(String upper) {
        this.upper = upper;
    }

    public void setPath(String path) {
        this.path = path;
    }
}