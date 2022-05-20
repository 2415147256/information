package com.hd123.baas.sop.remote.dingtalk;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class DingTalkMessageResponse {
  private String request_id;
  private int errcode;
  private String errmsg;
  private UserGetbymobileResponse result;
  private String task_id;
}
