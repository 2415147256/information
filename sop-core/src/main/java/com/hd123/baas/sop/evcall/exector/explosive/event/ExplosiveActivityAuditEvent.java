package com.hd123.baas.sop.evcall.exector.explosive.event;

import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplosiveActivityAuditEvent {
  private ExplosiveActivity activity;
  private boolean debug;

  public ExplosiveActivityAuditEvent(ExplosiveActivity activity) {
    this.activity = activity;
  }
}
