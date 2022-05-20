/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	FeedbackBean.java
 * 模块说明：
 * 修改历史：
 * 2020/12/3 - Leo - 创建。
 */

package com.hd123.baas.sop.excel.job.feedback;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.hd123.baas.sop.excel.common.AbstractBean;
import com.hd123.baas.sop.excel.common.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author Leo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackBean extends AbstractBean {
  @NotEmpty
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "门店代码")
  private String storeCode;
  @NotEmpty
  @ColumnWidth(value = 20)
  @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
  @ExcelProperty(value = "到货日期")
  private Date deliveryTime;
  @NotEmpty
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "商品代码")
  private String gdCode;
  @NotEmpty
  @ColumnWidth(value = 20)
  @NumberFormat(value = "0.####", roundingMode = RoundingMode.HALF_UP)
  @ExcelProperty(value = "申请数量")
  private BigDecimal qty;
  @NotEmpty
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "申请原因")
  private String applyReason;
}
