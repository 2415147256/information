package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("商品分类")
public class Category {
    @ApiModelProperty("组织类型")
    private String orgType;
    @ApiModelProperty("组织id")
    private String orgId;
    @ApiModelProperty(
            name = "分类id",
            required = true
    )
    private String id;
    @ApiModelProperty(
            name = "分类代码",
            required = true
    )
    private String code;
    @ApiModelProperty("分类名称")
    private String name;
    @ApiModelProperty("上级分类")
    private String upper;
    @ApiModelProperty("分类路径")
    private String path;
    @ApiModelProperty("分类层级")
    private int level;

    public Category() {
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

    public int getLevel() {
        return this.level;
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

    public void setLevel(int level) {
        this.level = level;
    }
}
