package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TemplateControlProperty {
  private String control;
  private String id;
  private List<TemplateText> title;
  private List<TemplateText> placeholder;
  private Integer require;
  @JsonProperty("un_print")
  private Integer unPrint;
}
