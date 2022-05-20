package com.hd123.baas.sop.excel.job.userposition;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.hd123.baas.sop.excel.common.AbstractBean;
import com.hd123.baas.sop.excel.common.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PositionBean extends AbstractBean {
    @NotEmpty
    @ColumnWidth(value = 20)
    @ExcelProperty(value = "岗位代码")
    private String code;
    @NotEmpty
    @ColumnWidth(value = 20)
    @ExcelProperty(value = "岗位名称")
    private String name;
}
