package com.hd123.baas.sop.jmzs.shopdailysale.api;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ShopDailySaleInfoLine {


  private String accountClassification;
  private List<ShopDailySaleInfoLineDetail> details = new ArrayList<>();

  @Getter
  @Setter
  public static class ShopDailySaleInfoLineDetail {
    private String detailName;
    private BigDecimal amount;
  }
}
