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
public class H6ShopSalePrice {
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "门店ID", index = 1)
  private String shopId;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "商品ID", index = 2)
  private String skuId;
  @ColumnWidth(value = 25)
  @ExcelProperty(value = "生效开始日期", index = 3)
  private String effectiveStartDate;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "售价", index = 4)
  private BigDecimal salePrice;

  public Object[] toLine() {
    List<String> line = new ArrayList<>();
    line.add(this.shopId);
    line.add(this.skuId);
    line.add(this.effectiveStartDate);
    line.add(SopUtils.toString(this.salePrice));
    return line.toArray(new Object[0]);
  }
}
