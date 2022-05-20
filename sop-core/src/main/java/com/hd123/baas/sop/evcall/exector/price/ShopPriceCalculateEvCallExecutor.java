package com.hd123.baas.sop.evcall.exector.price;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustment;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustmentService;
import com.hd123.baas.sop.service.api.price.shopprice.ShopPriceJobService;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJob;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJobState;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustment;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustmentService;
import com.hd123.baas.sop.service.impl.price.shopprice.ShopPriceCalculateMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.redis.RedisService;
import com.hd123.baas.sop.utils.RedisDistributedLocker;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.pms.util.ElapsedTimer;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 第（三）步
 *
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class ShopPriceCalculateEvCallExecutor extends AbstractEvCallExecutor<ShopPriceCalculateMsg> {

  public static final String SHOP_PRICE_CALCULATE_CREATE_EXECUTOR_ID = ShopPriceCalculateEvCallExecutor.class
      .getSimpleName();

  @Autowired
  private ShopPriceCalculateMgr calculateMgr;
  @Autowired
  private PriceAdjustmentService priceAdjustmentService;
  @Autowired
  private ShopPriceJobService priceJobService;
  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private TempShopPriceAdjustmentService tempShopPriceAdjustmentService;

  @Autowired
  private EvCallEventPublisher publisher;

  @Autowired
  private RedisService redisService;
  @Autowired
  private RedisDistributedLocker redisDistributedLocker;

  @Override
  @Tx
  protected void doExecute(ShopPriceCalculateMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    String shop = message.getShop();
    String pk = message.getPk();
    String taskId = message.getTaskId();
    log.info("第三步：门店价格计算开始，租户={},taskId={},门店={}", tenant, taskId, shop);
    H6Task h6Task = h6TaskService.get(tenant, taskId);
    if (h6Task == null) {
      log.info("h6Task任务不存在，忽略");
      return;
    }

    Date executeDate = message.getExecuteDate();
    ShopPriceJob priceJob = priceJobService.getByShopAndTask(tenant, shop, taskId);
    if (priceJob == null) {
      log.info("任务不存在，忽略");
      return;
    }

    if (priceJob.getState() == ShopPriceJobState.FINISHED) {
      log.info("任务已完成，忽略");
      return;
    }

    if (executeDate.before(DateUtils.truncate(new Date(), Calendar.DATE))) {
      // 计算今天之前的，就算是补偿也没有意义了
      log.info("计算日期 {} 为今天之前的日期，忽略", executeDate);
      return;
    }
    String lockId = null;
    String key = tenant + priceJob.getTaskId() + "shopNum";
    try {
      ElapsedTimer timer = ElapsedTimer.getThreadInstance();
      timer.clear();
      if (h6Task.getType() == H6TaskType.PRICE) {
        PriceAdjustment adjustment = priceAdjustmentService.get(tenant, pk);
        calculateMgr.calculateShopPriceAdjustment(tenant, shop, executeDate, adjustment, true, getOperateInfo());
      } else if (h6Task.getType() == H6TaskType.TEMP_SHOP) {
        PriceAdjustment adjustment = priceAdjustmentService.getEffective(tenant, h6Task.getOrgId(), executeDate);
        TempShopPriceAdjustment tempShopPriceAdjustment = tempShopPriceAdjustmentService.get(tenant, pk);
        calculateMgr.calculateTempShopPriceAdjustment(tenant, shop, executeDate, tempShopPriceAdjustment,
            adjustment);
      }
      try {
        lockId = redisDistributedLocker.lock(key, RedisDistributedLocker.LockPolicy.wait);
      } catch (Exception e) {
        context.giveBack().delay(3 * 1000);
        log.info("ShopPriceJobServiceImpl-shopTaskNum错误:{}", e.getMessage());
        return;
      }
      // 门店任务完成
      priceJobService.finish(tenant, priceJob.getUuid(), getOperateInfo());
      String cacheKey = tenant + priceJob.getTaskId();
      int finishCount = 0;
      String finishCountStr = redisService.get(cacheKey);
      if (StringUtils.isNotBlank(finishCountStr)) {
        finishCount = Integer.parseInt(finishCountStr);
      }
      finishCount++;
      long count = message.getShopCount();
      log.info("门店价格计算完毕 taskId={}，进度={}/{}，租户={} 门店={}", priceJob.getTaskId(), finishCount, count, tenant, priceJob);
      if (count == finishCount) {
        // 门店价格计算
        ShopPriceFinishedMsg msg = new ShopPriceFinishedMsg();
        msg.setTenant(tenant);
        msg.setShop(priceJob.getShop());
        msg.setTaskId(priceJob.getTaskId());
        publisher.publishForNormal(ShopPriceFinishedEvCallExecutor.SHOP_PRICE_FINISHED_CREATE_EXECUTOR_ID, msg);
      }
      redisService.set(cacheKey, finishCount, 60, TimeUnit.MINUTES);
      this.timer.accumulate(timer);
    } catch (Exception e) {
      String title = "计算门店价格异常，门店=" + shop + "，试算单=" + pk;
      priceJobService.logError(tenant, priceJob.getUuid(), title, e, getOperateInfo());
      log.error("ShopPriceCalculateEvCallExecutor错误", e);
      throw e;
    } finally {
      if (lockId != null) {
        redisDistributedLocker.unlock(key, lockId);
      }
    }
  }

//  private long getFinishCount(EvCallExecutionContext context, String tenant, ShopPriceJob priceJob) throws BaasException {
//
//    return finishCount;
//  }

  private OperateInfo getOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }

  @Override
  protected ShopPriceCalculateMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ShopPriceMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ShopPriceCalculateMsg.class);
  }

}
