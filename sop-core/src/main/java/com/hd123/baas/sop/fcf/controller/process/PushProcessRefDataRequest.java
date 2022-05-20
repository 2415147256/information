package com.hd123.baas.sop.fcf.controller.process;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangweigang
 */
@Setter
@Getter
public class PushProcessRefDataRequest {
  @ApiModelProperty("智能制作参考数据")
  private List<ProcessRefData> list = new ArrayList<>();

  @ApiModelProperty(value = "业务日期，用于幂等，即一天只推送一次。对于智能制作来说，今晚推送的是明日的数据", example = "2021-03-31")
  private Date date;
}
