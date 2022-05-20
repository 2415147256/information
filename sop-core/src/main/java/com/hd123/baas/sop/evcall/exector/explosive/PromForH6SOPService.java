package com.hd123.baas.sop.evcall.exector.explosive;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.dao.pomdata.PromotionBillDao;
import com.hd123.baas.sop.service.dao.rule.PromRuleDao;
import com.hd123.baas.sop.service.impl.pomdata.event.PromRuleGeneralBillEvent;
import com.hd123.baas.sop.service.api.promotion.PromotionType;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.activity.H6FavorSharingDetail;
import com.hd123.baas.sop.remote.rsh6sop.prom.RSPromotionBill;
import com.hd123.baas.sop.remote.rsh6sop.prom.RSPromotionBillJoin;
import com.hd123.baas.sop.remote.rsh6sop.prom.RSPromotionBillState;
import com.hd123.baas.sop.remote.rsh6sop.prom.RSPromotionItem;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.rumba.evcall.EvCallExecutor;
import com.hd123.rumba.evcall.EvCallManager;
import com.hd123.spms.commons.bean.OperateInfo;
import com.hd123.spms.commons.json.JsonUtil;
import com.hd123.spms.service.bill.PromotionItem;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component(PromForH6SOPService.BEAN_ID)
public class PromForH6SOPService implements EvCallExecutor {
  public static final String BEAN_ID = "sop-service.PromForH6SOPService";
  public static final OperateInfo operateInfo = new OperateInfo("系统");

  @Autowired
  private PromRuleDao promRuleDao;
  @Autowired
  private PromotionBillDao promotionBillDao;
  @Autowired
  private EvCallManager evCallManager;
  @Autowired
  private FeignClientMgr feignClientMgr;

  @PostConstruct
  public void init() {
    evCallManager.addExecutor(this, BEAN_ID);
  }

  @PmsTx
  @EventListener
  public void processPromRuleGeneralBillEvent(PromRuleGeneralBillEvent event) {
    PromRule rule = event.getRule();
    if (rule == null) {
      return;
    }
    PromRule target = new PromRule();
    target.setTenant(rule.getTenant());
    target.setUuid(rule.getUuid());
    target.setBillNumber(rule.getBillNumber());
    evCallManager.submit(BEAN_ID, JsonUtil.objectToJson(target));
  }

  @PmsTx
  @Override
  public void execute(String json, @NotNull EvCallExecutionContext evCallExecutionContext) throws BaasException {
    try {
      PromRule rule = JsonUtil.jsonToObject(json, PromRule.class);
      rule = promRuleDao.get(rule.getTenant(), rule.getUuid(), PromRule.ALL_PARTS);
      if (rule == null) {
        return;
      }

      RSPromotionBill bill = createBill(rule);
      log.info("调用rsH6SOPClient.save 促销单\n{}", JsonUtil.objectToJson(bill));
      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(rule.getTenant(), null, RsH6SOPClient.class);
      rsH6SOPClient.savePromBill(rule.getTenant(), bill);
    } catch (Exception e) {
      log.error("PromForH6SOPService执行异常：{}", JsonUtil.objectToJson(e));
      throw new BaasException(e);
    }
  }

  private RSPromotionBill createBill(PromRule rule) {
    RSPromotionBill target = new RSPromotionBill();
    target.setBillNumber(rule.getBillNumber());
    if (rule.getActivity() != null) {
      target.setActivityCode(rule.getActivity().getCode());
    }
    target.setRemark(rule.getPromNote());
    target.setType("retail");
    target.setStarterOrgUuid(DefaultOrgIdConvert.toH6DefOrgId(rule.getStarterOrgUuid()));
    target.setSettleNo(StringUtil.dateToString(rule.getCreateInfo().getTime(), "yyyyMM"));
    target.setAuditTime(rule.getLastModifyInfo().getTime());
    if (rule.getState() == PromRule.State.effect) {
      target.setState(RSPromotionBillState.audited);
    } else {
      target.setState(RSPromotionBillState.aborted);
      target.setAbortTime(rule.getStopInfo() != null ? rule.getStopInfo().getTime() : new Date());
    }

    target.setAllOrg(rule.getJoinUnits().getAllUnit());
    target.setMemberExclusive(rule.getOnlyMember());

    assert rule.getPromotion() != null;
    target.setTemplateName(rule.getPromotion().getPromotionType().name());
    target.setStartDate(rule.getDateRangeCondition().getDateRange().getBeginDate());
    target.setFinishDate(DateUtils.addSeconds(rule.getDateRangeCondition().getDateRange().getEndDate(), 1));
    target.setOverall(rule.getPromotion().getProductCondition().getEntityType() == null
        && rule.getPromotion().getProductCondition().isExcludePrm() == false);

    if (rule.getJoinUnits().getStores() != null) {
      List<RSPromotionBillJoin> joins = rule.getJoinUnits().getStores().stream().map(k -> {
        RSPromotionBillJoin join = new RSPromotionBillJoin();
        join.setJoinOrgUuid(k.getUuid());
        join.setJoinOrgCode(k.getCode());
        join.setJoinOrgName(k.getName());
        return join;
      }).collect(Collectors.toList());
      target.setJoins(joins);
    }
    if (!CollectionUtils.isEmpty(rule.getFavorSharings())){
      target.setSharingDetails(rule.getFavorSharings().stream().map(k -> {
        H6FavorSharingDetail detail = new H6FavorSharingDetail();
        detail.setTargetUuid(k.getTargetUnit().getUuid());
        detail.setTargetCode(k.getTargetUnit().getCode());
        detail.setTargetName(k.getTargetUnit().getName());
        detail.setRate(k.getRate().multiply(BigDecimal.valueOf(100)));
        return detail;
      }).collect(Collectors.toList()));
    }

    // List<String> itemUuids = promotionBillDao.queryItems(rule.getUuid());
    List<PromotionItem> itemUuids = promotionBillDao.getItems(rule.getUuid());
    List<RSPromotionItem> items = itemUuids.stream().map(s -> {
      RSPromotionItem item = new RSPromotionItem();
      item.setItemUuid(s.getUuid());
      if (rule.getPromotion().getPromotionType() == PromotionType.price && isInteger(s.getProductUuid())) {
        item.setGdGid(Integer.parseInt(s.getProductUuid()));
      }
      return item;
    }).collect(Collectors.toList());
    target.setItems(items);
    return target;
  }

  public static boolean isInteger(String str) {
    Pattern pattern = Pattern.compile("[0-9]*");
    return pattern.matcher(str).matches();
  }
}
