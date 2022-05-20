package com.hd123.baas.sop.evcall.exector.price;

import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.price.tempshop.TempShopPriceAdjustmentStartMsg;
import com.hd123.baas.sop.evcall.exector.price.tempshop.TempShopPriceStartEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 第（一）步
 *
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class ShopPriceEvCallExecutor extends AbstractEvCallExecutor<ShopPriceTaskMsg> {

  public static final String SHOP_PRICE_CREATE_EXECUTOR_ID = ShopPriceEvCallExecutor.class.getSimpleName();

  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  protected void doExecute(ShopPriceTaskMsg message, EvCallExecutionContext context) throws Exception {
    try {
      String tenant = message.getTenant();
      String orgId = message.getOrgId();
      Date executeDate = message.getExecuteDate();

      Assert.hasText(tenant, "tenant");
      Assert.hasText(orgId, "orgId");
      Assert.notNull(executeDate, "executeDate");

      H6TaskType taskType = message.getTaskType();
      if (taskType == null) {
        taskType = H6TaskType.PRICE;
      }
      log.info("第一步：开始创建任务 租户<{}>，组织<{}>,日期<{}>,类型<{}>", tenant, orgId, executeDate, taskType.name());
      if (executeDate.before(DateUtils.truncate(new Date(), Calendar.DATE))) {
        // 计算今天之前的，就算是补偿也没有意义了
        log.info("计算日期 {} 为今天之前的日期，忽略", executeDate);
        return;
      }

      List<H6Task> unFinishTasks = h6TaskService.getByDate(tenant, orgId, taskType, executeDate);
      if (unFinishTasks == null) {
        unFinishTasks = new ArrayList<>();
      }

      if (unFinishTasks.size() >= 2) {
        log.info("已存在2条或以上的待执行的任务，忽略");
        return;
      }
      // 创建任务
      H6Task h6Task = new H6Task();
      h6Task.setType(taskType);
      h6Task.setExecuteDate(executeDate);
      h6Task.setOrgId(orgId);
      String taskId = h6TaskService.init(tenant, h6Task, getSysOperateInfo());
      log.info("生成taskid，taskId={}", taskId);
      if (unFinishTasks.size() == 0) {
        // 执行下一步
        if (h6Task.getType() == H6TaskType.PRICE) {
          ShopPriceStartMsg msg = new ShopPriceStartMsg();
          msg.setTenant(tenant);
          msg.setExecuteDate(executeDate);
          msg.setTaskId(taskId);
          publisher.publishForNormal(ShopPriceStartEvCallExecutor.EXECUTOR_ID, msg);
        } else {
          TempShopPriceAdjustmentStartMsg startMsg = new TempShopPriceAdjustmentStartMsg();
          startMsg.setTenant(tenant);
          startMsg.setTaskId(taskId);
          publisher.publishForNormal(TempShopPriceStartEvCallExecutor.TEMP_SHOP_PRICE_START_EV_CALL, startMsg);
        }
      } else {
        log.info("存在1个待提交任务，待唤醒...");
      }
    } catch (Exception e) {
      log.error("ShopPriceEvCallExecutor错误", e);
      throw e;
    }
  }

  @Override
  protected ShopPriceTaskMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ShopPriceTaskMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ShopPriceTaskMsg.class);
  }

}
