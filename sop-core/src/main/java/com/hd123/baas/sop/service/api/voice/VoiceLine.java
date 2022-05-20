package com.hd123.baas.sop.service.api.voice;

import java.util.Map;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.entity.UCN;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class VoiceLine extends TenantEntity {

  /** 所属id */
  private String owner;
  /** 门店 */
  private UCN shop;
  /** 被叫 */
  private String callee;
  /** 代码 */
  private Map<String, String> templateParams;

}
