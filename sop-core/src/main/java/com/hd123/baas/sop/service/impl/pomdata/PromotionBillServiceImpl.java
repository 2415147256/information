package com.hd123.baas.sop.service.impl.pomdata;

import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.api.pms.rule.event.PromRuleCreateEvent;
import com.hd123.baas.sop.service.api.pms.rule.event.PromRuleStopEvent;
import com.hd123.baas.sop.service.api.pms.rule.event.PromRuleUpdateEvent;
import com.hd123.baas.sop.service.api.pms.template.event.PromTemplatePredefineEvent;
import com.hd123.baas.sop.service.dao.pomdata.ExecSeqGroupDao;
import com.hd123.baas.sop.service.dao.rule.PromRuleDao;
import com.hd123.baas.sop.service.impl.pomdata.event.PromRuleGeneralBillEvent;
import com.hd123.h5.pom.execseq.ExecSeqGroup;
import com.hd123.h5.pom.execseq.ExecSeqGroupType;
import com.hd123.h5.pom.execseq.ExecSeqInPolicy;
import com.hd123.h5.pom.execseq.ExecSeqOrder;
import com.hd123.spms.commons.bean.OperateInfo;
import com.hd123.spms.service.bill.PromotionBill;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component(PromotionBillServiceImpl.BEAN_ID)
public class PromotionBillServiceImpl {
  public static final String BEAN_ID = "sop-service.promData.generate";
  public static final OperateInfo operateInfo = new OperateInfo("系统");

  @Value("${sop-service.pms.exclude-prm.tag-code:prmGood}")
  private String excludePrmTagCode;

  @Autowired
  private PromotionBillGenerateFactory billGenerateFactory;
  @Autowired
  private PromRuleDao promRuleDao;
  @Autowired
  private ExecSeqGroupDao execSeqGroupDao;
  @Autowired
  private SpmsSyncServiceImpl spmsSyncService;
  @Autowired
  private ApplicationEventPublisher publisher;

  @PmsTx
  @EventListener
  public void processPromTemplatePredefineEvent(PromTemplatePredefineEvent event) throws BaasException {
    List<ExecSeqGroup> execSeqGroups = new ArrayList<>();
    for (int groupNumber = 1; groupNumber <= 7; groupNumber++) {
      ExecSeqGroup seqGroup = new ExecSeqGroup();
      seqGroup.setTenantId(event.getTenant());
      seqGroup.setType(ExecSeqGroupType.retail);
      seqGroup.setName("组" + groupNumber);
      seqGroup.setGroupNumber(String.valueOf(groupNumber));
      seqGroup.setOrder(ExecSeqOrder.asc);
      if (groupNumber < 5) {
        seqGroup.setInPolicy(ExecSeqInPolicy.只执行第一个);
      } else {
        seqGroup.setInPolicy(ExecSeqInPolicy.优惠最大化且每个商品只参与一个条目);
      }
      seqGroup.setGiftCondition(true);
      execSeqGroups.add(seqGroup);
    }
    execSeqGroupDao.save(execSeqGroups);
    spmsSyncService.saveSeqGroup(event.getTenant(), execSeqGroups);
  }

  @PmsTx
  @EventListener
  public void processPromRuleCreateEvent(PromRuleCreateEvent event) {
    PromRule rule = event.getRule();
    if (rule == null) {
      return;
    }
    PromRule targetRule = new PromRule();
    targetRule.setUuid(rule.getUuid());
    targetRule.setTenant(rule.getTenant());
    targetRule.setBillNumber(rule.getBillNumber());
    PromRuleGeneralBillEvent billEvent = new PromRuleGeneralBillEvent();
    BeanUtils.copyProperties(event, billEvent);
    billEvent.setRule(targetRule);
    execute(billEvent);
    log.info("processPromRuleCreateEvent：{}", event.getRule().getUuid());
  }

  @PmsTx
  @EventListener
  public void processPromRuleUpdateEvent(PromRuleUpdateEvent event) {
    PromRule rule = event.getRule();
    if (rule == null) {
      return;
    }
    PromRule targetRule = new PromRule();
    targetRule.setUuid(rule.getUuid());
    targetRule.setTenant(rule.getTenant());
    targetRule.setBillNumber(rule.getBillNumber());
    PromRuleGeneralBillEvent billEvent = new PromRuleGeneralBillEvent();
    BeanUtils.copyProperties(event, billEvent);
    billEvent.setRule(targetRule);
    execute(billEvent);
    log.info("processPromRuleUpdateEvent：{}", event.getRule().getUuid());
  }

  @PmsTx
  @EventListener
  public void processPromRuleStopEvent(PromRuleStopEvent event) {
    PromRule rule = event.getRule();
    if (rule == null) {
      return;
    }
    PromRule targetRule = new PromRule();
    targetRule.setUuid(rule.getUuid());
    targetRule.setTenant(rule.getTenant());
    targetRule.setBillNumber(rule.getBillNumber());
    PromRuleGeneralBillEvent billEvent = new PromRuleGeneralBillEvent();
    BeanUtils.copyProperties(event, billEvent);
    billEvent.setRule(targetRule);
    execute(billEvent);
    log.info("processPromRuleStopEvent：{}", event.getRule().getUuid());
  }

  public void execute(PromRuleGeneralBillEvent event) {
    if (MDC.get("trace_id") == null) {
      MDC.put("trace_id", UUID.randomUUID().toString());
    }

    PromRule rule = event.getRule();
    if (rule == null) {
      return;
    }
    rule = promRuleDao.get(rule.getTenant(), rule.getUuid(), PromRule.ALL_PARTS);
    if (rule == null) {
      return;
    }
    try {
      event.setRule(rule);
      if (rule.getState() == PromRule.State.stopped) {
        log.info("促销规则（{}）已终止，作废促销数据。", rule.getUuid());
        billGenerateFactory.abort(rule.getTenant(), rule.getUuid(), rule.getLastModifyInfo().getTime());
        publisher.publishEvent(event);
      } else {
        PromotionBill bill = billGenerateFactory.generalBill(rule);
        if (bill != null) {
          log.info("根据促销规则（{}），生成促销数据。", rule.getUuid());
          publisher.publishEvent(event);
        }
      }
    } catch (Exception e) {
      log.error("根据促销规则（" + rule.getUuid() + "），生成促销数据失败。", e);
      throw new RuntimeException(e);
    }
  }

}
