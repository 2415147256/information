package com.hd123.baas.sop.service.api.electricscale;

import com.qianfan123.baas.common.BaasException;

public interface ElecScaleKeyboardService {

  /**
   * 获取电子秤键盘主体信息
   * @param tenant
   * @param elecScaleKeyBoard
   * @return
   */
  ElecScaleKeyboard get(String tenant, String elecScaleKeyBoard) throws BaasException;

}
