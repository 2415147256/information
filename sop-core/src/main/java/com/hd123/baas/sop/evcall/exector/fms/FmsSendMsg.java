package com.hd123.baas.sop.evcall.exector.fms;

import com.hd123.baas.sop.evcall.AbstractEvCallMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FmsSendMsg extends AbstractEvCallMessage {
  private String tenant;
  /**
   * 消息模板id
   */
  private String templateId;

  /**
   * 模板参数
   */
  private Map<String, String> templateParams = new HashMap<>();

  /**
   * 目标对象
   */
  private List<String> target;

}
