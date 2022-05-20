package com.hd123.baas.sop.evcall.exector.skumgr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class ShopSkuEvCallExecutor extends AbstractEvCallExecutor<ShopSkuTaskMsg> {

  public static final String SHOP_SKU_TASK_EXECUTOR_ID = ShopSkuEvCallExecutor.class.getSimpleName();

  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  protected void doExecute(ShopSkuTaskMsg message, EvCallExecutionContext context) throws Exception {
    try {
      String tenant = message.getTenant();
      String orgId = message.getOrgId();
      Date executeDate = message.getExecuteDate();
      log.info("开始创建任务 租户<{}>，日期<{}>", tenant, executeDate);

      if (executeDate.before(DateUtils.truncate(new Date(), Calendar.DATE))) {
        // 计算今天之前的，就算是补偿也没有意义了
        log.info("计算日期 {} 为今天之前的日期，忽略", executeDate);
        return;
      }

      List<H6Task> unFinishTasks = h6TaskService.getByDate(tenant,orgId, H6TaskType.SKU, executeDate);
      if (unFinishTasks == null) {
        unFinishTasks = new ArrayList<>();
      }

      if (unFinishTasks.size() >= 1) {
        log.info("已存在1条或以上的待执行的任务，忽略");
        return;
      }
      // 创建任务
      H6Task h6Task = new H6Task();
      h6Task.setType(H6TaskType.SKU);
      h6Task.setOrgId(orgId);
      h6Task.setExecuteDate(executeDate);
      String taskId = h6TaskService.init(tenant, h6Task, getSysOperateInfo());
      log.info("生成taskid，taskId={}", taskId);
      if (unFinishTasks.size() == 0) {
        // 执行下一步
        DirectorySkuStartMsg msg = new DirectorySkuStartMsg();
        msg.setTenant(tenant);
        msg.setExecuteDate(executeDate);
        msg.setTaskId(taskId);
        publisher.publishForNormal(DirectorySkuStartEvCallExecutor.EXECUTOR_ID, msg);
      } else {
        log.info("存在1个待提交任务，待唤醒...");
      }
    } catch (Exception e) {
      log.error("ShopSkuEvCallExecutor错误", e);
      throw e;
    }
  }

  @Override
  protected ShopSkuTaskMsg decodeMessage(String msg) throws BaasException {
    log.info("收到门店商品下发ShopSkuTaskMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ShopSkuTaskMsg.class);
  }

}
