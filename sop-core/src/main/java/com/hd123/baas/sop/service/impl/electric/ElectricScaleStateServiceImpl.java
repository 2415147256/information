package com.hd123.baas.sop.service.impl.electric;

import com.hd123.baas.sop.service.api.electricscale.ShopElecScale;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleState;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleStateService;
import com.hd123.baas.sop.service.dao.electricscale.ShopElecScaleStateDaoBof;
import com.hd123.baas.sop.service.dao.electricscale.ShopElectricScaleDaoBof;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ElectricScaleStateServiceImpl implements ElecScaleStateService {

  @Autowired
  private ShopElecScaleStateDaoBof dao;

  @Autowired
  private ShopElectricScaleDaoBof electricScaleDao;

  @Override
  public List<ElecScaleState> listByShopCode(String tenant, String shopCode) {
    Assert.notNull(tenant,"租户");
    Assert.notNull(shopCode,"门店代码");
    return dao.listByShopCode(tenant,shopCode);
  }

  @Override
  public void syncState(String tenant, ElecScaleState state) throws BaasException {
    Assert.notNull(tenant,"租户");
    Assert.notNull(state,"state");
    ShopElecScale elecScale = electricScaleDao.get(tenant, state.getElectronicScaleUuid());
    if(elecScale == null){
      throw new BaasException("电子秤不存在");
    }
    if(state.getUuid() == null){
      state.setUuid(UUID.randomUUID().toString());
    }
    dao.insert(tenant, state);
  }
}
