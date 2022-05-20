package com.hd123.baas.sop.job.bean.basedata;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Setter
@Getter
public class PosPromDataDownloadTaskMsg extends AbstractTenantEvCallMessage {
  private String orgId;
}
