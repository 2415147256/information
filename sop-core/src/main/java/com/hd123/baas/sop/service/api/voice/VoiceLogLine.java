package com.hd123.baas.sop.service.api.voice;

import java.util.Date;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.entity.UCN;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class VoiceLogLine extends TenantEntity {
  /** 代码 */
  private String requestId;
  /** 代码 */
  private String templateId;
  /** 门店 */
  private UCN shop;
  /** 被叫 */
  private String callee;
  /** 主叫 */
  private String caller;
  /** 内容 */
  private String content;
  /** 开始时间 */
  private Date startDate;
  /** 通话开始时间 */
  private Date gmtCreate;
  /** 结束时间 */
  private Date endDate;
  /** 通话时长 */
  private int duration;
  /** 状态 */
  private VoiceLogState state;
  /** 通话时长 */
  private String callId;
  private String errCode;
  private String errMsg;
  /** 创建时间 */
  private Date created;
  /** 更新时间 */
  private Date lastModified;
}
