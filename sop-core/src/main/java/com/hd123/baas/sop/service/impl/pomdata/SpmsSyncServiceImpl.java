package com.hd123.baas.sop.service.impl.pomdata;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.dao.pomdata.ExecSeqGroupDao;
import com.hd123.baas.sop.service.dao.pomdata.PromotionBillDao;
import com.hd123.baas.sop.service.api.pomdata.SopPromotionBillJoin;
import com.hd123.baas.sop.remote.spms.ExecSeqClient;
import com.hd123.baas.sop.remote.spms.SpmsSyncClient;
import com.hd123.baas.sop.remote.spms.TransferSyncConfig;
import com.hd123.h5.pom.LimitType;
import com.hd123.h5.pom.document.PomBuilder;
import com.hd123.h5.pom.document.PomDocument;
import com.hd123.h5.pom.document.PomElement;
import com.hd123.h5.pom.document.PomEntity;
import com.hd123.h5.pom.document.PomEntityType;
import com.hd123.h5.pom.document.condition.member.voucher.VoucherActivityCondition;
import com.hd123.h5.pom.document.condition.product.BasketCondition;
import com.hd123.h5.pom.document.condition.product.ProductCondition;
import com.hd123.h5.pom.document.condition.product.SingleProductCondition;
import com.hd123.h5.pom.document.execution.GeneralExecution;
import com.hd123.h5.pom.document.expression.PomConditionRPNCodec;
import com.hd123.h5.pom.document.range.Condition;
import com.hd123.h5.pom.document.range.ProductRange;
import com.hd123.h5.pom.execseq.ExecSeqGroup;
import com.hd123.h5.pom.execseq.ExecSeqGroupType;
import com.hd123.h5.pom.execseq.ExecSeqInPolicy;
import com.hd123.h5.pom.execseq.ExecSeqOrder;
import com.hd123.pms.util.expression.ExpressionException;
import com.hd123.rumba.commons.collection.MapUtil;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.rumba.evcall.EvCallExecutor;
import com.hd123.rumba.evcall.EvCallManager;
import com.hd123.spms.commons.bean.ListWrapper;
import com.hd123.spms.commons.bean.Response;
import com.hd123.spms.commons.json.JsonUtil;
import com.hd123.spms.commons.util.CollectionUtil;
import com.hd123.spms.console.service.task.transfer.SpmsTransferTask;
import com.hd123.spms.console.service.task.transfer.SpmsTransferTaskState;
import com.hd123.spms.service.bill.PromotionBill;
import com.hd123.spms.service.bill.PromotionBillState;
import com.hd123.spms.service.bill.PromotionItem;
import com.hd123.spms.sync.PromPomItem;
import com.hd123.spms.sync.PromPomItemPlan;
import com.hd123.spms.sync.PromTransferDataV4;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.feign.DynamicFeignMgr;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SpmsSyncServiceImpl implements EvCallExecutor {
  public static final String BEAN_ID = "sop-service.promData.spmsSync";
  public static final String TRANSFER_SOURCE = "sop";

  @Autowired
  private EvCallManager evCallManager;
  @Autowired
  private PromotionBillDao promotionBillDao;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private DynamicFeignMgr dynamicFeignMgr;
  @Autowired
  private ExecSeqGroupDao execSeqGroupDao;

  @PostConstruct
  public void init() {
    evCallManager.addExecutor(this, BEAN_ID);
  }

  public void submitSync(String tenant, String billUuid) {
    PromRule target = new PromRule();
    target.setTenant(tenant);
    target.setUuid(billUuid);
    evCallManager.submit(BEAN_ID, JsonUtil.objectToJson(target));
  }

  @Override
  public void execute(String json, @NotNull EvCallExecutionContext evCallExecutionContext) throws Exception {
    if (MDC.get("trace_id") == null) {
      MDC.put("trace_id", UUID.randomUUID().toString());
    }

    PromRule rule = JsonUtil.jsonToObject(json, PromRule.class);
    transferBill(rule.getTenant(), rule.getUuid());
  }

  public void saveSeqGroup(String tenantId, List<ExecSeqGroup> seqGroups) throws BaasException {
    TransferSyncConfig config = configClient.getConfig(tenantId, TransferSyncConfig.class);
    if (StringUtils.isNotBlank(config.getUrl())) {
      saveSeqGroup(tenantId, seqGroups, config);
    }
    config = configClient.getConfig(tenantId, TransferSyncConfig.class, "second");
    if (StringUtils.isNotBlank(config.getUrl())) {
      saveSeqGroup(tenantId, seqGroups, config);
    }
  }

  public void saveSeqGroup(String tenantId, List<ExecSeqGroup> seqGroups, TransferSyncConfig config) throws BaasException {
    if (config.getUrl() == null) {
      log.warn("未配置促销中台同步设置，跳过");
      return;
    }
    if (config.getTenantId() == null) {
      config.setTenantId(tenantId);
    }

    ExecSeqClient execSeqClient = dynamicFeignMgr.getClient(ExecSeqClient.class, config);

    log.info("{}-初始化执行策略组", tenantId);
    Map<String, ExecSeqGroup> groupMap = seqGroupMap(seqGroups, config);

    groupMap.computeIfAbsent("0", k -> new ExecSeqGroup("组0", "0", ExecSeqOrder.asc, ExecSeqInPolicy.顺序执行并冲撞, null, "priceType"));
    groupMap.computeIfAbsent("10", k -> new ExecSeqGroup("组10", "10", ExecSeqOrder.asc, ExecSeqInPolicy.顺序执行并冲撞, "0-冲撞", "priceType"));

    seqGroups = new ArrayList<>(groupMap.values());
    seqGroups.sort(Comparator.comparing(o -> StringUtil.toBigDecimal(o.getGroupNumber())));
    execSeqClient.saveGroups(config.getTenantId(), ExecSeqGroupType.retail, ListWrapper.newInstance(seqGroups));
  }

  private Map<String, ExecSeqGroup> seqGroupMap(List<ExecSeqGroup> seqGroups, TransferSyncConfig config) {
    Map<String, ExecSeqGroup> result = new HashMap<>();
    if (seqGroups == null)
      return result;

    Map<String, String> seqGroupMap = MapUtil.toMap(config.getSeqGroupMap(), ',', '=');
    List<String> seqGroupConflictZeros = CollectionUtil.toList(config.getSeqGroupConflictZeros(), ',');
    Map<String, String> typeSeqGroupMap = MapUtil.toMap(config.getTypeSeqGroupMap(), ',', '=');
    for (ExecSeqGroup seqGroup : seqGroups) {
      if (seqGroupMap.get(seqGroup.getGroupNumber()) != null) {
        seqGroup.setGroupNumber(seqGroupMap.get(seqGroup.getGroupNumber()));
      }

      if (StringUtil.isNullOrBlank(seqGroup.getOutPolicy()) == false) {
        List<String> policyList = CollectionUtil.toList(seqGroup.getOutPolicy(), ',');
        List<String> policies = new ArrayList<>();
        for (String policy : policyList) {
          String[] groupPolicy = policy.split("-");
          policies.add(StringUtil.toString(seqGroupMap.get(groupPolicy[0]), groupPolicy[0]) + "-" + groupPolicy[1]);
        }
        seqGroup.setOutPolicy(CollectionUtil.toString(policies, ','));
      }

      if (seqGroupConflictZeros.contains(seqGroup.getGroupNumber())) {
        if (StringUtil.isNullOrBlank(seqGroup.getOutPolicy())) {
          seqGroup.setOutPolicy("0-冲撞");
        } else {
          List<String> policyList = CollectionUtil.toList(seqGroup.getOutPolicy(), ',');
          if (policyList.contains("0-冲撞") == false) {
            policyList.add(0, "0-冲撞");
            seqGroup.setOutPolicy(CollectionUtil.toString(policyList, ','));
          }
        }
      }

      result.put(seqGroup.getGroupNumber(), seqGroup);
    }
    return result;
  }

  public void transferBill(String tenantId, String billUuid) throws Exception {
    TransferSyncConfig config = configClient.getConfig(tenantId, TransferSyncConfig.class);
    if (StringUtils.isNotBlank(config.getUrl())) {
      transferBill(tenantId, billUuid, config);
    }
    config = configClient.getConfig(tenantId, TransferSyncConfig.class, "second");
    if (StringUtils.isNotBlank(config.getUrl())) {
      transferBill(tenantId, billUuid, config);
    }
  }

  public void transferBill(String tenantId, String billUuid, TransferSyncConfig config) throws Exception {
    try {
      if (config.getUrl() == null) {
        log.warn("未配置促销中台同步设置，跳过");
        return;
      }
      if (config.getTenantId() == null) {
        config.setTenantId(tenantId);
      }

      PromotionBill record = promotionBillDao.get4Sync(tenantId, billUuid);
      if (record == null) {
        log.info("促销单数据错误，未找到对应数据");
        return;
      }
      List<String> promChannels = getPromChannels(config, record.getPromChannels());
      record.setPromChannels(promChannels);
      SpmsSyncClient syncClient = dynamicFeignMgr.getClient(SpmsSyncClient.class, config);

      if (record.getState() == PromotionBillState.initial) {
        log.info("{} - {}, 未审核，跳过", tenantId, record.getBillNumber());
      } else if (record.getState() == PromotionBillState.aborted) {
        syncClient.delete(config.getTenantId(), TRANSFER_SOURCE, record.getBillNumber());
        log.info("{} - {}, 作废完成", tenantId, record.getBillNumber());
      } else {
        PromTransferDataV4 transferData = getData(record, config);
        if (transferData == null) {
          log.info("{} - {}, 未检索到数据，跳过", tenantId, record.getBillNumber());
        } else if (transferData.getItems() == null || transferData.getItems().isEmpty()) {
          log.info("{} - {}, 未检索到条目，跳过", tenantId, record.getBillNumber());
        } else {
          Response response = syncClient.sync(config.getTenantId(), transferData);
          if (false == response.isSuccess()) {
            log.error("{} - {}, 同步失败：{}", tenantId, record.getBillNumber(), response.getMessage());
            throw new Exception(response.getMessage());
          }
          log.info("{} - {}, 同步完成", tenantId, record.getBillNumber());
        }
      }

    } catch (Exception e) {
      log.error("同步（" + billUuid + "）失败：" + e.getMessage());
      throw e;
    }
  }

  private List<String> getPromChannels(TransferSyncConfig config, List<String> channels) {
    //获取配置
    log.info("---原促销渠道={}---", channels);
    List<String> promChannels = new ArrayList<>();
    Map<String, List<String>> map = (Map<String, List<String>>) JsonUtil.jsonToObject(config.getSpmsPromChannels(), Map.class);
    if (CollectionUtils.isNotEmpty(channels)) {
      String promChannelKey = channels.get(0);
      Set<String> channelKeys = map.keySet();
      for (String channelKey : channelKeys) {
        if (promChannelKey.equalsIgnoreCase(channelKey)) {
          promChannels = map.get(channelKey);
          break;
        }
      }
    }
    if (CollectionUtils.isEmpty(promChannels)) {
      promChannels = map.get("*");
    }
    log.info("---转换后促销渠道={}---", promChannels);
    return promChannels;
  }

  public void fullSync(String tenantId) throws Exception {
    TransferSyncConfig config = configClient.getConfig(tenantId, TransferSyncConfig.class);
    if (StringUtils.isNotBlank(config.getUrl())) {
      fullSync(tenantId, config);
    }
    config = configClient.getConfig(tenantId, TransferSyncConfig.class, "second");
    if (StringUtils.isNotBlank(config.getUrl())) {
      fullSync(tenantId, config);
    }
  }

  public void fullSync(String tenantId, TransferSyncConfig config) throws Exception {
    log.info("{}-全量同步开始： {}", tenantId, config.getUrl());

    syncSeqGroups(tenantId);
    SpmsSyncClient syncClient = dynamicFeignMgr.getClient(SpmsSyncClient.class, config);

    SpmsTransferTask transferTask = new SpmsTransferTask();
    transferTask.setTransferSource(TRANSFER_SOURCE);
    transferTask.setOcrTime(new Date());
    transferTask.setState(SpmsTransferTaskState.created);
    try {
      // 正在处理的话，将之替换。
      transferTask = syncClient.beginFullSync(config.getTenantId(), transferTask);
    } catch (Exception e) {
      log.error("租户：" + tenantId + ", beginFullSync失败", e);
      throw e;
    }

    try {
      log.info("开始查询促销单：{}", StringUtil.dateToString(new Date(), StringUtil.DEFAULT_DATE_FORMAT));
      List<String> billUuids = promotionBillDao.query4Sync(tenantId);
      log.info("促销单共计{}条：{}", billUuids.size(), StringUtil.dateToString(new Date(), StringUtil.DEFAULT_DATE_FORMAT));
      for (String billUuid : billUuids) {
        transferBill(tenantId, billUuid);
      }

      log.info("{}-全量同步完成", tenantId);
      transferTask.setState(SpmsTransferTaskState.success);
    } catch (Exception e) {
      log.error(MessageFormat.format("租户：{0}, 同步处理失败", tenantId), e);
      transferTask.setState(SpmsTransferTaskState.failed);
      transferTask.setMessage(e.getMessage());
      throw e;
    } finally {
      transferTask.setFinishTime(new Date());
      try {
        syncClient.overFullSync(config.getTenantId(), transferTask);
      } catch (Exception e) {
        log.error("全量同步失败", e);
      }
    }
  }

  private void syncSeqGroups(String tenant) throws BaasException {
    List<ExecSeqGroup> seqGroups = execSeqGroupDao.list(tenant);
    saveSeqGroup(tenant, seqGroups);
  }

  public PromTransferDataV4 getData(PromotionBill bill, TransferSyncConfig config) throws ExpressionException {
    Map<String, String> seqGroupMap = MapUtil.toMap(config.getSeqGroupMap(), ',', '=');
    List<String> seqGroupConflictZeros = CollectionUtil.toList(config.getSeqGroupConflictZeros(), ',');
    Map<String, String> typeSeqGroupMap = MapUtil.toMap(config.getTypeSeqGroupMap(), ',', '=');
    log.info("取得促销单: {}", bill.getBillNumber());
    PromTransferDataV4 target = new PromTransferDataV4();
    target.setBillUuid(bill.getUuid());
    target.setBillNumber(bill.getBillNumber());
    target.setBillCls(bill.getCustomFields().getField11());
    target.setAuditTime(bill.getAuditInfo().getTime());
    target.setTransferSource(TRANSFER_SOURCE);

    PromotionTypeGeneralBillConfig billConfig = configClient.getConfig(bill.getTenantId(), PromotionTypeGeneralBillConfig.class);
    target.setItems(new ArrayList<>());
    for (PromotionItem item : bill.getItems()) {
      PromPomItem ti = new PromPomItem();
      target.getItems().add(ti);

      ti.setBillCls(target.getBillCls());
      ti.setLabel(bill.getLabel());
      ti.setTransferBillNum(item.getUuid());
      ti.setOpportunity(bill.getOpportunity());
      ti.setDescription(bill.getActivityDesc());
      ti.setPromChannels(bill.getPromChannels());
      ti.setExecSeqNumber(bill.getExecSeqNumber());
      ti.setConflictMutexName(bill.getConflictMutexName());
      ti.setExecutionOptions(CollectionUtil.toList(bill.getCustomFields().getField15()));
      if ("score".equals(bill.getType())) { //空白条目-积分加速 或者 空白条目-赠送积分（基础积分，额外积分）
        // 积分赠送放组1100/1200/1300
        ti.setExecSeqGroup(StringUtil.toString(1000 + bill.getExecSeqNumber()));
      } else if (typeSeqGroupMap.containsKey(bill.getType())) {
        ti.setExecSeqGroup(typeSeqGroupMap.get(bill.getType()));
      } else if (seqGroupMap != null && seqGroupMap.get(bill.getExecSeqGroup()) != null) {
        ti.setExecSeqGroup(seqGroupMap.get(bill.getExecSeqGroup()));
      } else {
        ti.setExecSeqGroup(bill.getExecSeqGroup());
      }
      if (bill.getConflictSeqGroups() != null && seqGroupMap != null) {
        ti.setConflictGroups(bill.getConflictSeqGroups().stream().map(seqGroupMap::get).collect(Collectors.toList()));
      }

      PomDocument pom = PomBuilder.toPOM(item.getDocument());
      item.setPomDocument(pom);
      ti.setItemStartTime(pom.getStart());
      ti.setItemFinishTime(pom.getFinish());
      List<PomElement> vouchers = pom.getElementsByFunction(VoucherActivityCondition.FUNCTION.getName());
      if (vouchers != null && vouchers.isEmpty() == false) {
        ti.setVoucherNo(((VoucherActivityCondition) vouchers.get(0)).getActivity().getCode());
      }
      // 转换qpc=0。
      coverQpc(bill, item, config);
      // 转换qpcPrice给促销中台。
      if (config.isConvertQpcPrice() && pom.getProductCondition() instanceof SingleProductCondition) {
        SingleProductCondition productCondition = (SingleProductCondition) pom.getProductCondition();
        List<PomElement> generalExecutions = pom.getElementsByClass(GeneralExecution.class);
        if (generalExecutions.isEmpty() == false) {
          // 切换商品条件的规格
          for (Condition condition : productCondition.getProduct().getConditions()) {
            if (condition.getField() == PomEntityType.product) {
              for (PomEntity entity : condition.getOperand()) {
                entity.setQpc(BigDecimal.ZERO);
              }
            }
          }

          for (PomElement element : generalExecutions) {
            GeneralExecution generalExecution = (GeneralExecution) element;
            if (generalExecution.getForm() == GeneralExecution.Form.price) {
              generalExecution.focusSetForm(GeneralExecution.Form.qpcPrice);
            }
          }
        }
      }

      ti.setUuid(item.getUuid());
      ti.setItemLineNumber(item.getLineNumber());
      ti.setDocument(PomBuilder.toXML(pom));
      ti.setProductUuid(pom.getProductCondition().getProductUuid());
      ti.setConditionExpression(PomConditionRPNCodec.encode(pom.getProductCondition()));

      Map<String, String> extraInfo = new HashMap<>();
      extraInfo.put("name", bill.getCustomFields().getField18());
      extraInfo.put("productRange", bill.getCustomFields().getField19());
      extraInfo.put("overall", StringUtil.toString("全场".equals(bill.getCustomFields().getField19())));
      ti.setExtraInfo(JsonUtil.objectToJson(extraInfo));

      ti.setLimitType(ObjectUtils.defaultIfNull(item.getLimitType(), LimitType.none));
      ti.setLimitMode(item.getLimitMode());
      ti.setLimitLevel(item.getLimitLevel());
      ti.setLimitQuantity(item.getLimitQuantity());
      ti.setLimitType2(ObjectUtils.defaultIfNull(item.getLimitType2(), LimitType.none));
      ti.setLimitMode2(item.getLimitMode2());
      ti.setLimitLevel2(item.getLimitLevel2());
      ti.setLimitQuantity2(item.getLimitQuantity2());
      ti.setLimitType3(ObjectUtils.defaultIfNull(item.getLimitType3(), LimitType.none));
      ti.setLimitMode3(item.getLimitMode3());
      ti.setLimitLevel3(item.getLimitLevel3());
      ti.setLimitQuantity3(item.getLimitQuantity3());
      ti.setLimitType4(ObjectUtils.defaultIfNull(item.getLimitType4(), LimitType.none));
      ti.setLimitMode4(item.getLimitMode4());
      ti.setLimitLevel4(item.getLimitLevel4());
      ti.setLimitQuantity4(item.getLimitQuantity4());
      ti.setLimitFavAmount(item.getLimitFavAmount());
    }

    if (bill.getAllUnit() == false) {
      List<PromPomItemPlan> itemPlans = bill.getJoins().stream().map(join -> {
        PromPomItemPlan tp = new PromPomItemPlan();
        tp.setPlanStartTime(bill.getStart());
        tp.setPlanFinishTime(bill.getFinish());
        if (join instanceof SopPromotionBillJoin && StringUtils.isNotBlank(((SopPromotionBillJoin) join).getStoreTag())) {
          tp.setTagId(join.getJoinUnit().getUuid());
          tp.setTagType(((SopPromotionBillJoin) join).getStoreTag());
        } else {
          tp.setStoreUuid(join.getJoinUnit().getUuid());
          tp.setStoreCode(join.getJoinUnit().getCode());
        }
        return tp;
      }).collect(Collectors.toList());
      target.setPlans(itemPlans);
    } else {
      PromPomItemPlan tp = new PromPomItemPlan();
      tp.setPlanStartTime(bill.getStart());
      tp.setPlanFinishTime(bill.getFinish());
      if (bill.getEffectUnit() != null) {
        tp.setTagId(bill.getEffectUnit().getUuid());
        tp.setTagType("orgGid");
      } else {
        tp.setStoreUuid("-");
        tp.setStoreCode("-");
      }
      target.setPlans(Collections.singletonList(tp));
    }
    return target;
  }

  private void coverQpc(PromotionBill bill, PromotionItem item, TransferSyncConfig syncConfig) {
    List<String> list = com.hd123.rumba.commons.collection.CollectionUtil.toList(syncConfig.getCoverQpc());
    boolean coverQpc = false;
    if (list != null) {
      if (list.contains(bill.getCustomFields().getField11())) {
        coverQpc = true;
      } else if (list.contains(bill.getCustomFields().getField12())) {
        coverQpc = true;
      }
    }

    if (coverQpc == false) {
      return;
    }
    ProductCondition productCondition = item.getPomDocument().getProductCondition();
    productCondition.setQpc(BigDecimal.ZERO);
    if (productCondition instanceof BasketCondition) {
      for (ProductRange productRange : ((BasketCondition) productCondition).getProducts()) {
        for (Condition condition : productRange.getConditions()) {
          for (PomEntity pomEntity : condition.getOperand()) {
            pomEntity.setQpc(BigDecimal.ZERO);
          }
        }
      }
    } else if (productCondition instanceof SingleProductCondition) {
      for (Condition condition : ((SingleProductCondition) productCondition).getProduct().getConditions()) {
        for (PomEntity pomEntity : condition.getOperand()) {
          pomEntity.setQpc(BigDecimal.ZERO);
        }
      }
    }
    try {
      item.setDocument(PomBuilder.toXML(item.getPomDocument()));
      item.setProductExpression(PomConditionRPNCodec.encode(productCondition));
    } catch (ExpressionException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
