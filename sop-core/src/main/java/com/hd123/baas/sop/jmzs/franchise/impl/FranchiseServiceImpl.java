package com.hd123.baas.sop.jmzs.franchise.impl;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.jmzs.franchise.api.Franchise;
import com.hd123.baas.sop.jmzs.franchise.api.FranchiseService;
import com.hd123.baas.sop.jmzs.franchise.api.FranchiseShopAssignment;
import com.hd123.baas.sop.jmzs.franchise.dao.FranchiseDao;
import com.hd123.baas.sop.jmzs.franchise.dao.FranchiseShopAssignmentDao;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.Converter;
import com.hd123.rumba.commons.util.converter.ConverterBuilder;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FranchiseServiceImpl implements FranchiseService {
  @Autowired
  private FranchiseDao franchiseDao;
  @Autowired
  private FranchiseShopAssignmentDao franchiseShopAssignmentDao;

  public static final Converter<FranchiseShopAssignment, UCN> CONVERTER = ConverterBuilder //
      .newBuilder(FranchiseShopAssignment.class, UCN.class) //
      .map("shopId","uuid")
      .map("shopCode","code")
      .map("shopName","name")
      .build();
  @Override
  public QueryResult<Franchise> query(String tenant, QueryDefinition qd, String... fetchParts) {


    Assert.notNull(qd);
    Assert.hasText(tenant);

    QueryResult<Franchise> query = franchiseDao.query(tenant, qd);
    if (fetchParts == null || fetchParts.length == 0) {
      return query;
    }
    fetchParts(tenant, query.getRecords(), fetchParts);
    return query;
  }



  @Override
  public Franchise get(String tenant, String uuid) {

    Assert.hasText(tenant);
    Assert.hasText(uuid);

    Franchise franchise = franchiseDao.get(tenant, uuid);
    fetchParts(tenant, Collections.singletonList(franchise), FranchiseService.ALL_PARTS);
    return franchise;
  }

  @Override
  public Franchise getById(String tenant, String orgId, String id) {
    Assert.hasText(tenant);
    Assert.hasText(orgId);
    Assert.hasText(id);
    Franchise franchise = franchiseDao.get(tenant, orgId, id);
    fetchParts(tenant, Collections.singletonList(franchise), FranchiseService.ALL_PARTS);
    return franchise;
  }

  @Override
  @Tx
  public String saveNew(String tenant, Franchise franchise, OperateInfo operateInfo) throws Exception {
    Assert.notNull(franchise);
    Assert.hasText(tenant);
    Assert.notNull(operateInfo);

    buildForSaveNew(franchise, operateInfo);
    franchise.setTenant(tenant);
    franchiseDao.saveNew(franchise);

    saveShop(tenant, franchise, operateInfo);

    return franchise.getUuid();
  }



  @Override
  @Tx
  public void batchSave(String tenant, List<Franchise> franchises, OperateInfo operateInfo) throws Exception {
    Assert.notEmpty(franchises);
    Assert.hasText(tenant);
    Assert.notNull(operateInfo);

    List<String> ids = franchises.stream().map(Franchise::getId).collect(Collectors.toList());
    List<Franchise> records = franchiseDao.listById(tenant, ids);

    Map<String, Franchise> franchiseMap = records.stream()
        .collect(Collectors.toMap(e -> e.getId() + e.getOrgId(), i -> i));
    List<Franchise> updates = new ArrayList<>();
    List<Franchise> inserts = new ArrayList<>();
    for (Franchise franchise : franchises) {
      String key = franchise.getId() + franchise.getOrgId();
      if (franchiseMap.containsKey(key)) {
        // 更新
        Franchise record = franchiseMap.get(key);
        convertUpdate(franchise, record);
        buildForUpdate(record, operateInfo);
        updates.add(record);
        continue;
      }
      // 新增
      franchise.setTenant(tenant);
      buildForSaveNew(franchise,operateInfo);
      inserts.add(franchise);

    }

    if (CollectionUtils.isNotEmpty(updates)) {
      franchiseDao.batchUpdate(updates);
    }
    if (CollectionUtils.isNotEmpty(inserts)) {
      franchiseDao.batchSaveNew(inserts);
    }

  }

  @Override
  @Tx
  public void saveModify(String tenant, Franchise franchise, OperateInfo operateInfo) throws Exception {
    Assert.notNull(franchise);
    Assert.hasText(tenant);
    Assert.hasText(franchise.getUuid());
    Assert.notNull(operateInfo);

    Franchise record = franchiseDao.get(tenant, franchise.getUuid());
    if (record == null) {
      throw new BaasException("数据不存在或已被标记删除");
    }
    convertUpdate(franchise, record);
    buildForUpdate(record,operateInfo);
    franchiseDao.update(record);

    saveShop(tenant, franchise, operateInfo);

  }

  private void convertUpdate(Franchise update, Franchise record) {
    record.setExt(update.getExt());
    record.setCode(update.getCode());
    record.setName(update.getName());
    record.setCreateDate(update.getCreateDate());
    record.setStatus(update.getStatus());
    record.setContractImages(update.getContractImages());
    record.setMobile(update.getMobile());
    record.setPosition(update.getPosition());
    record.setDeleted(update.getDeleted());

  }

  private void saveShop(String tenant, Franchise franchise, OperateInfo operateInfo) {
    franchiseShopAssignmentDao.deleteByFranchiseUuid(tenant, franchise.getUuid());
    if (CollectionUtils.isNotEmpty(franchise.getShops())) {
      List<FranchiseShopAssignment> shopAssignments = new ArrayList<>();
      for (UCN shop : franchise.getShops()) {
        FranchiseShopAssignment franchiseShopAssignment = new FranchiseShopAssignment();
        franchiseShopAssignment.setShopId(shop.getUuid());
        franchiseShopAssignment.setShopCode(shop.getCode());
        franchiseShopAssignment.setShopName(shop.getName());
        franchiseShopAssignment.setFranchiseId(franchise.getId());
        franchiseShopAssignment.setFranchiseName(franchise.getName());
        franchiseShopAssignment.setFranchiseCode(franchise.getCode());
        franchiseShopAssignment.setFranchiseUuid(franchise.getUuid());
        franchiseShopAssignment.setTenant(tenant);
        buildForSaveNew(franchiseShopAssignment, operateInfo);
        shopAssignments.add(franchiseShopAssignment);

      }
      franchiseShopAssignmentDao.batchSave(shopAssignments);
    }
  }

  private void fetchParts(String tenant, List<Franchise> records, String[] fetchParts) {
    records = records.stream().filter(Objects::nonNull).collect(Collectors.toList());
    if (CollectionUtils.isEmpty(records)) {
      return;
    }
    for (String fetchPart : fetchParts) {
      if (fetchPart.equals(FranchiseService.FETCH_SHOP)) {
        fetchShop(tenant, records);
      }
    }

  }


  private void fetchShop(String tenant, List<Franchise> records) {
    List<String> frachiseUuids = records.stream().map(Franchise::getUuid).collect(Collectors.toList());
    List<FranchiseShopAssignment> franchiseShopAssignments = franchiseShopAssignmentDao.listByFranchiseUuids(tenant, frachiseUuids);
    if (CollectionUtils.isEmpty(franchiseShopAssignments)) {
      return;
    }
    Map<String, List<FranchiseShopAssignment>> franchiseUuidShopsMap = franchiseShopAssignments.stream().collect(Collectors.groupingBy(FranchiseShopAssignment::getFranchiseUuid));
    for (Franchise record : records) {
      if (franchiseUuidShopsMap.containsKey(record.getUuid())) {
        List<FranchiseShopAssignment> shops = franchiseUuidShopsMap.get(record.getUuid());
        record.setShops(ConverterUtil.convert(shops, CONVERTER));
      }
    }
  }

  private void buildForUpdate(StandardEntity entity, OperateInfo operateInfo) {
    entity.setVersion(entity.getVersion() + 1);
    entity.setLastModifyInfo(operateInfo);
  }

  private void buildForSaveNew(StandardEntity entity, OperateInfo operateInfo) {
    entity.setVersion(0);
    entity.setCreateInfo(operateInfo);
    entity.setLastModifyInfo(operateInfo);
  }
}
