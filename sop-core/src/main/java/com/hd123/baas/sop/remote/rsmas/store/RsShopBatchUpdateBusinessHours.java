/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 *
 * 项目名：	sop-parent
 * 文件名：	ShopBatchUpdateBusinessHours.java
 * 模块说明：
 * 修改历史：
 * 2021/8/10 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author XLT
 */
@Getter
@Setter
@ApiModel("批量更新门店营业时间对象")
public class RsShopBatchUpdateBusinessHours {
  @ApiModelProperty("门店id列表")
  List<String> idsRequest;
  @ApiModelProperty("门店营业时间")
  List<String> businessHours;
}