/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 *
 * 项目名：	mas-company-api
 * 文件名：	RsDescription.java
 * 模块说明：
 * 修改历史：
 * 2019年9月3日 - sulin - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sulin
 *
 */
@Getter
@Setter
@ApiModel("详情描述")
public class Description {
  @ApiModelProperty("排序")
  private int order;
  @ApiModelProperty("类型")
  private String type;
  @ApiModelProperty("内容")
  private String content;
}
