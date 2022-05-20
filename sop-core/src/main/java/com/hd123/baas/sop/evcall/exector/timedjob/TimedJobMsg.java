package com.hd123.baas.sop.evcall.exector.timedjob;

import com.hd123.baas.sop.evcall.AbstractEvCallMessage;
import com.hd123.baas.sop.job.entity.TimedJob;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 * @author yanghaixiao
 **/
@Getter
@Setter
@Accessors(chain = true)
public class TimedJobMsg extends AbstractEvCallMessage {
  private TimedJob job;
}
