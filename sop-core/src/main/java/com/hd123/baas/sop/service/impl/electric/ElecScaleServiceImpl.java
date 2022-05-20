package com.hd123.baas.sop.service.impl.electric;

import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.service.api.electricscale.ElecScale;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleService;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleState;
import com.hd123.baas.sop.service.api.electricscale.ShopElecScale;
import com.hd123.baas.sop.service.dao.electricscale.ElecScaleDaoBof;
import com.hd123.baas.sop.service.dao.electricscale.ShopElecScaleStateDaoBof;
import com.hd123.baas.sop.service.dao.electricscale.ShopElectricScaleDaoBof;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ElecScaleServiceImpl implements ElecScaleService {

  @Autowired
  private ShopElectricScaleDaoBof dao;
  @Autowired
  private ShopElecScaleStateDaoBof stateDao;

  @Autowired
  private StoreService storeService;

  @Autowired
  private ElecScaleDaoBof elecScaleDao;

  @Override
  public String saveNew(String tenant, ShopElecScale scale, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(scale, "scale");
    Assert.notNull(scale.getIp(), "ip");
    String ip = scale.getIp();

    StoreFilter filter = new StoreFilter();
    List<String> shop = new ArrayList<>();
    shop.add(scale.getShopCode());
    filter.setCodeIn(shop);
    filter.setOrgIdEq(scale.getOrgId());
    QueryResult<Store> store = storeService.query(tenant, filter);
    if (CollectionUtils.isEmpty(store.getRecords())) {
      throw new BaasException("门店不存在");
    }
    ShopElecScale daoResult = dao.getByIp(tenant, scale.getShopCode(), ip);
    if (daoResult != null) {
      throw new BaasException("IP不能重复");
    }
    if (scale.getUuid() != null) {
      scale.setUuid(UUID.randomUUID().toString());
    }
    dao.insert(tenant, scale, operateInfo);
    return scale.getUuid();
  }

  @Override
  public void saveModify(String tenant, ShopElecScale scale, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(scale, "scale");
    Assert.notNull(scale.getUuid(), "uuid");

    ShopElecScale elecScale = dao.get(tenant, scale.getUuid());
    if (elecScale == null) {
      throw new BaasException("电子秤不存在");
    }
    ShopElecScale daoResult = dao.getByIp(tenant, scale.getShopCode(), scale.getIp());
    if (daoResult != null && !daoResult.getUuid().equals(scale.getUuid())) {
      throw new BaasException("IP不能重复");
    }
    dao.update(tenant, scale, operateInfo);
  }

  @Override
  public ShopElecScale get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    return dao.get(tenant, uuid);
  }

  @Override
  public void delete(String tenant, String uuid) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    ShopElecScale elecScale = this.get(tenant, uuid);
    if (elecScale == null) {
      throw new BaasException("电子秤不存在");
    }
    dao.delete(tenant, uuid);
  }

  @Override
  public QueryResult<ShopElecScale> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "qd");
    return dao.query(tenant, qd);
  }

  @Override
  public List<ElecScaleState> stateListData(String tenant, List<String> uuids) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(uuids);

    return stateDao.queryLastDataState(tenant, uuids);
  }

  @Override
  public List<ElecScaleState> stateListTemplate(String tenant, List<String> uuids) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(uuids);
    return stateDao.queryLastTemState(tenant, uuids);
  }

  @Override
  public List<ElecScale> listElecScale(String tenant) {
    Assert.notNull(tenant);
    return elecScaleDao.query(tenant);
  }

  @Override
  public ShopElecScale getByShopCodeAndUUid(String tenant, String shopCode, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopCode, "门店code");
    Assert.notNull(uuid, "uuid");
    return dao.getByShopCodeAndUUid(tenant, shopCode, uuid);
  }

  @Override
  public List<ShopElecScale> listByShopCode(String tenant, String shopCode) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopCode, "门店code");

    return dao.getByShopCode(tenant, shopCode);
  }

}