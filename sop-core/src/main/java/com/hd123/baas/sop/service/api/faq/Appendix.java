package com.hd123.baas.sop.service.api.faq;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class Appendix implements Serializable {
  /**
   * url类型, 外部链接EXTERNAL_LINK,内部图片 INSIDE_IMG
   */
  private String type;
  /**
   * url
   */
  private String url;
  /**
   * 扩展信息
   */
  private String ext;
}
