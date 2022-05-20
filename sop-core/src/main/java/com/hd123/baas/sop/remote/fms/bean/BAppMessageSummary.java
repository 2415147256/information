package com.hd123.baas.sop.remote.fms.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author zyt
 */
@Getter
@Setter
public class BAppMessageSummary {

  @ApiModelProperty(value = "未读信息数量")
  private Map<String, Integer> unread;
  @ApiModelProperty(value = "所有未读数量")
  private int unreadTotal;

}
