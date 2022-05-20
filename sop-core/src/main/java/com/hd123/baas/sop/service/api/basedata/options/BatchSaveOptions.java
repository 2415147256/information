/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 
 * 项目名：	mas-client-api
 * 文件名：	OptionsUpdate.java
 * 模块说明：	
 * 修改历史：
 * 2019年3月7日 - __Silent - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.options;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 更新选项
 * 
 * @author __Silent
 *
 */
@Getter
@Setter
public class BatchSaveOptions {

  @ApiModelProperty(value = "新建选项明细", required = true)
  private List<OptionsCreation> lines = new ArrayList<>();

}
