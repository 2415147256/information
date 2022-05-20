/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsPlatformCategoryUpdate.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月6日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platformcategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@ApiModel("平台类目更新对象")
public class RsPlatformCategoryUpdate {

  @ApiModelProperty("代码")
  private String code;
  @ApiModelProperty("名称")
  private String name;
  @ApiModelProperty("上级")
  private String upperId;
  @ApiModelProperty("排序值")
  private Integer sort;
  @ApiModelProperty("图片")
  private String image;
  @ApiModelProperty(value = "是否启用")
  private Boolean enabled = true;
  @ApiModelProperty(value = "是否显示")
  private Boolean isShow = true;
}
