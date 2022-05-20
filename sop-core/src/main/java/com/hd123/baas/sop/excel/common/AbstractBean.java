package com.hd123.baas.sop.excel.common;

import org.apache.poi.ss.usermodel.Font;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/24.
 */
@Getter
@Setter
public abstract class AbstractBean {

  public static final int SUCCESS = 0;
  public static final int FAIL = 1;
  public static final int IGNORE = 2;

  @ColumnWidth(value = 50)
  @HeadFontStyle(color = Font.COLOR_RED)
  @ExcelProperty(value = "错误提示信息")
  private String feedbackMsg;

}
