/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	TasteGroupCreation.java
 * 模块说明：
 * 修改历史：
 * 2020/12/22 - lzy - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.taste;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lzy
 */
@Getter
@Setter
@ApiModel("口味组新建")
public class TasteGroupCreation extends TasteGroupUpdate {

  @ApiModelProperty(value = "代码")
  private String code;
}
