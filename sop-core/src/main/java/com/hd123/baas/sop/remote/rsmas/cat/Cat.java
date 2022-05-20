package com.hd123.baas.sop.remote.rsmas.cat;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录")
public class Cat extends MasEntity {
    private static final long serialVersionUID = 1L;
    public static final String PART_CAT_CUSTOMER = "customers";
    public static final String PART_CAT_SHOP = "shops";
    public static final String PART_CAT_SKU = "skus";
    @ApiModelProperty("组织id")
    private String orgId;
    @ApiModelProperty("代码")
    private String code;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("简称")
    private String title;
    @ApiModelProperty("所属门店ID")
    private String ownerId;
    @ApiModelProperty("所属门店名称")
    private String ownerName;
    @ApiModelProperty("门店数量")
    private int shopCount;
    @ApiModelProperty("客户数量")
    private int customerCount;
    @ApiModelProperty("商品数量")
    private int skuCount;
    @ApiModelProperty("是否上架")
    private Boolean enabled;
    @ApiModelProperty("目录客户")
    private List<CatCustomer> customers;
    @ApiModelProperty("目录门店")
    private List<CatShop> shops;
    @ApiModelProperty("目录商品")
    private List<CatSKU> skus;

    public Cat() {
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public int getShopCount() {
        return this.shopCount;
    }

    public int getCustomerCount() {
        return this.customerCount;
    }

    public int getSkuCount() {
        return this.skuCount;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public List<CatCustomer> getCustomers() {
        return this.customers;
    }

    public List<CatShop> getShops() {
        return this.shops;
    }

    public List<CatSKU> getSkus() {
        return this.skus;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setShopCount(int shopCount) {
        this.shopCount = shopCount;
    }

    public void setCustomerCount(int customerCount) {
        this.customerCount = customerCount;
    }

    public void setSkuCount(int skuCount) {
        this.skuCount = skuCount;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setCustomers(List<CatCustomer> customers) {
        this.customers = customers;
    }

    public void setShops(List<CatShop> shops) {
        this.shops = shops;
    }

    public void setSkus(List<CatSKU> skus) {
        this.skus = skus;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
