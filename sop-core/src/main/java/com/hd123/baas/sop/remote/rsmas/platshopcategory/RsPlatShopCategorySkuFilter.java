package com.hd123.baas.sop.remote.rsmas.platshopcategory;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("平台门店类目商品查询条件")
public class RsPlatShopCategorySkuFilter  extends RsMasFilter {

    @ApiModelProperty("组织类型等于")
    private String orgTypeEq;
    @ApiModelProperty("组织id等于")
    private String orgIdEq;
    @ApiModelProperty("平台id等于")
    private String platformIdEq;
    @ApiModelProperty("平台id在之中")
    private List<String> platformIdIn;
    @ApiModelProperty("门店id等于")
    private String shopIdEq;
    @ApiModelProperty("门店id在之中")
    private List<String> shopIdIn;
    @ApiModelProperty("平台类目类型等于")
    private String platformCategoryTypeEq;
    @ApiModelProperty("平台类目类型在之中")
    private List<String> platformCategoryTypeIn;
    @ApiModelProperty("平台类目id等于")
    private String platformCategoryIdEq;
    @ApiModelProperty("平台类目id在之中")
    private List<String> platformCategoryIdIn;
    @ApiModelProperty("平台门店类目id等于")
    private String platShopCategoryIdEq;
    @ApiModelProperty("平台门店类目id在之中")
    private List<String> platShopCategoryIdIn;
    @ApiModelProperty("商品SKUID在之中")
    private List<String> skuIdIn;
    @ApiModelProperty("商品SKUID类似于")
    private String skuIdLike;
}
