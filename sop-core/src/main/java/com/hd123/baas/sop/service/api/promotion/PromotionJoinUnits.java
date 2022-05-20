package com.hd123.baas.sop.service.api.promotion;

import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class PromotionJoinUnits {
  @ApiModelProperty("全部门店")
  private Boolean allUnit;
  @ApiModelProperty("门店列表")
  private List<JoinUnit> stores;

  public List<JoinUnit> getStores() {
    return allUnit == Boolean.FALSE ? stores : null;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class JoinUnit extends UCN {
    public JoinUnit() {
    }

    public JoinUnit(String uuid, String code, String name) {
      super(uuid, code, name);
    }

    private String joinUnitType;
  }
}
