package com.hd123.baas.sop.service.impl.position;

import java.util.List;
import java.util.stream.Collectors;

import com.hd123.baas.sop.service.api.price.config.PriceSkuConfig;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfigService;
import com.hd123.rumba.commons.biz.query.Cop;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.LogRequestPraras;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.entity.SkuPosition;
import com.hd123.baas.sop.service.api.position.SkuPositionService;
import com.hd123.baas.sop.service.dao.postion.SkuPositionDaoBof;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

@Service
public class SkuPositionServiceImpl implements SkuPositionService {
  @Autowired
  private SkuPositionDaoBof skuPositionDao;
  @Autowired
  private PriceSkuConfigService skuConfigService;

  @Override
  @Tx
  @LogRequestPraras
  public void saveNew(String tenant, SkuPosition skuPosition) throws BaasException {
    Assert.notNull(skuPosition);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuPosition.getName(), "名称");
    SkuPosition query = skuPositionDao.queryByName(tenant, skuPosition.getName());
    if (query != null) {
      throw new BaasException("商品定位已存在");
    }
    skuPositionDao.insert(tenant, skuPosition);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void batchSaveNew(String tenant, List<SkuPosition> skuPositions) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(skuPositions);
    List<String> names = skuPositions.stream().map(SkuPosition::getName).collect(Collectors.toList());
    List<SkuPosition> existPositions = skuPositionDao.queryByNames(tenant,skuPositions.get(0).getOrgId(), names);
    if (CollectionUtils.isNotEmpty(existPositions)) {
      List<String> existNames = existPositions.stream().map(SkuPosition::getName).collect(Collectors.toList());
      throw new BaasException("商品定位已存在：" + BaasJSONUtil.safeToJson(existNames));
    }
    skuPositionDao.batchInsert(tenant, skuPositions);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void saveModify(String tenant, SkuPosition skuPosition) throws BaasException {
    Assert.notNull(skuPosition);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuPosition.getUuid(), "uuid");
    Assert.notNull(skuPosition.getName(), "名称");
    SkuPosition queryById = skuPositionDao.queryById(tenant, skuPosition.getUuid());
    if (queryById == null) {
      throw new BaasException("商品定位不存在");
    }
    skuPositionDao.update(tenant, skuPosition);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void batchDelete(String tenant, List<Integer> uuids) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(uuids, "uuids");
    QueryDefinition qd =  new QueryDefinition();
    List<String> uuidStr = uuids.stream().map(x -> x + "").collect(Collectors.toList());
    qd.addByField(PriceSkuConfig.Queries.SKU_POSITION, Cop.IN, uuidStr.toArray());
    QueryResult<PriceSkuConfig> priceSkuConfigQueryResult = skuConfigService.querySkuConfig(tenant, qd);
    if (priceSkuConfigQueryResult.getRecordCount() > 0){
      throw new BaasException("删除的定位存在商品，无法删除商品定位");
    }
    skuPositionDao.delete(tenant, uuids);
  }

  @Override
  @LogRequestPraras
  public List<SkuPosition> list(String tenant) {
    Assert.notNull(tenant, "租戶");
    return skuPositionDao.list(tenant, null);
  }
  @Override
  @LogRequestPraras
  public List<SkuPosition> list(String tenant,String orgId) {
    Assert.notNull(tenant, "租戶");
    return skuPositionDao.list(tenant,orgId);
  }

  @Override
  public QueryResult<SkuPosition> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "租戶");
    Assert.notNull(qd, "qd");
    return skuPositionDao.query(tenant, qd);
  }
}
