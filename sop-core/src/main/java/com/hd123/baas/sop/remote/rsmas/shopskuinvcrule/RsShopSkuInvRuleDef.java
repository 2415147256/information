package com.hd123.baas.sop.remote.rsmas.shopskuinvcrule;

import com.hd123.baas.sop.remote.rsmas.RsParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 规则内容定义
 *
 * @author qyh
 */
@Setter
@Getter
@ApiModel("规则内容定义")
public class RsShopSkuInvRuleDef implements Serializable {
  private static final long serialVersionUID = -7130166136884318815L;

  /** 库存规则明细，开始生效日期*/
  public final static String PARAMETER_NAME_START_DATE = "startDate";

  @ApiModelProperty(value = "参数")
  private List<RsParameter> parameters = new ArrayList<>();
}
