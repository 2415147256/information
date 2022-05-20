package com.hd123.baas.sop.service.api.voice;

/**
 * @author W.J.H.7
 */
public enum VoiceLogState {
  INIT, // 未呼叫
  CALLED, // 已呼叫
  FAIL, // 呼叫失败
  // SUCCESS, // 呼叫成功
  SUCCESS_ANSWERED, // 呼叫成功&已接听
  SUCCESS_UN_ANSWERED, // 呼叫成功&未接听
  SUCCESS_REJECTED, // 呼叫成功&已拒绝
}
