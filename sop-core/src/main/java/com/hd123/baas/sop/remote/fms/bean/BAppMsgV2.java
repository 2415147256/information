package com.hd123.baas.sop.remote.fms.bean;


import com.fasterxml.jackson.databind.JsonNode;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.qianfan123.baas.common.entity.BEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class BAppMsgV2 extends BEntity {
  public static final String ORG_ID_EQUAL = "orgId:=";
  public static final String TARGET_TYPE_EQUAL = "targetType:=";
  public static final String SOURCE_TYPE_IN = "sourceOrdType:in";
  public static final String SOURCE_TYPE_NOT_EQUAL= "sourceOrdType:=";
  public static final String TARGET_ID_EQUAL = "targetId:=";
  public static final String TARGET_CODE_EQUAL = "targetCode:=";
  public static final String SEQ = "seq";
  public static final String CREATED = "created";
  public static final String READ_EQUAL = "read:=";

  @ApiModelProperty(value = "事务id")
  private String requestId;
  @ApiModelProperty(value = "消息类型", notes = "ANNOUNCEMENT 公告  NOTICE 通知，消息 ALERT 提醒")
  private String type;
  @ApiModelProperty(value = "点击动作")
  private String action;
  @ApiModelProperty(value = "动作信息", notes = "如果是url则为链接。如果是page则为path")
  private String actionInfo;
  @ApiModelProperty(value = "标题")
  private String title;
  @ApiModelProperty(value = "标签", required = false, example = "[\"退款\"]")
  private List<String> tags;
  @ApiModelProperty(value = "内容")
  private String content;
  @ApiModelProperty(value = "序号，排序序号")
  private int seq = 0;
  @ApiModelProperty("业务侧写")
  private JsonNode ext;
  @ApiModelProperty("来源ID")
  private String sourceId;
  @ApiModelProperty(value = "来源类型")
  private String sourceOrdType;
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
  @ApiModelProperty("目标名称")
  private String targetScope;
  @ApiModelProperty(value = "操作人信息")
  private OperateInfo operateInfo;

  @ApiModelProperty(value = "是否已读")
  private boolean read;
  @ApiModelProperty(value = "阅读人信息")
  private OperateInfo readInfo;
  @ApiModelProperty(value = "阅读app")
  private String readAppId;

  @ApiModelProperty(value = "创建信息")
  private OperateInfo createInfo;
  @ApiModelProperty(value = "最后修改人信息")
  private OperateInfo lastModifyInfo;


}
