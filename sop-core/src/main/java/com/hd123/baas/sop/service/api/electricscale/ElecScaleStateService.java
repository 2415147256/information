package com.hd123.baas.sop.service.api.electricscale;

import com.qianfan123.baas.common.BaasException;

import java.util.List;

public interface ElecScaleStateService {

  /**
   * 获取指定门店下的电子秤状态
   */
  List<ElecScaleState> listByShopCode(String tenant, String shopCode);

  /**
   * 同步电子秤状态
   */
  void syncState(String tenant, ElecScaleState states) throws BaasException;

}
