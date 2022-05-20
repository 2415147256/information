package com.hd123.baas.sop.remote.workwx.apply;

import lombok.Data;

import java.util.List;

/**
 * @author Y.U.A.N
 */
@Data
public class TableChildrenDetail {
  private String control;
  private String id;
  private List<TemplateText> title;
  private ApplyFormControl value;
}
