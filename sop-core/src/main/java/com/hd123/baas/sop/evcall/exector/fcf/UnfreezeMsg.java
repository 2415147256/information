package com.hd123.baas.sop.evcall.exector.fcf;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

@Getter
@Setter
public class UnfreezeMsg extends AbstractTenantEvCallMessage {
  private Type type;
  private String message;
  private String planId;
  private Date startTime;
  private Date endTime;
  private String storeCode;

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("type", type).append("message", message).append("planId", planId)
        .append("startTime", startTime).append("endTime", endTime).append("storeCode", storeCode).toString();
  }
}
