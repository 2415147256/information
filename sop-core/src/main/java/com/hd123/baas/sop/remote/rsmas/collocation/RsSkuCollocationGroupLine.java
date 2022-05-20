/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-parent 文件名：	RsSkuCollocationGroupLine.java 模块说明： 修改历史： 2021/8/2 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.collocation;

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
@ApiModel("sku加料组批量修改请求明细行")
public class RsSkuCollocationGroupLine {

  @ApiModelProperty(value = "skuId")
  private String skuId;
  @ApiModelProperty(value = "商品加料组列表")
  private List<RsSkuCollocationGroupSave> groupList = new ArrayList<>();
}