package com.hd123.baas.sop.remote.workwx.apply;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TemplateContent {
  private List<TemplateContentControl> controls = new ArrayList<>();

}
