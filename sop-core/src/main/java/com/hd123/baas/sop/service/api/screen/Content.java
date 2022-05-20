package com.hd123.baas.sop.service.api.screen;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Content {
  /** ROTATION_IMG:轮播图 TIP_LANGUAGE:广告语 */
  private String type;
  /** 轮播图url或广告语的JSON */
  private List<String> cnts;
}
