package com.hd123.baas.sop.remote.workwx.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateWorkWxReq {
  @JsonProperty(value = "template_id")
  private String templateId;
}
