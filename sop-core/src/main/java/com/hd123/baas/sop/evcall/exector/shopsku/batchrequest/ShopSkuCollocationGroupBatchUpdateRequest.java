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
public class ShopSkuCollocationGroupBatchUpdateRequest {
  public final static String GROUP_ID = "sopBatchUpdateCollocationGroupYC";
  public final static String KEY = "sopBatchUpdateCollocationGroupYC";
  public final static String TYPE = "BatchUpdate";
  public final static String NAME = "sop批量设置加料组";
  public final static String APP_CENTER_ID = "appCenterId";
  public final static String APP_CENTER_NAME = "sopService";
  public final static String PARAM_REQUESTBODY = "requestBosy";

  @ApiModelProperty(value = "加料组ID")
  private String collocationGroupId;
  @ApiModelProperty(value = "sku的ID列表")
  private List<String> skuIds = new ArrayList<String>();
  @ApiModelProperty(value = "门店的ID列表")
  private List<String> shopIds = new ArrayList<String>();

  @ApiModelProperty(value = "是否全部门店")
  private Boolean allShop = false;
  @ApiModelProperty(value = "是否全部商品")
  private Boolean allSku = false;
}
