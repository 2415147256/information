package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Getter
@Setter
@BcGroup(name = "fms")
public class FmsConfig {

  public static final String CONTENT = "content";

  @BcKey(name = "消息中心发布消息模板id")
  private String acPushTemplateId;

  @BcKey(name = "pms促销单下发通知POS的消息目标ID")
  private String sqlDataDownloadTaskPushTemplateId;

}
