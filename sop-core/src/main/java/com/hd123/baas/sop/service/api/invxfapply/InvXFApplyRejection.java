/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	workspace_shop
 * 文件名：	RequireApplyAuditor.java
 * 模块说明：
 * 修改历史：
 * 2020年02月14日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.service.api.invxfapply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author huangjunxian
 * @since 1.0
 */
@Data
@ApiModel(description = "拒绝并通知ERP")
public class InvXFApplyRejection implements Serializable {
  private static final long serialVersionUID = 9007087859977042821L;

  @ApiModelProperty(value = "单号", required = true)
  @NotBlank
  private String num;
  @ApiModelProperty(value = "拒绝原因", required = false)
  private String rejectReason;
}
