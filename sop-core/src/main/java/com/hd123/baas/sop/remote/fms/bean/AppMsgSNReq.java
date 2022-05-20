package com.hd123.baas.sop.remote.fms.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author W.J.H.7
 */
@ApiModel("新建消息请求")
@Setter
@Getter
public class AppMsgSNReq {
  @ApiModelProperty(value = "事务ID，幂等性校验，如果为空，系统会自动生成一个", example = "A1123344")
  private String requestId;
  @ApiModelProperty(value = "消息类型", notes = "ANNOUNCEMENT 公告  NOTICE 通知，消息 ALERT 提醒")
  private String type;
  @ApiModelProperty(value = "点击动作")
  private String action;
  @ApiModelProperty(value = "动作信息", notes = "如果是url则为链接。如果是page则为path")
  private String actionInfo;
  @ApiModelProperty(value = "标题")
  private String title;
  @ApiModelProperty(value = "内容")
  private String content;
  @ApiModelProperty(value = "序号，排序序号")
  private int seq = 0;
  @ApiModelProperty(value = "标签")
  private List<String> tags;
  @ApiModelProperty("业务侧写")
  private JsonNode ext;
  @ApiModelProperty("来源单据类型")
  private String sourceOrdType;
  @ApiModelProperty("来源ID")
  private String sourceId;
  @ApiModelProperty("来源APP")
  private String sourceAppId;
  @ApiModelProperty("目标类型")
  private String targetType;
  @ApiModelProperty("目标Id")
  private String targetId;
  @ApiModelProperty("目标代码")
  private String targetCode;
  @ApiModelProperty("目标名称")
  private String targetName;
  @ApiModelProperty("目标范围")
  private String targetScope;
  @ApiModelProperty(value = "操作人信息")
  private OperateInfo operateInfo;
}