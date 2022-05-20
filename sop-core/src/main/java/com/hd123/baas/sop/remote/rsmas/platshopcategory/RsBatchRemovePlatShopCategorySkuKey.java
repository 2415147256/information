package com.hd123.baas.sop.remote.rsmas.platshopcategory;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class RsBatchRemovePlatShopCategorySkuKey {

    private String platShopCategoryId;
    private String shopId;
    private String skuId;

    public RsBatchRemovePlatShopCategorySkuKey() {
        super();
    }

    public RsBatchRemovePlatShopCategorySkuKey(String platShopCategoryId, String shopId, String skuId) {
        super();
        this.platShopCategoryId = platShopCategoryId;
        this.shopId = shopId;
        this.skuId = skuId;
    }
}
