package com.hd123.baas.sop.service.api.voice;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class VoiceLog extends TenantEntity {
  /** owner */
  private String voiceId;
  /** ownerLine */
  private String voiceLineId;
  /** 门店 */
  private UCN shop;
  /** 被叫 */
  private String callee;
  /** fms模板id */
  private String outTemplateId;
  /** 模板参数 */
  private String templateParas;

  /** CALLID */
  private String callId;

  /** 主叫 */
  private String caller;
  /** 开始时间 */
  private Date startDate;
  /** 通话开始时间 */
  private Date gmtCreate;
  /** 结束时间 */
  private Date endDate;
  /** 通话时长 */
  private int duration;
  private String errCode;
  private String errMsg;

  /** 状态 */
  private VoiceLogState state;
  /** 创建时间 */
  private Date created;
  /** 更新时间 */
  private Date lastModified;

  @QueryEntity(VoiceLog.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = VoiceLog.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String VOICE_ID = PREFIX + "voiceId";
    @QueryField
    public static final String CREATED = PREFIX + "created";
    @QueryField
    public static final String STATE = PREFIX + "state";
  }

}
