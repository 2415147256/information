/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	TaskExportData
 * 模块说明：
 * 修改历史：
 * 2019/10/19 - chenhanwen - 创建。
 */
package com.hd123.baas.sop.evcall.exector.shopsku;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author 文
 *
 */
@Getter
@Setter
public class TaskExportData {
  private BigDecimal step;
  private BigDecimal addStep = BigDecimal.ZERO;
  private BigDecimal total;
  private BigDecimal process = BigDecimal.ZERO;
  private String taskId;
}
