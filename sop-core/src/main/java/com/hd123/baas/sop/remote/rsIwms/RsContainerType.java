package com.hd123.baas.sop.remote.rsIwms;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Getter
@Setter
public class RsContainerType implements Serializable {

  private String uuid;
  private String code;
  private String name;
}
