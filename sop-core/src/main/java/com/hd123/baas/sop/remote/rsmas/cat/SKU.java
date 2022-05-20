package com.hd123.baas.sop.remote.rsmas.cat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("SKU-V2")
public class SKU extends MasEntity {
    private static final long serialVersionUID = 1L;
    public static final String PART_IMAGES = "images";
    public static final String PART_BRAND = "brand";
    public static final String PART_CATEGORY = "category";
    public static final String PART_TAGS = "tags";
    public static final String PART_DESCRIPTIONS = "descriptions";
    public static final String PART_ATTRIBUTES = "attributes";
    public static final String PART_PARAMETER = "parameters";
    public static final String PART_PROPERTIES = "properties";
    public static final String PART_COMBOS = "combos";
    public static final String PART_INPUT_CODES = "inputCodes";
    @ApiModelProperty("组织类型")
    private String orgType;
    @ApiModelProperty("组织id")
    private String orgId;
    @ApiModelProperty("goodsId")
    private String goodsId;
    @ApiModelProperty("spuId")
    private String spuId;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("代码")
    private String code;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("副标题")
    private String subTitle;
    @ApiModelProperty("包装规格")
    private String spec;
    @ApiModelProperty("包装规格浮动空间")
    private BigDecimal specFloatingSpace;
    @ApiModelProperty("规格")
    private BigDecimal qpc;
    @ApiModelProperty("规格说明")
    private String qpcStr;
    @ApiModelProperty("商品统一代码")
    private String upc;
    @ApiModelProperty("单位")
    private String unit;
    @ApiModelProperty("重量")
    private BigDecimal weight;
    @ApiModelProperty("长度")
    private BigDecimal length;
    @ApiModelProperty("宽度")
    private BigDecimal width;
    @ApiModelProperty("高度")
    private BigDecimal height;
    @ApiModelProperty("参考进价")
    private BigDecimal purchasePrice;
    @ApiModelProperty("参考市场价")
    private BigDecimal marketPrice;
    @ApiModelProperty("主图")
    private String image;
    @ApiModelProperty("副图")
    private List<String> images;
    @ApiModelProperty("主视频")
    private String video;
    @ApiModelProperty("是否合格, 可用于标记主图是否合格")
    private Boolean qualified;
    @ApiModelProperty("品牌")
    private Brand brand;
    @ApiModelProperty("分类")
    private Category category;
    @ApiModelProperty("标签")
    private List<Tag> tags = new ArrayList();
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("图文详情")
    private List<Description> descriptions = new ArrayList();
    @ApiModelProperty("商品特性")
    private List<ProductAttribute> attributes = new ArrayList();
    @ApiModelProperty("自定义属性")
    private List<Parameter> customFields = new ArrayList();
    @ApiModelProperty("商品销售属性")
    private List<ProductProperty> properties = new ArrayList();
    @ApiModelProperty("组合明细")
    private List<SKUCombo> items = new ArrayList();
    @ApiModelProperty("首次售卖时间")
    private Date firstSaleTime;
    @ApiModelProperty("开始售卖时间")
    private Date startSellDate;
    @ApiModelProperty("结束售卖时间")
    private Date endSellDate;
    @ApiModelProperty("销售方式, 散装, 标准")
    private String saleType;
    @ApiModelProperty("计价方式, 计数, 计重")
    private String valuationType;
    @ApiModelProperty("经营方式, 经销，联营")
    private String businessType;
    @ApiModelProperty("上下架状态")
    private Boolean enabled;
    @ApiModelProperty("是否可售卖")
    private String saleStatus;
    @ApiModelProperty("sku类型，普通/组合")
    private String comboType;
    @ApiModelProperty("组合商品类型，同品/异品")
    private Boolean isDiffCombo;
    @ApiModelProperty("税率")
    private String taxRate;
    @ApiModelProperty("税率分类码")
    private String taxSortCode;
    @ApiModelProperty("输入码")
    private List<InputCode> inputCodes;
    @ApiModelProperty("会员价")
    private BigDecimal mbrPrice;
    @ApiModelProperty("是否虚拟商品")
    private Boolean isVirtual;
    @ApiModelProperty("最小起订量")
    private BigDecimal minOrderQty;

    public SKU() {
        this.enabled = Boolean.TRUE;
        this.inputCodes = new ArrayList();
        this.isVirtual = Boolean.FALSE;
        this.minOrderQty = BigDecimal.ZERO;
    }

