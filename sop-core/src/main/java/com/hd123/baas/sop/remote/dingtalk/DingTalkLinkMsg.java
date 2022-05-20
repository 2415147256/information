package com.hd123.baas.sop.remote.dingtalk;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class DingTalkLinkMsg {
  private String msgtype = "link";
  private Link link;

  @Setter
  @Getter
  public static class Link {
    private String messageUrl;
    private String picUrl;
    private String title;
    private String text;
  }
}
