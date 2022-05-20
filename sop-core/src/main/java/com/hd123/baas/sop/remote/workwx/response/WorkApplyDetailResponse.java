package com.hd123.baas.sop.remote.workwx.response;

import com.hd123.baas.sop.remote.workwx.apply.ApplyInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkApplyDetailResponse extends BaseWorkWxResponse {
  private ApplyInfo info;

}
