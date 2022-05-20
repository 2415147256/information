package com.hd123.baas.sop.remote.workwx.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hd123.baas.sop.remote.workwx.apply.TemplateContent;
import com.hd123.baas.sop.remote.workwx.apply.TemplateText;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Y.U,A.N
 */
@Getter
@Setter
public class WorkApplyTemplateResponse extends BaseWorkWxResponse {

  @JsonProperty("template_names")
  private List<TemplateText> templateNames;

  @JsonProperty("template_content")
  private TemplateContent content;

}
