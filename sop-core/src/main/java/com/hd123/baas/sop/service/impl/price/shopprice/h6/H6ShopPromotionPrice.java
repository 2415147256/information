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
public class H6ShopPromotionPrice {

  @ColumnWidth(value = 20)
  @ExcelProperty(value = "门店ID", index = 0)
  private String shopId;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "商品gid", index = 1)
  private String goodsId;

  @ColumnWidth(value = 25)
  @ExcelProperty(value = "生效开始日期", index = 2)
  private String effectiveStartDate;
  @ColumnWidth(value = 25)
  @ExcelProperty(value = "生效结束日期", index = 3)
  private String effectiveEndDate;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "实际到店价", index = 4)
  private BigDecimal shopPrice;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "原价格", index = 5)
  private BigDecimal baseShopPrice;

  @ColumnWidth(value = 20)
  @ExcelProperty(value = "促销规则ID", index = 6)
  private String promotionUuid;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "订货限制金额", index = 7)
  private BigDecimal ordLimitAmount;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "订货限制数量", index = 8)
  private BigDecimal ordLimitQty;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "督导费用承担比例", index = 9)
  private String supervisorFavorSharing;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "总部费用承担比例", index = 10)
  private String headFavorSharing;
  @ColumnWidth(value = 20)
  @ExcelProperty(value = "组织ID", index = 11)
  private String orgId;

  public Object[] toLine() {
    List<String> line = new ArrayList<>();
    line.add(this.shopId);
    line.add(this.goodsId);
    line.add(this.effectiveStartDate);
    line.add(this.effectiveEndDate);
    line.add(SopUtils.toString(this.shopPrice));
    line.add(SopUtils.toString(this.baseShopPrice));
    line.add(this.promotionUuid);
    line.add(SopUtils.toString(this.ordLimitAmount));
    line.add(SopUtils.toString(this.ordLimitQty));
    line.add(this.supervisorFavorSharing);
    line.add(this.headFavorSharing);
    line.add(this.orgId);
    return line.toArray(new Object[0]);
  }
}
