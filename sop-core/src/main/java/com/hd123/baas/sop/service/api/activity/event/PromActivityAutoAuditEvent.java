package com.hd123.baas.sop.service.api.activity.event;

import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
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
public class PromActivityAutoAuditEvent {
  private PromActivity activity;
}
