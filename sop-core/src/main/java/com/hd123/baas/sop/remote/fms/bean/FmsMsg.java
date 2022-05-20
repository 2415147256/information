package com.hd123.baas.sop.remote.fms.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang
 */
@Getter
@Setter
public class FmsMsg {
  public static final String ANDROID_APP_KEY = "androidAppKey";
  public static final String IOS_APP_KEY = "iosAppKey";
  public static final String TARGET = "Target";

  @ApiModelProperty("应用id")
  private String appId;
  @ApiModelProperty(value = "接收者")
  private List<String> target = new ArrayList<>();
  @ApiModelProperty(value = "消息组id", required = false,
      notes = "当此值不为空时，表示这个消息的接收目标可能是大于1个；在任意一个目标标识已读此消息后，其他目标的消息也同步标记已读")
  private String groupId;
  @ApiModelProperty(value = "模板id")
  private String templateId;
  @ApiModelProperty("模板参数")
  private Map<String, String> templateParams = new HashMap<>();
  @ApiModelProperty(value = "参数", notes = "非模板参数，一般用于消息推送透传信息")
  private Map<String, String> params = new HashMap<>();
  /**
   * {@link TemplateMessage##ANDROID_APP_KEY } <br/>
   * {@link TemplateMessage##IOS_APP_KEY } <br/>
   * {@link TemplateMessage##TARGET } <br/>
   */
  @ApiModelProperty(value = "额外业务参数。针对不同的消息系统定义解析规则。", notes = "对于阿里云推送：key:Target，Target默认为ACCOUNT，可以通过该参数指定其他的\n")
  private Map<String, String> ext;

  @ApiModelProperty(value = "发送渠道和身份标识", notes = "示例 {\n" + "  \"aliyunNotifySender\":{\n" + "    \"jack\":\"a001\",\n"
      + "    \"mary\":\"a002\",\n" + "  },\n" + "  \"webSocketSender\":{\n" + "    \"jack\":\"w001\",\n"
      + "    \"mary\":\"w002\",\n" + "  }\n"
      + "} 。当此消息需要通过多个发送渠道进行推送时，需要传此参数。sender当前支持aliyunNotifySender阿里云消息推送、webSocketSender长连接推送、defaultEmailSender邮件发送")
  private Map<String, Map<String, String>> senderAndUserIdents = new HashMap<>();

  @JsonIgnore
  public FmsMsg userIdents(Sender sender, Map<String, String> userIdentMap) {
    if (senderAndUserIdents == null) {
      this.senderAndUserIdents = new HashMap<>();
    }
    senderAndUserIdents.put(sender.name(), userIdentMap);
    return this;
  }

  public enum Sender {
    webSocketSender
  }

  public static class Topic {
    public static final String SOP_PMS_SYNC = "sop.pms.sync";
  }
}
