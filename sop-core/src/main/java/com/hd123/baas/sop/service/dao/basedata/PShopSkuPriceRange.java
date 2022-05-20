package com.hd123.baas.sop.service.dao.basedata;

import com.hd123.baas.sop.service.api.skutag.ShopSkuPriceRange;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import lombok.Getter;
import lombok.Setter;

@SchemaMeta
@MapToEntity(ShopSkuPriceRange.class)
@Getter
@Setter
public class PShopSkuPriceRange extends PStandardEntity {
  public static final String TABLE_NAME = "shop_sku_price_range";
  public static final String TABLE_CAPTION = "门店商品价格带";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "orgId";
  public static final String SHOP_ID = "shopId";
  public static final String SHOP_CODE = "shopCode";
  public static final String SHOP_NAME = "shopName";
  public static final String SKU_ID = "skuId";
  public static final String GD_GID = "gdGid";
  public static final String SKU_CODE = "skuCode";
  public static final String INPUT_CODE = "inputCode";
  public static final String SKU_NAME = "skuName";
  public static final String SKU_QPC = "skuQpc";
  public static final String TITLE = "title";
  public static final String SALE_PRICE = "salePrice";
  public static final String TAG_IDS = "tag_ids";
  public static final String DELETED = "deleted";
}
