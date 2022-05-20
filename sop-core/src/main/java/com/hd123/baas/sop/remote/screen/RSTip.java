/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： RSTip.java
 * 模块说明：
 * 修改历史：
 * 2021年06月04日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.screen;

/**
 * @author huangjunxian
 * @since 1.0
 */

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "广告语")
public class RSTip extends RSOption {
  @NotBlank
  @ApiModelProperty(value = "广告语内容", required = true)
  private String content;
}