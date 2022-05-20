package com.hd123.baas.sop.evcall.exector.price;

import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.impl.price.shopprice.ShopPriceCalculateMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.pms.util.ElapsedTimer;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * 第（二）步
 *
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class ShopPriceStartEvCallExecutor extends AbstractEvCallExecutor<ShopPriceStartMsg> {

  public static final String EXECUTOR_ID = ShopPriceStartEvCallExecutor.class.getSimpleName();

  @Autowired
  private ShopPriceCalculateMgr calculateMgr;
  @Autowired
  private H6TaskService h6TaskService;

  @Override
  protected void doExecute(ShopPriceStartMsg message, EvCallExecutionContext context) throws Exception {
    try {
      MDC.put("trace_id", IdGenUtils.buildIidAsString());
    } catch (Exception ex) {
      // nothing
    }

    String tenant = message.getTenant();
    Date executeDate = message.getExecuteDate();
    String taskId = message.getTaskId();
    log.info("第二步：开始计算价格 租户<{}>,日期<{}>，任务=<{}>", tenant, executeDate, taskId);
    if (taskId == null) {
      log.info("taskId is null，忽略");
      return;
    }
    if (executeDate.before(DateUtils.truncate(new Date(), Calendar.DATE))) {
      // 计算今天之前的，就算是补偿也没有意义了
      log.info("计算日期 {} 为今天之前的日期，忽略", executeDate);
      return;
    }

    H6Task h6Tasks = h6TaskService.get(tenant, taskId);
    if (h6Tasks == null) {
      log.info("任务不存在。");
      return;
    }
    if (h6Tasks.getState().equals(H6TaskState.FINISHED)) {
      log.info("任务已完成。");
      return;
    }
    String orgId = h6Tasks.getOrgId();
    String title = "";
    OperateInfo operateInfo = getSysOperateInfo();
    ElapsedTimer timer = ElapsedTimer.getThreadInstance();
    try {
      // 更新状态
      h6TaskService.updateState(tenant, taskId, H6TaskState.CONFIRMED, getSysOperateInfo());
      timer.start("计算未来的门店价格级");
      calculateMgr.calculatePriceGradeManager(tenant, orgId, executeDate, operateInfo);
      timer.stop("计算未来的门店价格级");

      timer.start("计算当前门店价格级");
      calculateMgr.calculatePriceGrade(tenant, orgId);
      timer.stop("计算当前门店价格级");

      timer.start("计算未来到店价促销");
      calculateMgr.calculatePricePromotionManager(tenant, orgId, executeDate, operateInfo);
      timer.stop("计算未来到店价促销");

      timer.start("计算当前到店价促销");
      calculateMgr.calculatePricePromotion(tenant, orgId, executeDate);
      timer.stop("计算当前到店价促销");

      timer.start("计算门店价格");
      String currentAdjustmentUuid = calculateMgr.calculatePriceAdjustment(tenant, orgId, executeDate, taskId, operateInfo);
      timer.stop("计算门店价格");

      timer.start("清除今天之前的数据");
      calculateMgr.clearExpiredShopPrice(tenant, orgId);
      timer.stop("清除今天之前的数据");

      timer.start("过期试算单");
      calculateMgr.expiredAdjustment(tenant, orgId, executeDate, currentAdjustmentUuid, operateInfo);
      timer.stop("过期试算单");

      timer.start("过期门店价格级调整单");
      calculateMgr.expiredGradeAdjustment(tenant, orgId, operateInfo);
      timer.stop("过期门店价格级调整单");

      timer.start("过期到店价促销单");
      calculateMgr.expiredPricePromotion(tenant, orgId, operateInfo);
      timer.stop("过期到店价促销单");

      this.timer.accumulate(timer);
    } catch (Exception e) {
      String msg = title + " 发生异常";
      log.error(msg, e);
      h6TaskService.logError(tenant, taskId, title, e, operateInfo);
      throw e;
    }
  }

  @Override
  protected ShopPriceStartMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ShopPriceStartMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ShopPriceStartMsg.class);
  }

}
