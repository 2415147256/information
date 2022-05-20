/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	FeedbackFeeGenerateTime.java
 * 模块说明：
 * 修改历史：
 * 2020/11/3 - Leo - 创建。
 */

package com.hd123.baas.sop.remote.rsh6sop.feedback;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Leo
 */
@Data
@ApiModel(description = "费用生成时间配置")
public class RsH6FeedbackFeeGenerateTime implements Serializable {
  private static final long serialVersionUID = 122855120613634263L;
  private Date feeGenerateTime;
}
