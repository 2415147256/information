package com.hd123.baas.sop.service.impl.menu;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author W.J.H.7
 * @since 2022-01-24
 */
@Getter
@Setter
public class DefMenu2 implements Serializable {
  private String code;
  private String path;
  private String title;
  private String icon;
}

