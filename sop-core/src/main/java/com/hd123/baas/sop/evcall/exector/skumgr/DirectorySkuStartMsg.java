package com.hd123.baas.sop.evcall.exector.skumgr;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class DirectorySkuStartMsg extends AbstractTenantEvCallMessage {

  /**
   * 指定待计算的日期
   */
  private Date executeDate;
  // 任务id
  private String taskId;
  // 组织id
  private String orgId;
}
