package com.hd123.baas.sop.service.api.electricscale;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ElecScale extends TenantStandardEntity {
  private String name;
  private String manufacturer;
  private String model;
  private BigDecimal length;
  private BigDecimal width;

  @QueryEntity(ElecScale.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = ElecScale.class.getName() + "::";

  }
}
