package com.hd123.baas.sop.service.api.electricscale;

import java.util.List;

public interface ShopElecScaleTemplateService {

  /**
   * 通过电子秤模板id获取门店电子秤关系
   * 
   * @param tenant
   *          租户
   * @param template
   *          模板id
   * @return
   */
  List<ShopElecScaleTemplate> getByElecScaleTemplate(String tenant, String template);

  /**
   * 根据电子秤模板id获取门店电子秤模板
   * 
   * @param tenant
   * @param templates
   * @return
   */
  List<ShopElecScaleTemplate> listShopElecScaleTemplate(String tenant, List<String> templates);

  /**
   * 根据门店code获取门店电子秤模板
   * @param tenant
   * @param shopCode
   * @return
   */
  List<ShopElecScaleTemplate> listByShopCode(String tenant, String shopCode);
}
