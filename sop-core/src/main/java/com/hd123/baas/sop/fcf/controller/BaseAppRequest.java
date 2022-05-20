package com.hd123.baas.sop.fcf.controller;

import java.io.Serializable;

import com.hd123.rumba.commons.biz.entity.UCN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel
public class BaseAppRequest implements Serializable {

  private static final long serialVersionUID = 4261587559144078534L;
  @ApiModelProperty("机号")
  private String posNo;
  @ApiModelProperty("操作人")
  private UCN operator;
}
