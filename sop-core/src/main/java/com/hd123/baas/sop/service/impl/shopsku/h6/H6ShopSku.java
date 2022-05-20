package com.hd123.baas.sop.service.impl.shopsku.h6;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Getter
@Setter
public class H6ShopSku {
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "storegid", index = 0)
  private String shopId;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "gdgid", index = 2)
  private String skuGid;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "是否必定", index = 3)
  private String required;

  public Object[] toLine() {
    List<String> line = new ArrayList<>();
    line.add(this.shopId);
    line.add(this.skuGid);
    line.add(this.required);

    return line.toArray(new Object[0]);
  }
}
