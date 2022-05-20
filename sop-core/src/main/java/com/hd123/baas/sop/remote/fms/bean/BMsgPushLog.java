package com.hd123.baas.sop.remote.fms.bean;

import com.hd123.rumba.commons.biz.entity.Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class BMsgPushLog extends Entity {
  @ApiModelProperty("租户")
  private String tenant;
  @ApiModelProperty("调用者")
  private String outId;
  @ApiModelProperty("目标者")
  private String callee;
  @ApiModelProperty("模板id")
  private String templateId;
  @ApiModelProperty("模板参数")
  private String templatePrams;
  @ApiModelProperty("内容")
  private String context;
  @ApiModelProperty("请求额外字段")
  private String ext;
  @ApiModelProperty("状态")
  private String state;
  @ApiModelProperty("调用回执id")
  private String callId;
  @ApiModelProperty("调用回执内容")
  private String callExt;
  @ApiModelProperty("调用返回代码")
  private String retCode;
  @ApiModelProperty("调用返回异常内容")
  private String retMsg;
  @ApiModelProperty("创建时间")
  private Date created;
  @ApiModelProperty("最后修改时间")
  private Date lastModified;
}