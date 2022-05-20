/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sos-service
 * 文件名：	BSOPFeedbackReasonSaver.java
 * 模块说明：	
 * 修改历史：
 * 2021年2月19日 - panzhibin - 创建。
 */
package com.hd123.baas.sop.remote.rssos.feedback;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 质量反馈截止时间对象
 * 
 * @author panzhibin
 *
 */
@Data
@ApiModel(description = "质量反馈截止时间对象")
public class FeedbackEndTime implements Serializable {

  private static final long serialVersionUID = 7922703260124580423L;
  public static final String endTimeFormat = "HH:mm";

  @NotBlank
  @ApiModelProperty(value = "代码", example = "offline", required = true)
  @Length(max = 64)
  private String code;

  @ApiModelProperty(value = "收货质量反馈截止时间", example = "14:00", required = false)
  private String feedbackEndTime;

  @ApiModelProperty(value = "收货质量反馈截止天数", example = "14:00", required = false)
  private Integer feedbackEndDays;

}
