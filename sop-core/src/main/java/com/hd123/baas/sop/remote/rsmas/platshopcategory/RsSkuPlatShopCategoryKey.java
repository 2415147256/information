package com.hd123.baas.sop.remote.rsmas.platshopcategory;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel
public class RsSkuPlatShopCategoryKey {
    private String skuId;
    private List<String> platShopCategoryIds = new ArrayList<String>();
}
