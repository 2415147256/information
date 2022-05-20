/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	ReceiptFeedbackOption.java
 * 模块说明：
 * 修改历史：
 * 2020/10/29 - Leo - 创建。
 */

package com.hd123.baas.sop.service.api.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Leo
 */
@ApiModel(description = "质量反馈选项")
@Getter
@Setter
public class FeedbackOption implements Serializable {
  private static final long serialVersionUID = -6248937216654307883L;

  @ApiModelProperty(value = "收货质量反馈截止时间", example = "14:00", required = true)
  private String feedbackEndTime;
  @ApiModelProperty(value = "反馈时间设置，自确认收货日起 n 天内可反馈", example = "2", required = true)
  private Integer feedbackDays;
  @ApiModelProperty(value = "是否启用自动审批，true-是，false-否(Def)", example = "true")
  private Boolean autoAudit;
  @ApiModelProperty(value = "质量反馈时是否可以选择图片，0-否，1-是(def)", example = "0")
  private int enableChooseImage;
  @ApiModelProperty(value = "质量反馈时间类型，0-根据收货时间限制(def)，1-根据发货时间限制", example = "0")
  private int timeLmtMode;
  @ApiModelProperty(value = "质量反馈商品选择范围，0-标记商品(def)，1-收货商品", example = "0")
  private int goodsScope;
}
