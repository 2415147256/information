package com.hd123.baas.sop.remote.rsh6sop.fineRule;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class FineRule {
  // 开启罚款功能，1-是，0-否
  private int enabled;
  // 组织ID
  private String orgUuid;
  //规则明细
  private List<FineRuleDetail> ruleDtls;
}
