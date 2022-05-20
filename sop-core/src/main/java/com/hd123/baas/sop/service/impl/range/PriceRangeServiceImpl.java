package com.hd123.baas.sop.service.impl.range;

import java.util.List;
import java.util.stream.Collectors;

import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.LogRequestPraras;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.entity.PriceRange;
import com.hd123.baas.sop.service.api.range.PriceRangeService;
import com.hd123.baas.sop.service.dao.range.PriceRangeDaoBof;
import com.hd123.baas.sop.utils.CommonUtils;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

@Service
public class PriceRangeServiceImpl implements PriceRangeService {
  @Autowired
  private PriceRangeDaoBof priceRangeDao;

  @Override
  @Tx
  @LogRequestPraras
  public void saveNew(String tenant, PriceRange priceRange) throws BaasException {
    Assert.notNull(priceRange);
    Assert.notNull(tenant, "租戶");
    if (!CommonUtils.isInteger(priceRange.getName())) {
      throw new BaasException("价格带名称不为整数");
    }
    PriceRange query = priceRangeDao.queryByName(tenant, priceRange.getOrgId(), priceRange.getName());
    if (query != null) {
      throw new BaasException("新增价格带已存在");
    }
    initPriceRange(tenant,priceRange.getOrgId());
    priceRangeDao.insert(tenant, priceRange);
  }

  @Override
  public void batchSaveNew(String tenant, List<PriceRange> priceRanges) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(priceRanges);
    List<String> list = list(tenant, priceRanges.get(0).getOrgId()).stream()
        .map(PriceRange::getName)
        .collect(Collectors.toList());
    List<PriceRange> existRanges = priceRanges.stream().filter(p -> list.contains(p.getName())).collect(Collectors.toList());
    if (!existRanges.isEmpty()){
      throw new BaasException("价格带已存在");
    }
    priceRangeDao.batchInsert(tenant,priceRanges);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void saveModify(String tenant, PriceRange priceRange) throws BaasException {
    Assert.notNull(priceRange);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(priceRange.getUuid(), "uuid");
    Assert.notNull(priceRange.getName(), "名称");
    if (!CommonUtils.isInteger(priceRange.getName())) {
      throw new BaasException("价格带名称不为整数");
    }
    PriceRange query = priceRangeDao.queryByUuid(tenant, priceRange.getUuid());
    if (query == null) {
      throw new BaasException("修改价格带不存在");
    }
    PriceRange range = priceRangeDao.queryByName(tenant, query.getOrgId(), priceRange.getName());
    if (range != null){
      throw new BaasException("修改后的价格带已存在");
    }
    priceRange.setOrgId(query.getOrgId());
    priceRangeDao.update(tenant, priceRange);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void batchDelete(String tenant, List<Integer> uuids) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(uuids, "uuids");
    priceRangeDao.delete(tenant, uuids);
  }

  /**
   * dq 查询
   * @param tenant
   * @param qd
   * @return
   */
  @Override
  public QueryResult<PriceRange> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant,"tenant");
    return priceRangeDao.query(tenant,qd);
  }

  @Override
  public List<PriceRange> list(String tenant,String orgId) throws BaasException {
    Assert.notNull(tenant, "租戶");
    initPriceRange(tenant,orgId);
    return priceRangeDao.list(tenant,orgId);
  }

  @Override
  public List<PriceRange> list(String tenant) throws BaasException {
    Assert.notNull(tenant, "租戶");
    initPriceRange(tenant, "*");
    return list(tenant, null);
  }
  @Tx
  public void initPriceRange(String tenant,String orgId) throws BaasException {
    List<PriceRange> list = priceRangeDao.list(tenant,orgId);
    if (CollectionUtils.isEmpty(list)) {
      PriceRange priceRange = new PriceRange();
      priceRange.setTenant(tenant);
      priceRange.setOrgId(orgId);
      priceRange.setName("0");
      priceRangeDao.insert(tenant,priceRange);
    }
  }
}
