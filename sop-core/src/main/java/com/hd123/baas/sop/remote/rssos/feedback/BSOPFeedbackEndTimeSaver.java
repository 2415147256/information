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
import java.util.List;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author panzhibin
 *
 */
@Data
@ApiModel(description = "质量反馈截止时间保存对象")
public class BSOPFeedbackEndTimeSaver implements Serializable {

  private static final long serialVersionUID = 519753281480480301L;

  @ApiModelProperty(value = "质量反馈截止时间列表", required = false)
  private List<FeedbackEndTime> endTimes;

}
