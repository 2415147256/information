/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 *
 * 项目名：	mas-cms-api
 * 文件名：	PlatformCategory.java
  * 模块说明：
 * 修改历史：

 * 2019年9月19日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.platshopcategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel("门店平台类目复制请求")
public class PlatShopCategoryCopyRequest {

  @ApiModelProperty("是否全部门店")
  private boolean allUnit;
  @ApiModelProperty("门店ID列表")
  private List<String> shopIds = new ArrayList<String>();
  @ApiModelProperty("来源门店ID")
  private String sourceShopId;
}
