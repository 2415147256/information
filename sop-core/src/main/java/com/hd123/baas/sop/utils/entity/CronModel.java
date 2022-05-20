package com.hd123.baas.sop.utils.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronModel {

  /**
   * 所选作业类型: single,daily,weekly,monthly;
   */
  CronType cronType;

  /** 类型的哪几天 */
  Integer[] dayOf = new Integer[1];


}