    public String getOrgType() {
        return this.orgType;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public String getGoodsId() {
        return this.goodsId;
    }

    public String getSpuId() {
        return this.spuId;
    }

    public String getType() {
        return this.type;
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

    public String getSubTitle() {
        return this.subTitle;
    }

    public String getSpec() {
        return this.spec;
    }

    public BigDecimal getSpecFloatingSpace() {
        return this.specFloatingSpace;
    }

    public BigDecimal getQpc() {
        return this.qpc;
    }

    public String getQpcStr() {
        return this.qpcStr;
    }

    public String getUpc() {
        return this.upc;
    }

    public String getUnit() {
        return this.unit;
    }

    public BigDecimal getWeight() {
        return this.weight;
    }

    public BigDecimal getLength() {
        return this.length;
    }

    public BigDecimal getWidth() {
        return this.width;
    }

    public BigDecimal getHeight() {
        return this.height;
    }

    public BigDecimal getPurchasePrice() {
        return this.purchasePrice;
    }

    public BigDecimal getMarketPrice() {
        return this.marketPrice;
    }

    public String getImage() {
        return this.image;
    }

    public List<String> getImages() {
        return this.images;
    }

    public String getVideo() {
        return this.video;
    }

    public Boolean getQualified() {
        return this.qualified;
    }

    public Brand getBrand() {
        return this.brand;
    }

    public Category getCategory() {
        return this.category;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Description> getDescriptions() {
        return this.descriptions;
    }

    public List<ProductAttribute> getAttributes() {
        return this.attributes;
    }

    public List<Parameter> getCustomFields() {
        return this.customFields;
    }

    public List<ProductProperty> getProperties() {
        return this.properties;
    }

    public List<SKUCombo> getItems() {
        return this.items;
    }

    public Date getFirstSaleTime() {
        return this.firstSaleTime;
    }

    public Date getStartSellDate() {
        return this.startSellDate;
    }

    public Date getEndSellDate() {
        return this.endSellDate;
    }

    public String getSaleType() {
        return this.saleType;
    }

    public String getValuationType() {
        return this.valuationType;
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public String getSaleStatus() {
        return this.saleStatus;
    }

    public String getComboType() {
        return this.comboType;
    }

    public Boolean getIsDiffCombo() {
        return this.isDiffCombo;
    }

    public String getTaxRate() {
        return this.taxRate;
    }

    public String getTaxSortCode() {
        return this.taxSortCode;
    }

    public List<InputCode> getInputCodes() {
        return this.inputCodes;
    }

    public BigDecimal getMbrPrice() {
        return this.mbrPrice;
    }

    public Boolean getIsVirtual() {
        return this.isVirtual;
    }

    public BigDecimal getMinOrderQty() {
        return this.minOrderQty;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public void setSpecFloatingSpace(BigDecimal specFloatingSpace) {
        this.specFloatingSpace = specFloatingSpace;
    }

    public void setQpc(BigDecimal qpc) {
        this.qpc = qpc;
    }

    public void setQpcStr(String qpcStr) {
        this.qpcStr = qpcStr;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setQualified(Boolean qualified) {
        this.qualified = qualified;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescriptions(List<Description> descriptions) {
        this.descriptions = descriptions;
    }

    public void setAttributes(List<ProductAttribute> attributes) {
        this.attributes = attributes;
    }

    public void setCustomFields(List<Parameter> customFields) {
        this.customFields = customFields;
    }

    public void setProperties(List<ProductProperty> properties) {
        this.properties = properties;
    }

    public void setItems(List<SKUCombo> items) {
        this.items = items;
    }

    public void setFirstSaleTime(Date firstSaleTime) {
        this.firstSaleTime = firstSaleTime;
    }

    public void setStartSellDate(Date startSellDate) {
        this.startSellDate = startSellDate;
    }

    public void setEndSellDate(Date endSellDate) {
        this.endSellDate = endSellDate;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public void setValuationType(String valuationType) {
        this.valuationType = valuationType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setSaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public void setComboType(String comboType) {
        this.comboType = comboType;
    }

    public void setIsDiffCombo(Boolean isDiffCombo) {
        this.isDiffCombo = isDiffCombo;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public void setTaxSortCode(String taxSortCode) {
        this.taxSortCode = taxSortCode;
    }

    public void setInputCodes(List<InputCode> inputCodes) {
        this.inputCodes = inputCodes;
    }

    public void setMbrPrice(BigDecimal mbrPrice) {
        this.mbrPrice = mbrPrice;
    }

    public void setIsVirtual(Boolean isVirtual) {
        this.isVirtual = isVirtual;
    }

    public void setMinOrderQty(BigDecimal minOrderQty) {
        this.minOrderQty = minOrderQty;
    }
}
