package com.hd123.baas.sop.service.impl.menu;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author W.J.H.7
 * @since 2022-01-24
 */
@Getter
@Setter
public class DefMenu1 implements Serializable {
  private String code;
  private String path;
  private String title;
  private String icon;
  private List<DefMenu2> children;
}