package com.hd123.baas.sop.remote.fms.bean;


import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.qianfan123.baas.common.entity.BEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhengzewang on 2020/11/9.
 */
@Getter
@Setter
public class BAppMessage extends BEntity {

  @ApiModelProperty(value = "租户")
  private String tenant;
  @ApiModelProperty(value = "组织ID")
  private String orgId;
  @ApiModelProperty(value = "门店")
  private String shop;
  @ApiModelProperty(value = "门店")
  private String shopCode;
  @ApiModelProperty(value = "门店")
  private String shopName;
  @ApiModelProperty(value = "创建时间")
  private Date created;
  @ApiModelProperty(value = "消息类型")
  private String type;
  @ApiModelProperty(value = "点击动作")
  private String action;
  @ApiModelProperty(value = "动作信息", notes = "如果是url则为链接。如果是page则为path")
  private String actionInfo;
  @ApiModelProperty(value = "动作对像", notes = "如果action为media，则使用该对像")
  private MediaInfo mediaInfo;
  @ApiModelProperty(value = "标签")
  private String tag;
  @ApiModelProperty("应用")
  private String appId;

  @ApiModelProperty(value = "标题")
  private String title;
  @ApiModelProperty(value = "正文", notes = "key取值有：TEXT(文本),IMAGE(图片),URL(链接)")
  private Map<String, String> content = new LinkedHashMap<>();
  @ApiModelProperty(value = "是否已读")
  private boolean read;
  @ApiModelProperty(value = "序号", notes = "查询结果默认按此倒序")
  private Long seq;
  @ApiModelProperty(value = "图片", notes = "查询结果默认按此倒序")
  private List<String> images = new ArrayList<>();

  @ApiModelProperty(value = "用户ID")
  private String userId;
  @ApiModelProperty(value = "是否发布到收银机")
  private boolean sendPos;

  @ApiModelProperty(value = "创建信息")
  private OperateInfo createInfo;
  @ApiModelProperty(value = "最后修改人信息")
  private OperateInfo lastModifyInfo;
  @ApiModelProperty("业务ID")
  private String source;
  @ApiModelProperty("待办配置ID")
  private String sourceTypeId;
  @ApiModelProperty("业务侧写")
  private BAppMessageExt ext;
  @ApiModelProperty(value = "接受人")
  private String userName;
  @ApiModelProperty(value = "状态")
  private String state = "SUCCESS";
  @ApiModelProperty(value = "错误原因")
  private String errorMessage = "";

  @ApiModelProperty(value = "阅读人信息")
  private OperateInfo readInfo;
  @ApiModelProperty(value = "阅读app")
  private String readAppId;
}
