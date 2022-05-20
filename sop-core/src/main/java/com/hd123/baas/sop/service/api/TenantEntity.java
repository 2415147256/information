package com.hd123.baas.sop.service.api;

import com.hd123.rumba.commons.biz.entity.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Getter
@Setter
public class TenantEntity extends Entity {
  /**
   * 租户id
   */
  private String tenant;

}
