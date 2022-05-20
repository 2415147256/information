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

import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel("门店平台类目移动请求")
public class PlatShopCategoryMoveRequest {

  @ApiModelProperty("门店前端类目移动请求明细")
  private List<PlatShopCategoryMoveRequestLine> items;
}
