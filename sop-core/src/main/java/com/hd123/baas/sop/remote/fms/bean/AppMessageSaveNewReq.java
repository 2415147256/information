package com.hd123.baas.sop.remote.fms.bean;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zyt
 */
@ApiModel("新建消息请求")
@Setter
@Getter
public class AppMessageSaveNewReq {

  @ApiModelProperty(value = "门店ID")
  private String shop;
  @ApiModelProperty(value = "门店name")
  private String shopName;
  @ApiModelProperty(value = "门店code")
  private String shopCode;
  @ApiModelProperty(value = "消息类型", notes = "  ANNOUNCEMENT 公告  NOTICE 通知，消息 ALERT 提醒")
  private String type;
  @ApiModelProperty(value = "点击跳转方式", notes = "PAGE:跳转到页面  URL:跳转到指定URL DETAIL:不跳转")
  private String jumpType;
  @ApiModelProperty(value = "跳转地址")
  private String jumpUrl;
  @ApiModelProperty(value = "标签")
  private String tag;
  @ApiModelProperty(value = "标题")
  private String title;
  @ApiModelProperty(value = "文本正文")
  private String textContent;
  @ApiModelProperty(value = "图片正文")
  private String imageContent;
  @ApiModelProperty(value = "url正文")
  private String urlContent;
  @ApiModelProperty(value = "接受人")
  private String userId;
  @ApiModelProperty("接受app")
  private String appId;
  @ApiModelProperty("发送到pos机")
  private boolean sendPos;
  @ApiModelProperty("操作人信息")
  private OperateInfo operateInfo;
  @ApiModelProperty("业务ID")
  private String source;
  @ApiModelProperty("待办配置ID")
  private String sourceTypeId;
  @ApiModelProperty("业务侧写")
  private BAppMessageExt ext;
  @ApiModelProperty(value = "接受人")
  private String userName;
  @ApiModelProperty(value = "动作对像", notes = "如果action为media，则使用该对像")
  private MediaInfo mediaInfo;
}