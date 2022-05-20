package com.hd123.baas.sop.service.api.feedback;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 质量反馈额外字段
 *
 * @author Y.U.A.N
 */
@Getter
@Setter
public class FeedbackExt {
  @ApiModelProperty(value = "最后审批人名称,企业微信拒绝", example = "张三")
  private String lastApprovalName;
  @ApiModelProperty(value = "最后审批人手机号,企业微信拒绝", example = "17878787788")
  private String lastApprovalMobile;

}
