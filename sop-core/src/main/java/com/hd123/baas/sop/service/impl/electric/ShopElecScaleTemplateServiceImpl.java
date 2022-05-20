package com.hd123.baas.sop.service.impl.electric;

import com.hd123.baas.sop.service.api.electricscale.ShopElecScaleTemplate;
import com.hd123.baas.sop.service.api.electricscale.ShopElecScaleTemplateService;
import com.hd123.baas.sop.service.dao.electricscale.ShopElecScaleTemplateDaoBof;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author youjiawei
 */
@Service
public class ShopElecScaleTemplateServiceImpl implements ShopElecScaleTemplateService {
  @Autowired
  ShopElecScaleTemplateDaoBof dao;

  @Override
  public List<ShopElecScaleTemplate> getByElecScaleTemplate(String tenant, String template) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(template, "template");
    return dao.getByElecScaleTemplate(tenant, template);
  }

  @Override
  public List<ShopElecScaleTemplate> listShopElecScaleTemplate(String tenant, List<String> templates) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(templates);
    return dao.listByTemplates(tenant, templates);
  }

  @Override
  public List<ShopElecScaleTemplate> listByShopCode(String tenant, String shopCode) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopCode, "shopCode");
    return dao.listByShopCode(tenant, shopCode);
  }
}
