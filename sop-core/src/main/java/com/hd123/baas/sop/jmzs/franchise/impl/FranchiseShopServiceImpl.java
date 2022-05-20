package com.hd123.baas.sop.jmzs.franchise.impl;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.jmzs.franchise.api.FranchiseShopAssignment;
import com.hd123.baas.sop.jmzs.franchise.api.FranchiseShopService;
import com.hd123.baas.sop.jmzs.franchise.dao.FranchiseShopAssignmentDao;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FranchiseShopServiceImpl implements FranchiseShopService {


  @Autowired
  private FranchiseShopAssignmentDao dao;

  @Override
  @Tx
  public void save(String tenant, List<FranchiseShopAssignment> franchiseShops, OperateInfo operateInfo) {
    Assert.notEmpty(franchiseShops);
    Assert.hasText(tenant);
    Assert.notNull(operateInfo);

    franchiseShops = franchiseShops.stream().filter(i -> i.getFranchiseUuid() != null).collect(Collectors.toList());

    List<String> franchiseUuids = franchiseShops.stream()
        .map(FranchiseShopAssignment::getFranchiseUuid)
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(franchiseUuids)) {
      return;
    }
    dao.deleteByFranchiseUuids(tenant, franchiseUuids);
    franchiseShops.forEach(item->{
      item.setTenant(tenant);
      buildForSaveNew(item, operateInfo);
    });
    dao.batchSave(franchiseShops);

  }

  @Override
  public List<FranchiseShopAssignment> listByUuid(String tenant, String franchiseUuid) {
    Assert.hasText(tenant);
    Assert.hasText(franchiseUuid);
    return dao.listByFranchiseUuid(tenant, franchiseUuid);
  }

  private void buildForSaveNew(StandardEntity entity, OperateInfo operateInfo) {
    entity.setVersion(0);
    entity.setCreateInfo(operateInfo);
    entity.setLastModifyInfo(operateInfo);
  }
}
