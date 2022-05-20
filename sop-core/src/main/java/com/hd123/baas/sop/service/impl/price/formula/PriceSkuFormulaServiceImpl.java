package com.hd123.baas.sop.service.impl.price.formula;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.formula.PriceSkuFormula;
import com.hd123.baas.sop.service.api.formula.PriceSkuFormulaService;
import com.hd123.baas.sop.service.dao.price.formula.PriceSkuFormulaDaoBof;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @Author maodapeng
 * @Since
 */
@Service
public class PriceSkuFormulaServiceImpl implements PriceSkuFormulaService {
  @Autowired
  private PriceSkuFormulaDaoBof formulaDao;

  @Override
  @Tx
  public void batchSaveNew(String tenant, String orgId, List<PriceSkuFormula> priceSkuFormulaList) {
    Assert.notNull(tenant, "tenant");
    //校验 1、商品是否存在
    formulaDao.deleteAll(tenant, orgId);
    if (CollectionUtils.isNotEmpty(priceSkuFormulaList)){
      formulaDao.batchInsert(tenant, priceSkuFormulaList);
    }
  }

  @Override
  public PriceSkuFormula getBySkuId(String tenant, String orgId, String skuId) {
    return formulaDao.getBySkuId(tenant, orgId, skuId);
  }

  @Override
  public List<PriceSkuFormula> getByDependOnSkuId(String tenant, String orgId, String dependOnSkuId) {
    return formulaDao.getByDependOnSkuId(tenant, orgId, dependOnSkuId);
  }

  @Override
  public void delete(String tenant, String uuid) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    formulaDao.delete(tenant, uuid);
  }

  @Override
  public QueryResult<PriceSkuFormula> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(qd, "qd");
    return formulaDao.query(tenant, qd);
  }
}
