/* 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 *
 * 项目名：	com.hd123.mas.commons.service.api.source
 * 文件名：	SourceBatchInit
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author 老娜
 * @date 2020/3/2
 */
@Getter
@Setter
@ApiModel("选项初始化")
public class RsOptionsBatchInit implements Serializable {

  private static final long serialVersionUID = -6246852291690878606L;

  @ApiModelProperty(value = "类型")
  private String type;

  @ApiModelProperty(value = "明细")
  private List<RsOptionsBatchInitLine> lines = new ArrayList<RsOptionsBatchInitLine>();
}
