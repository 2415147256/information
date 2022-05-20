package com.hd123.baas.sop.remote.dingtalk;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class DingTalkMessageRequest {
  private String agent_id;
  // user123,user456
  private String userid_list;
  private JSONObject msg;

  public class Msg {
    private String msgType;
    private Text text;

    public class Text {
      private String content;
    }
  }

}
