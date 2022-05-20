package com.hd123.baas.sop.service.impl.price.shopprice.h6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import com.hd123.baas.sop.utils.SopUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Getter
@Setter
public class H6BasePrice {
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "商品gid", index = 0)
  private String goodsId;
  @ColumnWidth(value = 25)
  @ExcelProperty(value = "生效开始日期", index = 1)
  private String effectiveStartDate;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "基础到店价", index = 2)
  private BigDecimal basePrice;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "组织ID", index = 3)
  private String orgId;
  public Object[] toLine() {
    List<String> line = new ArrayList<>();
    line.add(this.goodsId);
    line.add(this.effectiveStartDate);
    line.add(SopUtils.toString(this.basePrice));
    line.add(this.getOrgId());
    return line.toArray(new Object[0]);
  }

}
