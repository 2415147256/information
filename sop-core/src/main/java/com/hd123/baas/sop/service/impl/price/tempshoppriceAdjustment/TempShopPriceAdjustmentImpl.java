package com.hd123.baas.sop.service.impl.price.tempshoppriceAdjustment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustment;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustment;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustmentLine;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustmentService;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustmentState;
import com.hd123.baas.sop.service.dao.price.tempshopadjustment.TempShopPriceAdjustmentDaoBof;
import com.hd123.baas.sop.service.dao.price.tempshopadjustment.TempShopPriceAdjustmentLineDaoBof;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceTaskMsg;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Service
@Slf4j
public class TempShopPriceAdjustmentImpl implements TempShopPriceAdjustmentService {
  @Autowired
  private TempShopPriceAdjustmentDaoBof tempShopPriceAdjustmentDao;
  @Autowired
  private TempShopPriceAdjustmentLineDaoBof lineDao;
  @Autowired
  private BillNumberMgr billNumberMgr;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private H6TaskService h6TaskService;

  @Override
  public TempShopPriceAdjustment create(String tenant, String orgId, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    TempShopPriceAdjustment adjustment = new TempShopPriceAdjustment();
    adjustment.setOrgId(orgId);
    adjustment.setUuid(IdGenUtils.buildRdUuid());
    adjustment.setState(TempShopPriceAdjustmentState.INIT);
    adjustment.setEffectiveStartDate(null);
    adjustment.setTenant(tenant);
    adjustment.setFlowNo(null);
    tempShopPriceAdjustmentDao.insert(tenant, adjustment, operateInfo);
    return adjustment;
  }

  @Override
  public TempShopPriceAdjustment get(String tenant, String uuid) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    return tempShopPriceAdjustmentDao.get(tenant, uuid);
  }

  @Override
  @Tx
  public void save(String tenant, TempShopPriceAdjustment temp, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(temp);
    Assert.hasText(temp.getUuid(), "uuid");
    Assert.notNull(operateInfo);
    TempShopPriceAdjustment history = tempShopPriceAdjustmentDao.get(tenant, temp.getUuid());
    if (history == null) {
      throw new BaasException("改价单不存在");
    }
    if (history.getState() != TempShopPriceAdjustmentState.INIT) {
      throw new BaasException("改价单不是init状态，无法保存");
    }
    if (temp.getEffectiveStartDate() == null) {
      throw new BaasException("生效时间为空");
    }
    long count = lineDao.count(tenant, temp.getUuid());
    if (count <= 0) {
      throw new BaasException("临时到店价改价行信息为空");
    }
    history.setFlowNo(billNumberMgr.generateTempPriceAdjustmentFlowNo(tenant));
    history.setEffectiveStartDate(temp.getEffectiveStartDate());
    history.setReason(temp.getReason());
    history.setState(TempShopPriceAdjustmentState.CONFIRMED);
    tempShopPriceAdjustmentDao.update(tenant, history, operateInfo);

    List<H6Task> h6Tasks = h6TaskService.getByDate(tenant, history.getOrgId(), H6TaskType.TEMP_SHOP,
        history.getEffectiveStartDate());
    if (CollectionUtils.isNotEmpty(h6Tasks)) {
      throw new BaasException("存在正在执行的任务，请求稍后保存");
    }

    Date endDate = DateUtils.addDays(new Date(), PriceAdjustment.MIN_EFFECTIVE_DAYS);
    endDate = DateUtils.truncate(endDate, Calendar.DATE);
    log.info("准备核对生效时间：EffectiveStartDate={}，endDate={}", history.getEffectiveStartDate(), endDate);
    // 最后的时间是：明天的00：00：00
    if (history.getEffectiveStartDate().getTime() <= endDate.getTime()) {
      // 实时推送
      publishShopPriceEvCall(tenant, history.getOrgId(), history.getEffectiveStartDate());
    }
  }

  @Override
  public QueryResult<TempShopPriceAdjustment> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    return tempShopPriceAdjustmentDao.query(tenant, qd);
  }

  @Override
  @Tx
  public void terminate(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "tenant");
    TempShopPriceAdjustment tempShopPriceAdjustment = tempShopPriceAdjustmentDao.get(tenant, uuid);
    if (tempShopPriceAdjustment == null) {
      throw new BaasException("改价单不存在");
    }
    List<H6Task> h6Tasks = h6TaskService.getByDate(tenant, tempShopPriceAdjustment.getOrgId(), H6TaskType.TEMP_SHOP,
        tempShopPriceAdjustment.getEffectiveStartDate());
    if (CollectionUtils.isNotEmpty(h6Tasks)) {
      throw new BaasException("存在正在执行的任务，请求稍后终止");
    }
    tempShopPriceAdjustmentDao.changeState(tenant, uuid, TempShopPriceAdjustmentState.CANCELED, operateInfo);
  }

  @Override
  public void delete(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "owner");
    lineDao.delete(tenant, uuid);
  }

  @Override
  @Tx
  public void addLiens(String tenant, String owner, List<TempShopPriceAdjustmentLine> lines) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "owner");
    lineDao.batchInsert(tenant, owner, lines);
  }

  @Override
  public List<TempShopPriceAdjustmentLine> getLines(String tenant, String owner) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "owner");
    return lineDao.list(tenant, owner);
  }

  @Override
  public void publish(String tenant, String uuid, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    tempShopPriceAdjustmentDao.changeState(tenant, uuid, TempShopPriceAdjustmentState.EFFECTED, operateInfo);
  }

  private void publishShopPriceEvCall(String tenant, String orgId, Date executeDate) {
    ShopPriceTaskMsg msg = new ShopPriceTaskMsg();
    msg.setTenant(tenant);
    msg.setExecuteDate(executeDate);
    msg.setTaskType(H6TaskType.TEMP_SHOP);
    msg.setOrgId(orgId);
    log.info("准备实时推送消息：{}", JsonUtil.objectToJson(msg));
    publisher.publishForNormal(ShopPriceEvCallExecutor.SHOP_PRICE_CREATE_EXECUTOR_ID, msg);
  }

}
