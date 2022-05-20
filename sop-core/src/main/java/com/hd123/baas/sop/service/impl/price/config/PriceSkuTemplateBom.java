package com.hd123.baas.sop.service.impl.price.config;

import java.math.BigDecimal;
import java.util.List;

import com.hd123.baas.sop.service.api.price.SkuDefine;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Setter
@Getter
public class PriceSkuTemplateBom {

  private String gdGid;
  // 商品定义
  private SkuDefine skuDefine = SkuDefine.NORMAL;
  // 原料品id
  private String raw;
  private List<PriceSkuTemplateBom.PriceSkuTemplateBomLine> finish;

  @Setter
  @Getter
  public static class PriceSkuTemplateBomLine {
    private String gdGid;
    // 比例
    private BigDecimal rate;
  }
}
