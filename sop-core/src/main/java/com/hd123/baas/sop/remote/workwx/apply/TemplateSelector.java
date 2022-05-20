package com.hd123.baas.sop.remote.workwx.apply;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Y.U.A.N
 */
@Getter
@Setter
public class TemplateSelector {
  private String type;
  private List<TemplateOption> options;
}
