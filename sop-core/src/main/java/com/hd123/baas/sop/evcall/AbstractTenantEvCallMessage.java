package com.hd123.baas.sop.evcall;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Getter
@Setter
public class AbstractTenantEvCallMessage extends AbstractEvCallMessage {
  private String tenant;
}
