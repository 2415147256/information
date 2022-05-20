package com.hd123.baas.sop.service.impl.grade;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.hd123.baas.sop.utils.OrgUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.LogRequestPraras;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.entity.PriceGrade;
import com.hd123.baas.sop.service.api.grade.PriceGradeService;
import com.hd123.baas.sop.service.dao.grade.PriceGradeDaoBof;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

@Service
public class PriceGradeServiceImpl implements PriceGradeService {
  @Autowired
  private PriceGradeDaoBof priceGradeDao;

  @Override
  @Tx
  @LogRequestPraras
  public void saveNew(String tenant, PriceGrade priceGrade) throws BaasException {
    Assert.notNull(priceGrade);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(priceGrade.getName(), "名称");
    List<PriceGrade> list = priceGradeDao.list(tenant, priceGrade.getOrgId());
    if (CollectionUtils.isEmpty(list)) {
      priceGrade.setSeq(0);
      priceGrade.setDft(true);
    } else {
      boolean b = list.stream().anyMatch(grade -> grade.getName().equalsIgnoreCase(priceGrade.getName()));
      if (b) {
        throw new BaasException("新增价格级已存在");
      }
      int maxSeq = list.stream().map(PriceGrade::getSeq).distinct().max(Integer::compareTo).get();
      priceGrade.setSeq(maxSeq + 1);
    }
    priceGradeDao.insert(tenant, priceGrade);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void saveModify(String tenant, PriceGrade priceGrade) throws BaasException {
    Assert.notNull(priceGrade);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(priceGrade.getUuid(), "uuid");
    Assert.notNull(priceGrade.getName(), "名称");
    PriceGrade query = priceGradeDao.query(tenant, priceGrade.getUuid());
    if (query == null) {
      throw new BaasException("修改的价格级不存在");
    }
    priceGradeDao.update(tenant, priceGrade);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void batchSaveModify(String tenant, List<PriceGrade> priceGrades) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(priceGrades);
    List<Integer> updateIds = priceGrades.stream().map(PriceGrade::getUuid).collect(Collectors.toList());
    List<PriceGrade> existPriceGrades = priceGradeDao.queryByIds(tenant, updateIds);
    if (CollectionUtils.isEmpty(existPriceGrades)) {
      throw new BaasException("更新的价格级全部不存在");
    }
    List<Integer> existIds = existPriceGrades.stream().map(PriceGrade::getUuid).collect(Collectors.toList());
    updateIds.removeAll(existIds);
    if (CollectionUtils.isNotEmpty(updateIds)) {
      throw new BaasException("价格集不存在");
    }
    priceGradeDao.batchUpdate(tenant, priceGrades);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void batchDelete(String tenant, List<Integer> uuids) {
    Assert.notNull(tenant, "租戶");
    if (CollectionUtils.isNotEmpty(uuids)) {
      priceGradeDao.delete(tenant, uuids);
    }
  }

  @Override
  public void batchSaveNew(String tenant, List<PriceGrade> priceGrades) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(priceGrades, "priceGrades");
    Set<String> names = priceGrades.stream().map(PriceGrade::getName).collect(Collectors.toSet());
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceGrade.Queries.NAME, Cop.IN, names.toArray());
    QueryResult<PriceGrade> query = priceGradeDao.query(tenant, qd);
    List<PriceGrade> records = query.getRecords();
    if (!records.isEmpty()) {
      throw new BaasException("新增价格级已存在");
    }
    priceGradeDao.batchInsert(tenant, priceGrades);
  }

  @Override
//  @LogRequestPraras
  public List<PriceGrade> list(String tenant, String orgId) {
    Assert.notNull(tenant, "租戶");
    return priceGradeDao.list(tenant, orgId);
  }

  @Override
//  @LogRequestPraras
  public List<PriceGrade> list(String tenant) {
    Assert.notNull(tenant, "租戶");
    return priceGradeDao.list(tenant, null);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void setDefault(String tenant, String orgId, Integer uuid) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    QueryDefinition qd = new QueryDefinition();
    if (OrgUtils.isNotAllScope(tenant, orgId)) {
      qd.addByField(PriceGrade.Queries.ORG_ID, Cop.EQUALS, orgId);
    }
    QueryResult<PriceGrade> query = priceGradeDao.query(tenant, qd);
    List<PriceGrade> records = query.getRecords();
    if (CollectionUtils.isEmpty(records)) {
      throw new BaasException("该租户不存在价格级");
    }
    boolean isExist = records.stream().anyMatch(x -> uuid == x.getUuid());
    if (!isExist) {
      throw new BaasException("设置默认的价格级不存在");
    }
    records.stream().forEach(x -> {
      if (x.getUuid() == uuid) {
        x.setDft(true);
      } else {
        x.setDft(false);
      }
    });
    PriceGrade defaultGrade = priceGradeDao.query(tenant, uuid);
    if (defaultGrade == null) {
      throw new BaasException("选择默认的价格级不存在");
    }
    priceGradeDao.setDefault(tenant, records);
  }

  @Override
  public PriceGrade getDftPriceGrade(String tenant,String orgId) throws BaasException {
    return priceGradeDao.getDftPriceGrade(tenant,orgId);
  }

  @Override
  public PriceGrade getDftPriceGrade(String tenant) throws BaasException {
    return priceGradeDao.getDftPriceGrade(tenant, null);
  }
  @Override
  public QueryResult<PriceGrade> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "租戶");
    Assert.notNull(qd, "qd");
    return priceGradeDao.query(tenant, qd);
  }

  @Override
  public PriceGrade get(String tenant, Integer uuid) {
    Assert.hasText(tenant, "租戶");
    Assert.notNull(uuid, "uuid");

    return priceGradeDao.get(tenant, String.valueOf(uuid));
  }
}
