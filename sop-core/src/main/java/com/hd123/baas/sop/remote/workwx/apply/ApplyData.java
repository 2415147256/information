
package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hd123.baas.sop.remote.workwx.deserializer.ApplyFormControlDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ApplyData {

  // value = "审批申请详情"
  @JsonDeserialize(using = ApplyFormControlDeserializer.class)
  private List<ApplyDataContent> contents = new ArrayList<>();
}