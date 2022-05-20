/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 *
 * 项目名：	mas-cms-api
 * 文件名：	PlatformCategoryCreation.java
  * 模块说明：
 * 修改历史：

 * 2019年9月19日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.platshopcategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class PlatShopCategoryCreation extends PlatShopCategoryUpdate {

  @ApiModelProperty("代码")
  private String code;
  @ApiModelProperty("ID")
  private String id;
  @ApiModelProperty("分类层级")
  private Integer level = 0;
  //@ApiModelProperty("路径")
  //private String path;
  @ApiModelProperty("门店id")
  private String shopId;

}
