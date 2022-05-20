package com.hd123.baas.sop.service.impl.price.temppriceAdjustment;

import java.util.List;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.price.TempPriceAdjustmentExecutor;
import com.hd123.baas.sop.evcall.exector.price.TempPriceAdjustmentMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.api.price.temppriceadjustment.*;
import com.hd123.baas.sop.service.dao.price.tempadjustment.TempPriceAdjustmentDaoBof;
import com.hd123.baas.sop.service.dao.price.tempadjustment.TempPriceAdjustmentLineDaoBof;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @Author maodapeng
 * @Since
 */
@Service
@Slf4j
public class TempPriceAdjustmentServiceImpl implements TempPriceAdjustmentService {
  @Autowired
  private TempPriceAdjustmentDaoBof tempPriceAdjustmentDao;
  @Autowired
  private BillNumberMgr billNumberMgr;
  @Autowired
  private TempPriceAdjustmentLineDaoBof tempPriceAdjustmentLineDao;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  public TempPriceAdjustment create(String tenant, String orgId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operateInfo);
    // 生成一個空改价单
    TempPriceAdjustment temp = new TempPriceAdjustment();
    temp.setUuid(IdGenUtils.buildRdUuid());
    temp.setState(TempPriceAdjustmentState.INIT);
    temp.setEffectiveStartDate(null);
    temp.setTenant(tenant);
    temp.setFlowNo(null);
    temp.setOrgId(orgId);
    tempPriceAdjustmentDao.insert(tenant, temp, operateInfo);
    return temp;
  }
  @Override
  public TempPriceAdjustment get(String tenant, String uuid){
    return tempPriceAdjustmentDao.get(tenant,uuid);
  }

  @Override
  @Tx
  public void save(String tenant, TempPriceAdjustment temp, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operateInfo);
    Assert.notNull(temp);
    TempPriceAdjustment history = tempPriceAdjustmentDao.get(tenant, temp.getUuid());
    if (history == null) {
      throw new BaasException("改价单不存在");
    }
    history.setFlowNo(billNumberMgr.generateTempPriceAdjustmentFlowNo(tenant));
    if (temp.getEffectiveStartDate() == null) {
      throw new BaasException("生效时间为空");
    }
    long count = tempPriceAdjustmentLineDao.count(tenant, temp.getUuid());
    if (count <= 0){
      throw new BaasException("临时改价行信息为空");
    }
    history.setEffectiveStartDate(temp.getEffectiveStartDate());
    history.setReason(temp.getReason());
    history.setState(TempPriceAdjustmentState.AUDITED);
    tempPriceAdjustmentDao.update(tenant, history, operateInfo);
    TempPriceAdjustmentMsg msg = new TempPriceAdjustmentMsg();
    msg.setTenant(tenant);
    msg.setPk(temp.getUuid());
    publisher.publishForNormal(TempPriceAdjustmentExecutor.TEMP_SHOP_PRICE_ADJUSTMENT_EXECUTOR_ID, msg);
  }

  @Override
  public QueryResult<TempPriceAdjustment> query(String tenant, QueryDefinition qd) {
    QueryResult<TempPriceAdjustment> result = tempPriceAdjustmentDao.query(tenant, qd);
    if (result.getRecords().isEmpty()) {
      return result;
    }
    for (TempPriceAdjustment record : result.getRecords()) {
      QueryDefinition lineQd = new QueryDefinition();
      lineQd.setPageSize(1);
      lineQd.setPage(0);
      lineQd.addByField(TempPriceAdjustmentLine.Queries.OWNER, Cop.EQUALS, record.getUuid());
      List<TempPriceAdjustmentLine> shopQuery = tempPriceAdjustmentLineDao.query(tenant, lineQd).getRecords();
      if (shopQuery.isEmpty()) {
        continue;
      }
      TempPriceAdjustmentLine line = shopQuery.get(0);
      TempShop shop = new TempShop();
      shop.setShop(line.getShop());
      shop.setCode(line.getShopCode());
      shop.setName(line.getShopName());
      record.setShop(shop);

      long shopCount = tempPriceAdjustmentLineDao.countShop(tenant, record.getUuid());
      record.setShopCount(shopCount);

      long count = tempPriceAdjustmentLineDao.count(tenant, record.getUuid());
      record.setCount(count);
    }
    return result;
  }

  @Override
  public QueryResult<TempShop> queryShop(String tenant, String uuid, QueryDefinition qd) {
    qd.addByField(TempPriceAdjustmentLine.Queries.OWNER, Cop.EQUALS, uuid);
    return tempPriceAdjustmentLineDao.queryShop(tenant, qd);
  }

  @Override
  public void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    TempPriceAdjustment temp = tempPriceAdjustmentDao.get(tenant, uuid);
    if (temp == null) {
      throw new BaasException("单据不存在");
    }
    if (temp.getState() == TempPriceAdjustmentState.PUBLISHED) {
      log.info("单据已发布，忽略");
      return;
    }
    if (temp.getState() != TempPriceAdjustmentState.AUDITED) {
      throw new BaasException("当前状态不可发布");
    }
    tempPriceAdjustmentDao.changeState(tenant, uuid, TempPriceAdjustmentState.PUBLISHED, operateInfo);
  }
}
