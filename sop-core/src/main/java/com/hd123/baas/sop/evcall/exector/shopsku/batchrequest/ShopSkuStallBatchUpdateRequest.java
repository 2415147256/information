package com.hd123.baas.sop.evcall.exector.shopsku.batchrequest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lina
 */
@Getter
@Setter
public class ShopSkuStallBatchUpdateRequest {

  public final static String GROUP_ID = "sopBatchUpdateStallYC";
  public final static String KEY = "sopBatchUpdateStallYC";
  public final static String TYPE = "batchUpdate";
  public final static String NAME = "sop批量设置出品部门";
  public final static String APP_CENTER_ID = "appCenterId";
  public final static String APP_CENTER_NAME = "sopService";
  public final static String PARAM_REQUESTBODY = "requestBosy";
  public final static String PLATFORM_ID = "-";
  public final static String DEFAULT_PLAT_SHOP_CATEGORY_ID = "default";

  @ApiModelProperty(value = "出品部门ID")
  private String stallId;
  @ApiModelProperty(value = "sku的ID列表")
  private List<String> skuIds = new ArrayList<String>();
  @ApiModelProperty(value = "门店的ID列表")
  private List<String> shopIds = new ArrayList<String>();

  @ApiModelProperty(value = "是否全部门店")
  private Boolean allShop = false;
  @ApiModelProperty(value = "是否全部商品")
  private Boolean allSku = false;
}
