package com.hd123.baas.sop.evcall;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 **/
@Getter
@Setter
public class AbstractEvCallMessage implements Serializable {
  private Date createDate;
  private String traceId;
}
