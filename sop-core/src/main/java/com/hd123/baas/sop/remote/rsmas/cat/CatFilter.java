package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录查询条件定义")
public class CatFilter extends MasFilter {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组织类型等于...")
    private String orgTypeEq;
    @ApiModelProperty(value = "组织ID等于")
    private String orgIdEq;

    @ApiModelProperty(value = "组织ID在范围之内")
    private List<String> orgIdIn;
    @ApiModelProperty("关键字等于，匹配名称/条码的模糊查询")
    private String keyword;
    @ApiModelProperty("名称类似于")
    private String nameLike;
    @ApiModelProperty("是否上架")
    private Boolean enabled;
    @ApiModelProperty("包含商品ID")
    private List<String> includeSKUId;
    @ApiModelProperty("包含客户ID")
    private List<String> includeCustomerId;
    @ApiModelProperty("包含门店ID")
    private List<String> includeShopId;
    @ApiModelProperty("所属门店ID等于")
    private String ownerIdEq;
    @ApiModelProperty("所属门店ID不等于")
    private String ownerIdNotEq;
    @ApiModelProperty("所属门店ID或者目录门店ID在之中")
    private List<String> ownerIdOrCatShopIdIn;
    @ApiModelProperty("所属门店名称或者目录门店名称类似于")
    private String ownerNameOrCatShopNameLike;
    @ApiModelProperty("创建人姓名")
    private String operatorNameLike;
    @ApiModelProperty("创建人ID")
    private String operatorIdLike;

    public CatFilter() {
    }

    public String getKeyword() {
        return this.keyword;
    }

    public String getNameLike() {
        return this.nameLike;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public List<String> getIncludeSKUId() {
        return this.includeSKUId;
    }

    public List<String> getIncludeCustomerId() {
        return this.includeCustomerId;
    }

    public List<String> getIncludeShopId() {
        return this.includeShopId;
    }

    public String getOwnerIdEq() {
        return this.ownerIdEq;
    }

    public String getOwnerIdNotEq() {
        return this.ownerIdNotEq;
    }

    public List<String> getOwnerIdOrCatShopIdIn() {
        return this.ownerIdOrCatShopIdIn;
    }

    public String getOwnerNameOrCatShopNameLike() {
        return this.ownerNameOrCatShopNameLike;
    }

    public String getOperatorNameLike() {
        return this.operatorNameLike;
    }

    public String getOperatorIdLike() {
        return this.operatorIdLike;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setIncludeSKUId(List<String> includeSKUId) {
        this.includeSKUId = includeSKUId;
    }

    public void setIncludeCustomerId(List<String> includeCustomerId) {
        this.includeCustomerId = includeCustomerId;
    }

    public void setIncludeShopId(List<String> includeShopId) {
        this.includeShopId = includeShopId;
    }

    public void setOwnerIdEq(String ownerIdEq) {
        this.ownerIdEq = ownerIdEq;
    }

    public void setOwnerIdNotEq(String ownerIdNotEq) {
        this.ownerIdNotEq = ownerIdNotEq;
    }

    public void setOwnerIdOrCatShopIdIn(List<String> ownerIdOrCatShopIdIn) {
        this.ownerIdOrCatShopIdIn = ownerIdOrCatShopIdIn;
    }

    public void setOwnerNameOrCatShopNameLike(String ownerNameOrCatShopNameLike) {
        this.ownerNameOrCatShopNameLike = ownerNameOrCatShopNameLike;
    }

    public void setOperatorNameLike(String operatorNameLike) {
        this.operatorNameLike = operatorNameLike;
    }

    public void setOperatorIdLike(String operatorIdLike) {
        this.operatorIdLike = operatorIdLike;
    }

    public List<String> getOrgIdIn() {
        return orgIdIn;
    }

    public void setOrgIdIn(List<String> orgIdIn) {
        this.orgIdIn = orgIdIn;
    }

    public String getOrgTypeEq() {
        return orgTypeEq;
    }

    public void setOrgTypeEq(String orgTypeEq) {
        this.orgTypeEq = orgTypeEq;
    }

    public String getOrgIdEq() {
        return orgIdEq;
    }

    public void setOrgIdEq(String orgIdEq) {
        this.orgIdEq = orgIdEq;
    }
}

