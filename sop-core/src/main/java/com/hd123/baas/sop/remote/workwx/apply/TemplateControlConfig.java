package com.hd123.baas.sop.remote.workwx.apply;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Y.U.A.N
 */
@Getter
@Setter
public class TemplateControlConfig {
  private TemplateSelector selector;

  private TemplateDate date;

  private TemplateContact contact;

  private TemplateTable table;

  private TemplateAttendance attendance;
}
