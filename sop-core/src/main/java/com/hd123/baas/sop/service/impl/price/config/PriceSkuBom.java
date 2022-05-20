package com.hd123.baas.sop.service.impl.price.config;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Setter
@Getter
public class PriceSkuBom {

  private String type;
  private List<BomFinishLine> finish;
  private List<BomRawLine> raw;

  @Setter
  @Getter
  public static class BomFinishLine {
    private String uuid;
    private String ownerUuid;
    private String line;
    private String note;
    private String gdGid;
    private BigDecimal qty;
    private BigDecimal costRatio;
    private BigDecimal lowTolerRance;
    private BigDecimal highTolerRance;
  }

  @Setter
  @Getter
  public static class BomRawLine {
    private String uuid;
    private String ownerUuid;
    private String line;
    private String note;
    private String gdGid;
    private BigDecimal qty;
    private BigDecimal saleRatio;
  }

  public static enum PriceSkuBomType {
    combination, splitByGrade, formula, multiQpc, splitByPart
  }
}
