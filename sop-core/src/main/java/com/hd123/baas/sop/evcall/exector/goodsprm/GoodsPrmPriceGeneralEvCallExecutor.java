package com.hd123.baas.sop.evcall.exector.goodsprm;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.dao.rule.PromRuleDao;
import com.hd123.baas.sop.service.api.pomdata.PromSingleProductService;
import com.hd123.baas.sop.service.impl.pomdata.event.PromRuleGeneralBillEvent;
import com.hd123.baas.sop.service.api.promotion.PromotionType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.rumba.evcall.EvCallExecutor;
import com.hd123.rumba.evcall.EvCallManager;
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.commons.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component(GoodsPrmPriceGeneralEvCallExecutor.BEAN_ID)
public class GoodsPrmPriceGeneralEvCallExecutor implements EvCallExecutor {
  public static final String BEAN_ID = "sop.GoodsPrmPriceGeneralEvCallExecutor";

  @Autowired
  private EvCallManager evCallManager;
  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private PromRuleDao promRuleDao;
  @Autowired
  private PromSingleProductService singleProductService;

  @PostConstruct
  public void init() {
    evCallManager.addExecutor(this, BEAN_ID);
  }

  @PmsTx
  @EventListener
  public void processPromRuleGeneralBillEvent(PromRuleGeneralBillEvent billEvent) {
    PromRule rule = billEvent.getRule();
    if (rule == null) {
      return;
    }

    rule = promRuleDao.get(rule.getTenant(), rule.getUuid());
    // 只有促销价的规则，才会产下发任务
    if (rule.getPromotion().getPromotionType() != PromotionType.price) {
      return;
    } else if (rule.getTimePeriodCondition() != null) {
      return;
    } else if ("-".equals(rule.getStarterOrgUuid()) == false) {
      return;
    }

    GoodsPrmPriceGeneralConfig config = configClient.getConfig(rule.getTenant(), GoodsPrmPriceGeneralConfig.class);
    Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
    Date endDate = DateUtils.addDays(startDate, config.getSpanDays() + 1);
    if (new DateRange(startDate, endDate).interactExists(rule.getDateRangeCondition().getDateRange())) {
      log.info("商品促销价数据下发作业，收到促销单变更消息(rule={})。\n开始下发数据", rule.getBillNumber());
      GoodsPrmPriceGeneralEvent generalEvent = new GoodsPrmPriceGeneralEvent();
      generalEvent.setTenant(rule.getTenant());
      generalEvent.setOrgId(rule.getOrgId());
      generalEvent.setTargetRule(rule);
      generalEvent.setProductCondition(billEvent.getProductCondition());
      generalEvent.setJoinUnits(billEvent.getJoinUnits());
      generalEvent.setExecuteDate(DateUtils.truncate(DateUtils.addDays(new Date(), 1), Calendar.DATE));
      evCallManager.submit(BEAN_ID, JsonUtil.objectToJson(generalEvent));
    } else {
      log.info("商品促销价数据下发作业，收到促销单变更消息(rule={})。\n日期范围不在触发范围内(d+{})", rule.getBillNumber(), config.getSpanDays());
    }
  }

  @Override
  public void execute(String json, @NotNull EvCallExecutionContext evCallExecutionContext) throws Exception {
    log.info("GoodsPrmPriceGeneralEvCallExecutor:{}", json);
    GoodsPrmPriceGeneralEvent event = JsonUtil.jsonToObject(json, GoodsPrmPriceGeneralEvent.class);
    if (event == null) {
      return;
    }

    try {
      // 任务
      H6Task h6Task = new H6Task();
      h6Task.setTenant(event.getTenant());
      h6Task.setType(H6TaskType.PROM);
      h6Task.setOrgId(event.getOrgId());
      h6Task.setExecuteDate(event.getExecuteDate());
      OperateInfo operateInfo = new OperateInfo(new Operator("system", "系统作业"));
      String taskId = h6TaskService.saveNew(event.getTenant(), h6Task, operateInfo);

      GoodsPrmPriceGeneralConfig config = configClient.getConfig(event.getTenant(), GoodsPrmPriceGeneralConfig.class);
      String fileUrl = event.getTargetRule() == null ?
              singleProductService.generalZip(event.getTenant(), config.getSpanDays()) :
              singleProductService.generalZip(event.getTargetRule(), event.getProductCondition(), event.getJoinUnits(), config.getSpanDays());
      log.info("GoodsPrmPriceGeneralEvCallExecutor，{},生成下发文件:{}", taskId, fileUrl);
      h6Task.setFileUrl(fileUrl);
      h6TaskService.fixUrl(event.getTenant(), taskId, fileUrl, operateInfo);
      evCallManager.submit(GoodsPrmPriceFinishedEvCallExecutor.BEAN_ID, JsonUtil.objectToJson(h6Task));
    } catch (Exception e) {
      log.error("GoodsPrmPriceGeneralEvCallExecutor错误", e);
      throw e;
    }
  }
}
