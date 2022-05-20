package com.hd123.baas.sop.evcall.exector.h6task;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhengzewang on 2020/11/24.
 */
@Getter
@Setter
public class H6TaskDeliveredMsg extends AbstractTenantEvCallMessage {

  // 任务id
  private String pk;
  /** 指定待计算的日期 */
  private Date executeDate;
}
