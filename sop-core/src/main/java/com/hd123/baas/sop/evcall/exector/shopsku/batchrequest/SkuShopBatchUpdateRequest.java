/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	mas-openapi-v2 文件名：	IdShopId.java 模块说明： 修改历史：
 * <p>
 * 2021年1月6日 - lsz - 创建。
 */
package com.hd123.baas.sop.evcall.exector.shopsku.batchrequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class SkuShopBatchUpdateRequest implements Serializable {

  public final static String ENABLE_GROUP_ID = "sopBatchEnableSkuShopYC";
  public final static String ENABLE_KEY = "sopEnableSkuShopYC";
  public final static String ENABLE_TYPE = "BatchEnable";
  public final static String ENABLE_NAME = "sop批量上架";
  public final static String APP_CENTER_ID = "appCenterId";
  public final static String APP_CENTER_NAME = "sopService";
  public final static String PARAM_REQUESTBODY = "requestBosy";

  public final static String DISABLE_GROUP_ID = "sopBatchDisableSkuShopYC";
  public final static String DISABLE_KEY = "sopDisableSkuShopYC";
  public final static String DISABLE_TYPE = "BatchDisable";
  public final static String DISABLE_NAME = "sop批量下架";

  public final static String REMOVE_GROUP_ID = "sopBatchRemoveSkuShopYC";
  public final static String REMOVE_KEY = "sopRemoveSkuShopYC";
  public final static String REMOVE_TYPE = "BatchRemove";
  public final static String REMOVE_NAME = "sop批量删除";

  private static final long serialVersionUID = 8982786246274429919L;

  @ApiModelProperty(value = "sku的ID列表")
  private List<String> skuIds = new ArrayList<String>();
  @ApiModelProperty(value = "门店的ID列表")
  private List<String> shopIds = new ArrayList<String>();
  @ApiModelProperty(value = "是否全部门店")
  private Boolean allShop = false;
  @ApiModelProperty(value = "是否全部商品")
  private Boolean allSku = false;

}
