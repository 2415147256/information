/* 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 *
 * 项目名：	com.hd123.mas.commons.service.api.source
 * 文件名：	SourceBatchInitLine
 * 模块说明：
 * 修改历史：
 * 2020/3/2 - 老娜 - 创建。
 */

package com.hd123.baas.sop.remote.rsmas.options;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author 老娜
 * @date 2020/3/2
 */
@Getter
@Setter
@ApiModel("选项初始化明细")
public class RsOptionsBatchInitLine implements Serializable {
  private static final long serialVersionUID = 1355431037663533182L;

  @ApiModelProperty(value = "选项名")
  private String key;
  @ApiModelProperty(value = "选项值")
  private String value;
  @ApiModelProperty(value = "备注")
  private String remark;
}
