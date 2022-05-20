/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 *
 * 项目名：	mas-cms-api
 * 文件名：	PlatformCategoryUpdate.java
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
public class PlatShopCategoryUpdate {

  @ApiModelProperty("名称")
  private String name;
  @ApiModelProperty("上级")
  private String upperId;
  @ApiModelProperty("排序值")
  private Integer sort;
  @ApiModelProperty(value = "是否启用")
  private Boolean enabled = true;
  @ApiModelProperty(value = "是否显示")
  private Boolean isShow = true;

  @ApiModelProperty("图片")
  private String image;

}
