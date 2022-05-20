package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author Y.U.A.N
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ApplyFormControl extends Serializable {

  /**
   * 控件名称
   *
   * @return 控件名称
   */
  String formControlName();
}

