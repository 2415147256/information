package com.hd123.baas.sop.service.api.voice;

import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class Voice extends TenantEntity {
  /** 请求id */
  private String requestId;
  /** title */
  private String title;
  /** 模板id */
  private String templateId;
  /** 模板代码 */
  private VoiceTemplateCode templateCode;
  /** 模板内容 */
  private String templateContent;
  /** 创建时间 */
  private Date created;
  /** 明细 */
  private List<VoiceLine> lines;

  public static final String LINES = "lines";

  public static final String[] ALL_PARTS = {LINES};

  @QueryEntity(Voice.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = Voice.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String TEMPLATE_ID = PREFIX + "templateId";
    @QueryField
    public static final String TEMPLATE_CODE = PREFIX + "templateCode";
    @QueryField
    public static final String REQUEST_ID = PREFIX + "requestId";
    @QueryField
    public static final String CREATED = PREFIX + "created";
  }
}
