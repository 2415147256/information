/**
 * 版权所有(C),上海海鼎信息工程股份有限公司,2020,所有权利保留。
 * <p>
 * 项目名: sop-pms-parent
 * 文件名: SkuExcelListen.java
 * 模块说明:
 * 修改历史:
 * 2020年11月09日 - wushuaijun- 创建
 */
package com.hd123.baas.sop.excel.common;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.qianfan123.baas.common.BaasException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Slf4j
public abstract class ImportExcelListener<T> extends AnalysisEventListener<T> {

  private int batchCount = 100;
  private List<T> excelRows = new ArrayList<>();

  public ImportExcelListener() {
  }

  public ImportExcelListener(int batchCount) {
    this.batchCount = batchCount;
  }

  @SneakyThrows
  @Override
  public void invoke(T row, AnalysisContext analysisContext) {
    excelRows.add(row);
    if (excelRows.size() == batchCount) {
      processData(this.excelRows);
    }
  }

  @SneakyThrows
  @Override
  public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    if (this.excelRows.isEmpty() == false) {
      processData(this.excelRows);
    }
  }

  public abstract void processData(List<T> excelRows) throws BaasException;
}
