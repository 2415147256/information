package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import com.qianfan123.baas.config.api.annotation.BcOption;
import com.qianfan123.baas.config.api.field.ConfigEditor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "质量反馈")
public class FeedbackConfig {
  private static final String PREFIX = "feedback.";

  public static final String DEFAULT_POLICY = "auto";
  public static final String WORK_WX_POLICY = "workWx";

  @BcKey(name = "审批策略：自动确认(auto)/外部审批(企业微信:workWx)", editor = ConfigEditor.OPTION, options = {
      @BcOption(value = DEFAULT_POLICY, name = DEFAULT_POLICY),
      @BcOption(value = WORK_WX_POLICY, name = WORK_WX_POLICY)
  })
  private String checkPolicy = DEFAULT_POLICY;
}
