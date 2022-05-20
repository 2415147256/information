package com.hd123.baas.sop.remote.rsmas2.shopsku;

import com.hd123.baas.sop.remote.rsmas2.RsEntityDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@Getter
@Setter
public class RsShopSkuDto extends RsEntityDto {
  private String shopId;
  private String skuId;

  private Shop shop;
  private Sku sku;

  @Getter
  @Setter
  public static class Shop {
    private String code;
    private String name;
  }

  @Getter
  @Setter
  public static class Sku {
    private String code;
    private String name;
  }

}
