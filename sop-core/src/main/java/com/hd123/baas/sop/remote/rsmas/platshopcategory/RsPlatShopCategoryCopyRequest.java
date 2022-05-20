/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop 文件名：	RsPlatShopCategoryCopyRequest.java 模块说明： 修改历史： 2021/8/17 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platshopcategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XLT
 */
@Getter
@Setter
@ApiModel("门店平台类目复制请求")
public class RsPlatShopCategoryCopyRequest {
  @ApiModelProperty("平台ID")
  private String platformId;
  @ApiModelProperty("类型")
  private String type;

  @ApiModelProperty("来源门店ID")
  private String sourceShopId;
  @ApiModelProperty("门店ID列表")
  private List<String> shopIds = new ArrayList<String>();
}