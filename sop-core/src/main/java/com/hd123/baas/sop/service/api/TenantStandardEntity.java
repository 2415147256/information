package com.hd123.baas.sop.service.api;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Getter
@Setter
public class TenantStandardEntity extends StandardEntity {
  /**
   * 租户id
   */
  private String tenant;

}
