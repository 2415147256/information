package com.hd123.baas.sop.service.impl.pomdata;

import com.google.common.base.Enums;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.dao.pomdata.PromotionBillDao;
import com.hd123.baas.sop.service.api.pomdata.SopPromotionBillJoin;
import com.hd123.baas.sop.service.api.pomdata.SopSingleProduct;
import com.hd123.baas.sop.service.api.promotion.EntityType;
import com.hd123.baas.sop.service.api.promotion.PromotionType;
import com.hd123.baas.sop.service.api.promotion.condition.TimePeriodCondition;
import com.hd123.baas.sop.service.api.promotion.execution.ExecutionSet;
import com.hd123.baas.sop.service.api.promotion.execution.TimePeriodExecution;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.h5.pom.ExecOpportunity;
import com.hd123.h5.pom.document.HasThen;
import com.hd123.h5.pom.document.PomBuilder;
import com.hd123.h5.pom.document.PomDocument;
import com.hd123.h5.pom.document.PomEntity;
import com.hd123.h5.pom.document.PomEntityType;
import com.hd123.h5.pom.document.PomExecutionOption;
import com.hd123.h5.pom.document.PomExecutionSet;
import com.hd123.h5.pom.document.PomFunction;
import com.hd123.h5.pom.document.PomProduct;
import com.hd123.h5.pom.document.condition.cycle.CycleCondition;
import com.hd123.h5.pom.document.condition.cycle.DailyCycleCondition;
import com.hd123.h5.pom.document.condition.cycle.DayOfWeek;
import com.hd123.h5.pom.document.condition.cycle.WeeklyCycleCondition;
import com.hd123.h5.pom.document.condition.general.DateRangeCondition;
import com.hd123.h5.pom.document.condition.member.MemberCondition;
import com.hd123.h5.pom.document.condition.product.BasketCondition;
import com.hd123.h5.pom.document.condition.product.MultiProductCondition;
import com.hd123.h5.pom.document.condition.product.PacketCondition;
import com.hd123.h5.pom.document.condition.product.ProductCondition;
import com.hd123.h5.pom.document.condition.product.SingleProductCondition;
import com.hd123.h5.pom.document.condition.step.PriceType;
import com.hd123.h5.pom.document.condition.step.StepCondition;
import com.hd123.h5.pom.document.condition.step.ValueType;
import com.hd123.h5.pom.document.execution.GeneralExecution;
import com.hd123.h5.pom.document.execution.GiftExecution;
import com.hd123.h5.pom.document.execution.MultiPriceExecution;
import com.hd123.h5.pom.document.execution.SpecialPriceExecution;
import com.hd123.h5.pom.document.expression.PomConditionRPNCodec;
import com.hd123.h5.pom.document.range.Condition;
import com.hd123.h5.pom.document.range.Operator;
import com.hd123.h5.pom.document.range.ProductRange;
import com.hd123.pms.util.expression.ExpressionException;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.collection.CollectionUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.spms.commons.bean.OperateInfo;
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.connector.service.mdata.PromUnit;
import com.hd123.spms.service.bill.InputType;
import com.hd123.spms.service.bill.JoinUnitRange;
import com.hd123.spms.service.bill.PromCustomFields;
import com.hd123.spms.service.bill.PromotionBill;
import com.hd123.spms.service.bill.PromotionBillJoin;
import com.hd123.spms.service.bill.PromotionBillState;
import com.hd123.spms.service.bill.PromotionBillType;
import com.hd123.spms.service.bill.PromotionItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PromotionBillGenerateFactory {
  public static final OperateInfo operateInfo = new OperateInfo("系统");

  @Value("${sop-service.pms.exclude-prm.tag-code:prmGood}")
  private String excludePrmTagCode;

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private PromotionBillDao promotionBillDao;
  @Autowired
  private SpmsSyncServiceImpl spmsSyncService;

  public void abort(String tenant, String uuid, Date lastModifyTime) {
    promotionBillDao.updateState(tenant, uuid, PromotionBillState.aborted.name(), lastModifyTime);
    spmsSyncService.submitSync(tenant, uuid);
  }

  public PromotionBill generalBill(PromRule rule) {
    PromotionTypeGeneralBillConfig generalBillConfig = configClient.getConfig(rule.getTenant(), PromotionTypeGeneralBillConfig.class);
    Map<String, String> promotionTypeGeneralBillConfigs = JsonUtil.jsonToObject(JsonUtil.objectToJson(generalBillConfig), Map.class);

    PromotionBill bill = new PromotionBill();
    bill.setUuid(rule.getUuid());
    bill.setTenantId(rule.getTenant());
    bill.setBillNumber(rule.getBillNumber());
    bill.setRemark(rule.getPromNote());
    bill.setSettleNo(StringUtil.dateToString(rule.getCreateInfo().getTime(), "yyyyMM"));
    bill.setAuditInfo(new OperateInfo(rule.getLastModifyInfo().getTime(), "系统"));
    bill.setType("retail");
    bill.setStarterOrgUuid(rule.getStarterOrgUuid());
    bill.setEffectUnit(PromUnit.newInstance(rule.getOrgId()));
    bill.setInputType(InputType.normal);
    bill.setState(PromotionBillState.audited);
    bill.setCreated(rule.getCreateInfo().getTime());
    bill.setLastModified(rule.getLastModifyInfo().getTime());
    bill.setBillType(PromotionBillType.orgUnit);

    assert rule.getPromotion() != null;
    bill.setTemplateName(rule.getPromotion().getPromotionType().name());
    bill.setActivityDesc(rule.getPromotion().getDescription());
    bill.setStart(rule.getDateRangeCondition().getDateRange().getBeginDate());
    bill.setFinish(DateUtils.addSeconds(rule.getDateRangeCondition().getDateRange().getEndDate(), 1));
    bill.setOverall(rule.getPromotion().getProductCondition().getEntityType() == null && rule.getPromotion().getProductCondition().isExcludePrm() == false);
    bill.setPromChannels(rule.getPromChannels());
    bill.setExecSeqNumber(100);
    bill.setExecSeqGroup(promotionTypeGeneralBillConfigs.getOrDefault(rule.getPromotion().getPromotionType() + "ExecGroup", "2"));
    bill.setConflictMutexName(promotionTypeGeneralBillConfigs.get(rule.getPromotion().getPromotionType() + "ConflictMutexName"));

    if (rule.getPromotion().getPromotionType() == PromotionType.preReduce) {
      if (rule.getPromotion().getExecutionOptions() == null) {
        rule.getPromotion().setExecutionOptions(new ArrayList<>());
      }
      rule.getPromotion().getExecutionOptions().add("满足范围条件的所有商品都参与优惠分摊");
    }
    PromCustomFields customFields = new PromCustomFields();
    customFields.setField11(rule.getPromotion().getPromotionType().name());
    customFields.setField12(rule.getPromotion().getPromotionMode());
    customFields.setField15(CollectionUtil.toString(rule.getPromotion().getExecutionOptions()));
    customFields.setField18(rule.getName());

    String productRange = null;
    if (rule.getPromotion().getProductCondition().getEntityType() == null) {
      productRange = "全场";
    } else if (rule.getPromotion().getProductCondition().getEntityType() == EntityType.product) {
      productRange = "指定商品";
    } else if (rule.getPromotion().getProductCondition().getEntityType() == EntityType.category) {
      if (rule.getPromotion().getProductCondition().getItems().size() == 1) {
        UCN category = rule.getPromotion().getProductCondition().getItems().get(0);
        try {
          productRange = category.getName().split("\\[")[0];
        } catch (Exception e) {
          productRange = category.getName();
        }
      } else if (rule.getPromotion().getProductCondition().getItems().size() > 1) {
        productRange = "指定分类";
      }
    }
    if (rule.getPromotion().getProductCondition().isConflictPrm()) {
      bill.setConflictSeqGroups(CollectionUtil.toList(generalBillConfig.getConflictPrmGroups()));
    }
    customFields.setField19(productRange);
    bill.setCustomFields(customFields);

    List<PromotionItem> items = new ArrayList<>();
    if (rule.getPromotion().getPromotionType() == PromotionType.price) {
      items = buildItemsByPrice(rule, bill);
      bill.setOpportunity(ExecOpportunity.enter);
    } else if (rule.getPromotion().getPromotionType() == PromotionType.segmentPrice) {
      items = buildItemsBySegmentPrice(rule, bill);
      bill.setOpportunity(ExecOpportunity.enter);
    } else if (rule.getPromotion().getPromotionType() == PromotionType.fullReduce
        || rule.getPromotion().getPromotionType() == PromotionType.preReduce
        || rule.getPromotion().getPromotionType() == PromotionType.stepReduce
        || rule.getPromotion().getPromotionType() == PromotionType.discount
        || rule.getPromotion().getPromotionType() == PromotionType.priceDiscount
        || rule.getPromotion().getPromotionType() == PromotionType.gift
        || rule.getPromotion().getPromotionType() == PromotionType.specialPrice) {
      if ("DiscountZero".equals(rule.getPromotion().getPromotionMode())
          && StringUtil.toBoolean(promotionTypeGeneralBillConfigs.get("discountZeroSingleProduct"), false)) {
        bill.setExecSeqGroup(promotionTypeGeneralBillConfigs.getOrDefault("discountZeroExecGroup", bill.getExecSeqGroup()));
        bill.setConflictMutexName(null);
        items = buildItemsByStepForm(rule, bill, ExecOpportunity.enter, GeneralExecution.Form.retailPriceDiscount);
        bill.setOpportunity(ExecOpportunity.enter);
      } else {
        items = buildItemsByStep(rule, bill, ExecOpportunity.deal);
        bill.setOpportunity(ExecOpportunity.deal);
      }
    } else if (rule.getPromotion().getPromotionType() == PromotionType.stepDiscount) {
      // 考虑 单品满价是否要换成SingleProductCondition。
      items = buildItemsByStep(rule, bill, ExecOpportunity.deal);
      bill.setOpportunity(ExecOpportunity.deal);
    } else if (rule.getPromotion().getPromotionType() == PromotionType.gdDiscount) {
      items = buildItemsByStep(rule, bill, ExecOpportunity.enter);
      bill.setOpportunity(ExecOpportunity.enter);
    } else if (rule.getPromotion().getPromotionType() == PromotionType.gdGift
        || rule.getPromotion().getPromotionType() == PromotionType.gdSpecialPrice) {
      items = buildItemsBySingleProduct(rule, bill);
      bill.setOpportunity(ExecOpportunity.enter);
    } else if (rule.getPromotion().getPromotionType() == PromotionType.groupDiscount) {
      items = buildItemsByMultiProduct(rule, bill);
      bill.setOpportunity(ExecOpportunity.deal);
    } else if (rule.getPromotion().getPromotionType() == PromotionType.clearDiscount) {
      items = buildItemsByClearDiscount(rule, bill);
      bill.setOpportunity(ExecOpportunity.deal);
    } else if (rule.getPromotion().getPromotionType() == PromotionType.groupPrice) {
      // 组合特价
      items = buildItemsByGroupPrice(rule, bill);
      bill.setOpportunity(ExecOpportunity.deal);
    } else if (rule.getPromotion().getPromotionType() == PromotionType.groupGift) {
      // 组合满赠
      items = buildItemsByGroupGift(rule, bill);
      bill.setOpportunity(ExecOpportunity.deal);
    }

    coverQpc(rule, items, generalBillConfig);
    bill.setItems(items);

    if (rule.getJoinUnits().getAllUnit()) {
      bill.setAllUnit(true);
    } else if (rule.getJoinUnits().getStores() == null || rule.getJoinUnits().getStores().isEmpty()) {
      return null;
    } else {
      bill.setAllUnit(false);
      List<PromotionBillJoin> billJoins = rule.getJoinUnits().getStores().stream().map(store -> {
        SopPromotionBillJoin target = new SopPromotionBillJoin();
        target.setBill(bill);
        target.setJoinInfo(operateInfo);
        target.setJoinUnit(store);
        target.setStart(bill.getStart());
        target.setStoreTag(store.getJoinUnitType());
        target.setUnitRange(JoinUnitRange.organization);
        return target;
      }).collect(Collectors.toList());
      bill.setJoins(billJoins);
    }

    promotionBillDao.save(bill);
    spmsSyncService.submitSync(rule.getTenant(), bill.getUuid());
    return bill;
  }

  /**
   * 构造组合满赠促销单
   *
   * @param rule
   * @param bill
   * @return
   */
  private List<PromotionItem> buildItemsByGroupGift(PromRule rule, PromotionBill bill) {
    PromotionItem target = new PromotionItem();
    target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), 0));
    target.setBill(bill);
    target.setCreated(new Date());
    target.setIsDM(false);

    PomDocument document = PomBuilder.newDocument();
    DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
    document.setExecution(dateRangeCondition);
    HasThen hasThen = dateRangeCondition;

    hasThen = decodeCycleCondition(rule, hasThen);
    hasThen = decodeGroupProductCondition(rule, target, hasThen);
    hasThen = decodeMemberCondition(rule, hasThen);

    PomExecutionSet executionSet = convert(rule.getPromotion().getExecutionSet());
    hasThen.setThen(executionSet);

    target.setPomDocument(document);
    target.setDocument(PomBuilder.toXML(document));

    bill.setConflictGroup(getConflictGroup(document));
    return Collections.singletonList(target);
  }

  /**
   * 构造组合特价促销单
   *
   * @param rule
   * @param bill
   * @return
   */
  private List<PromotionItem> buildItemsByGroupPrice(PromRule rule, PromotionBill bill) {
    PromotionItem target = new PromotionItem();
    target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), 0));
    target.setBill(bill);
    target.setCreated(new Date());
    target.setIsDM(false);

    PomDocument document = PomBuilder.newDocument();
    DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
    document.setExecution(dateRangeCondition);
    HasThen hasThen = dateRangeCondition;

    hasThen = decodeCycleCondition(rule, hasThen);
    hasThen = decodeGroupProductCondition(rule, target, hasThen);
    hasThen = decodeMemberCondition(rule, hasThen);

    PomExecutionSet executionSet = convert(rule.getPromotion().getExecutionSet());
    hasThen.setThen(executionSet);

    target.setPomDocument(document);
    target.setDocument(PomBuilder.toXML(document));

    bill.setConflictGroup(getConflictGroup(document));
    return Collections.singletonList(target);
  }

  private HasThen decodeGroupProductCondition(PromRule rule, PromotionItem promItem, HasThen hasThen) {
    MultiProductCondition target = new MultiProductCondition();
    target.setFunction(MultiProductCondition.FUNCTION_AND);
    rule.getPromotion().getProductCondition().getItems().forEach(item -> {
      MultiProductCondition.Group group = new MultiProductCondition.Group();
      group.setApportionGroupNo(item.getUuid());
      if (item.getApportionRatio() != null) {
        group.setApportionRatio(item.getApportionRatio());
      }
      group.setQuantity(item.getQty());

      ProductRange productRange = convertProductRange(
          Collections.singletonList(item), rule.getPromotion().getProductCondition().getExcludeItems(),
          EntityType.product, ProductRange.SINGLE_MODE);
      group.getProducts().add(productRange);
      target.getGroups().add(group);
    });
    try {
      promItem.setProductUuid("-");
      promItem.setQpc(BigDecimal.ZERO);
      promItem.setProductCondDoc(productConditionToXML(target));
      promItem.setProductExpression(PomConditionRPNCodec.encode(target));
    } catch (ExpressionException e) {
      throw new RuntimeException(e.getMessage());
    }
    hasThen.setThen(target);
    return target;
  }

  private void coverQpc(PromRule rule, List<PromotionItem> items, PromotionTypeGeneralBillConfig generalBillConfig) {
    List<String> list = CollectionUtil.toList(generalBillConfig.getCoverQpc());
    boolean coverQpc = false;
    if (list != null) {
      if (list.contains(rule.getPromotion().getPromotionType().name())) {
        coverQpc = true;
      } else if (list.contains(rule.getPromotion().getPromotionMode())) {
        coverQpc = true;
      }
    }

    if (coverQpc == false) {
      return;
    }
    for (PromotionItem item : items) {
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
        item.setProductCondDoc(productConditionToXML(productCondition));
        item.setProductExpression(PomConditionRPNCodec.encode(productCondition));
      } catch (ExpressionException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  /**
   * 促销类型：促销价
   */
  private List<PromotionItem> buildItemsByPrice(PromRule rule, PromotionBill bill) {
    int lineNo = 0;
    List<PromotionItem> result = new ArrayList<>();
    for (com.hd123.baas.sop.service.api.promotion.condition.ProductCondition.Item item : rule.getPromotion().getProductCondition().getItems()) {
      PromotionItem target = new PromotionItem();
      target.setLineNumber(lineNo);
      target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), String.valueOf(lineNo++)));
      target.setBill(bill);
      target.setCreated(new Date());
      target.setIsDM(false);

      SopSingleProduct singleProduct = new SopSingleProduct();
      singleProduct.setBill(bill);
      singleProduct.setItem(target);
      singleProduct.setBillUuid(bill.getUuid());
      singleProduct.setItemUuid(target.getUuid());
      singleProduct.setTenantId(rule.getTenant());

      PomDocument document = PomBuilder.newDocument();
      DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
      document.setExecution(dateRangeCondition);
      HasThen hasThen = dateRangeCondition;
      hasThen = decodeCycleCondition(rule, hasThen);
      // 周期促销条件，singleProduct记TimeCycle=true
      if (hasThen instanceof CycleCondition) {
        singleProduct.setTimeCycle(true);
      }

      hasThen = decodeProductCondition(target, item, hasThen);
      hasThen = decodeMemberCondition(rule, hasThen);

      PomExecutionSet executionSet = new PomExecutionSet();
      PomExecutionOption executionOption = new PomExecutionOption();
      executionSet.add(executionOption);
      GeneralExecution generalExecution = new GeneralExecution();
      generalExecution.setForm(GeneralExecution.Form.price);
      generalExecution.setValue(item.getPrmPrice());
      executionOption.getItems().add(generalExecution);
      hasThen.setThen(executionSet);

      singleProduct.setQpc(item.getQpc());
      singleProduct.setType(item.getEntityType() == null ? PomEntityType.product : Enums.getIfPresent(PomEntityType.class, item.getEntityType().name()).or(PomEntityType.product));
      singleProduct.setSpecification(item.getSpecification());
      singleProduct.setManufactory(item.getManufactory());
      singleProduct.setMeasureUnit(item.getMeasureUnit());
      singleProduct.setPrice(item.getPrmPrice());
      singleProduct.setMember(rule.getOnlyMember());
      singleProduct.setStart(bill.getStart());
      singleProduct.setFinish(bill.getFinish());
      singleProduct.setAuditTime(bill.getAuditInfo().getTime());
      singleProduct.setQpcStr("1*" + item.getQpc());
      singleProduct.setPromValue(item.getPrmPrice());
      singleProduct.setEntityUuid(item.getUuid());
      singleProduct.setEntityCode(item.getCode());
      singleProduct.setEntityName(item.getName());
      singleProduct.setForm(GeneralExecution.Form.price);
      target.setProducts(Collections.singletonList(singleProduct));

      target.setPomDocument(document);
      target.setDocument(PomBuilder.toXML(document));
      result.add(target);

      bill.setConflictGroup(getConflictGroup(document));
    }
    return result;
  }

  private List<PromotionItem> buildItemsBySegmentPrice(PromRule rule, PromotionBill bill) {
    int lineNo = 0;
    List<PromotionItem> result = new ArrayList<>();
    for (com.hd123.baas.sop.service.api.promotion.condition.ProductCondition.Item item : rule.getPromotion().getProductCondition().getItems()) {
      PromotionItem target = new PromotionItem();
      target.setLineNumber(lineNo);
      target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), String.valueOf(lineNo++)));
      target.setBill(bill);
      target.setCreated(new Date());
      target.setIsDM(false);

      SopSingleProduct singleProduct = new SopSingleProduct();
      singleProduct.setBill(bill);
      singleProduct.setItem(target);
      singleProduct.setBillUuid(bill.getUuid());
      singleProduct.setItemUuid(target.getUuid());
      singleProduct.setTenantId(rule.getTenant());

      PomDocument document = PomBuilder.newDocument();
      DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
      document.setExecution(dateRangeCondition);
      HasThen hasThen = dateRangeCondition;
      hasThen = decodeCycleCondition(rule, hasThen);
      // 周期促销条件，singleProduct记TimeCycle=true
      if (hasThen instanceof CycleCondition) {
        singleProduct.setTimeCycle(true);
      }

      hasThen = decodeProductCondition(target, item, hasThen);
      hasThen = decodeMemberCondition(rule, hasThen);
      Assert.notEmpty(item.getStepPrices(), "stepPrices");

      StepCondition stepCondition = new StepCondition();
      stepCondition.setValueType(ValueType.quantity);
      stepCondition.setPriceType(PriceType.actualPrice);
      stepCondition.setOperator(com.hd123.h5.pom.document.condition.step.Operator.range);
      hasThen.setThen(stepCondition);

      item.getStepPrices().forEach(segmentPrice -> {
        PomExecutionSet executionSet = new PomExecutionSet();
        PomExecutionOption executionOption = new PomExecutionOption();
        executionSet.add(executionOption);
        GeneralExecution generalExecution = new GeneralExecution();
        generalExecution.setForm(GeneralExecution.Form.price);
        generalExecution.setValue(segmentPrice.getPrmPrice());
        executionOption.getItems().add(generalExecution);

        StepCondition.StepCase segmentCase = stepCondition.addCase(new StepCondition.StepCase());
        segmentCase.setValue(segmentPrice.getStepValue());
        segmentCase.setThen(executionSet);
      });

      target.setPomDocument(document);
      target.setDocument(PomBuilder.toXML(document));
      result.add(target);

      bill.setConflictGroup(getConflictGroup(document));
    }
    return result;
  }

  private HasThen decodeProductCondition(
      PromotionItem target,
      com.hd123.baas.sop.service.api.promotion.condition.ProductCondition.Item item,
      HasThen hasThen) {
    SingleProductCondition productCondition = new SingleProductCondition();
    ProductRange productRange = convertProductRange(
        Collections.singletonList(item), Collections.emptyList(), EntityType.product, ProductRange.SINGLE_MODE);
    productCondition.setProduct(productRange);
    try {
      target.setProductUuid(item.getUuid());
      target.setQpc(ObjectUtils.defaultIfNull(item.getQpc(), BigDecimal.ONE));
      target.setProductCondDoc(productConditionToXML(productCondition));
      target.setProductExpression(PomConditionRPNCodec.encode(productCondition));
    } catch (ExpressionException e) {
      throw new RuntimeException(e.getMessage());
    }
    hasThen.setThen(productCondition);
    hasThen = productCondition;
    return hasThen;
  }

  /**
   * 促销类型：普通满减、每满减、阶梯满减、普通折扣、整单买赠、整单换购
   */
  private List<PromotionItem> buildItemsByStep(
      PromRule rule, PromotionBill bill, ExecOpportunity opportunity) {
    PromotionItem target = new PromotionItem();
    target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), 0));
    target.setBill(bill);
    target.setCreated(new Date());
    target.setIsDM(false);

    PomDocument document = PomBuilder.newDocument();
    DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
    document.setExecution(dateRangeCondition);
    HasThen hasThen = dateRangeCondition;
    hasThen = decodeCycleCondition(rule, hasThen);

    if (opportunity == ExecOpportunity.deal) {
      hasThen = decodeBasketCondition(rule, target, hasThen);
    } else if (opportunity == ExecOpportunity.enter) {
      hasThen = decodeSingleProductCondition(rule, target, hasThen);
    }

    hasThen = decodePacketCondition(rule, hasThen);
    hasThen = decodeMemberCondition(rule, hasThen);

    StepCondition stepCondition = decodeStepCondition(rule);
    hasThen.setThen(stepCondition);

    target.setPomDocument(document);
    target.setDocument(PomBuilder.toXML(document));

    bill.setConflictGroup(getConflictGroup(document));
    return Collections.singletonList(target);
  }

  /**
   * 促销类型：普通满减、每满减、阶梯满减、普通折扣、整单买赠、整单换购
   */
  private List<PromotionItem> buildItemsByStepForm(PromRule rule, PromotionBill bill, ExecOpportunity opportunity, GeneralExecution.Form form) {
    PromotionItem target = new PromotionItem();
    target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), 0));
    target.setBill(bill);
    target.setCreated(new Date());
    target.setIsDM(false);

    PomDocument document = PomBuilder.newDocument();
    DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
    document.setExecution(dateRangeCondition);
    HasThen hasThen = dateRangeCondition;
    hasThen = decodeCycleCondition(rule, hasThen);

    if (opportunity == ExecOpportunity.deal) {
      hasThen = decodeBasketCondition(rule, target, hasThen);
    } else if (opportunity == ExecOpportunity.enter) {
      hasThen = decodeSingleProductCondition(rule, target, hasThen);
    }

    hasThen = decodePacketCondition(rule, hasThen);
    hasThen = decodeMemberCondition(rule, hasThen);

    StepCondition stepCondition = decodeStepConditionForm(rule, form);
    hasThen.setThen(stepCondition);

    target.setPomDocument(document);
    target.setDocument(PomBuilder.toXML(document));

    bill.setConflictGroup(getConflictGroup(document));
    return Collections.singletonList(target);
  }

  private BasketCondition decodeBasketCondition(PromRule rule, PromotionItem pomItem, HasThen hasThen) {
    com.hd123.baas.sop.service.api.promotion.condition.ProductCondition productCondition = rule.getPromotion().getProductCondition();

    BasketCondition target = new BasketCondition();
    target.setOverall(productCondition.getEntityType() == null && productCondition.isExcludePrm() == false);

    ProductRange productRange = null;
    if (productCondition.getEntityType() != null && productCondition.getItems() != null && productCondition.getItems().isEmpty() == false) {
      productRange = convertProductRange(
          productCondition.getItems(), productCondition.getExcludeItems(), productCondition.getEntityType(), ProductRange.RANGE_MODE);
    }
    // 全场商品指定排除商品时
    if (productRange == null && productCondition.getExcludeItems().isEmpty() == false) {
      productRange = convertProductRange(
          Collections.emptyList(), productCondition.getExcludeItems(), productCondition.getEntityType(), ProductRange.RANGE_MODE);
    }
    if (productCondition.isExcludePrm()) {
      if (productRange == null) {
        productRange = new ProductRange();
        productRange.setMode(ProductRange.RANGE_MODE);
      }

      Condition condition = new Condition();
      condition.setField(PomEntityType.tag);
      condition.setOperator(Operator.not_equals);
      PomEntity operand = new PomEntity();
      operand.setType(PomEntityType.tag);
      operand.setUuid(excludePrmTagCode);
      operand.setCode(excludePrmTagCode);
      operand.setName("促销商品");
      condition.setOperand(Collections.singletonList(operand));
      productRange.getConditions().add(condition);
    }
    if (productRange != null) {
      target.getProducts().add(productRange);
    }

    try {
      pomItem.setQpc(BigDecimal.ZERO);
      pomItem.setProductUuid("-");
      pomItem.setProductCondDoc(productConditionToXML(target));
      pomItem.setProductExpression(PomConditionRPNCodec.encode(target));
    } catch (ExpressionException e) {
      throw new RuntimeException(e.getMessage());
    }
    hasThen.setThen(target);
    return target;
  }

  private StepCondition decodeStepCondition(PromRule rule) {
    StepCondition targetCondition = new StepCondition();
    com.hd123.baas.sop.service.api.promotion.condition.StepCondition stepCondition = rule.getPromotion().getStepCondition();
    targetCondition.setValueType(StringUtil.toEnum(stepCondition.getValueType().name(), ValueType.class));
    targetCondition.setPriceType(StringUtil.toEnum(stepCondition.getPriceType().name(), PriceType.class));
    targetCondition.setOperator(StringUtil.toEnum(stepCondition.getOperator().name(), com.hd123.h5.pom.document.condition.step.Operator.class));

    List<StepCondition.StepCase> stepCases = rule.getPromotion().getStepCondition().getStepCases().stream().map(e -> {
      StepCondition.StepCase stepCase = new StepCondition.StepCase();
      stepCase.setValue(e.getValue());
      stepCase.setThen(convert(e.getExecutionSet()));
      return stepCase;
    }).collect(Collectors.toList());
    targetCondition.getCases().addAll(stepCases);
    return targetCondition;
  }

  private StepCondition decodeStepConditionForm(PromRule rule, GeneralExecution.Form form) {
    StepCondition targetCondition = new StepCondition();
    com.hd123.baas.sop.service.api.promotion.condition.StepCondition stepCondition = rule.getPromotion().getStepCondition();
    targetCondition.setValueType(StringUtil.toEnum(stepCondition.getValueType().name(), ValueType.class));
    targetCondition.setPriceType(StringUtil.toEnum(stepCondition.getPriceType().name(), PriceType.class));
    targetCondition.setOperator(StringUtil.toEnum(stepCondition.getOperator().name(), com.hd123.h5.pom.document.condition.step.Operator.class));

    List<StepCondition.StepCase> stepCases = rule.getPromotion().getStepCondition().getStepCases().stream().map(e -> {
      StepCondition.StepCase stepCase = new StepCondition.StepCase();
      stepCase.setValue(e.getValue());

      PomExecutionSet executionSet = new PomExecutionSet();
      PomExecutionOption executionOption = new PomExecutionOption();
      executionSet.getOptions().add(executionOption);
      GeneralExecution generalExecution = new GeneralExecution();
      BeanUtils.copyProperties(e.getExecutionSet().getGeneralExecution(), generalExecution);
      generalExecution.setForm(form);
      executionOption.getItems().add(generalExecution);
      stepCase.setThen(executionSet);
      return stepCase;
    }).collect(Collectors.toList());
    targetCondition.getCases().addAll(stepCases);
    return targetCondition;
  }

  /**
   * 促销类型：单品买赠、单品换购
   */
  private List<PromotionItem> buildItemsBySingleProduct(PromRule rule, PromotionBill bill) {
    PromotionItem target = new PromotionItem();
    target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), 0));
    target.setBill(bill);
    target.setCreated(new Date());
    target.setIsDM(false);

    PomDocument document = PomBuilder.newDocument();
    DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
    document.setExecution(dateRangeCondition);
    HasThen hasThen = dateRangeCondition;

    hasThen = decodeCycleCondition(rule, hasThen);
    hasThen = decodeSingleProductCondition(rule, target, hasThen);
    hasThen = decodePacketCondition(rule, hasThen);
    hasThen = decodeMemberCondition(rule, hasThen);

    if (rule.getPromotion().getStepCondition() != null
        && rule.getPromotion().getStepCondition().getStepCases() != null
        && rule.getPromotion().getStepCondition().getStepCases().isEmpty() == false
    ) {
      StepCondition stepCondition = decodeStepCondition(rule);
      hasThen.setThen(stepCondition);
    } else {
      PomExecutionSet executionSet = convert(rule.getPromotion().getExecutionSet());
      hasThen.setThen(executionSet);
    }

    target.setPomDocument(document);
    target.setDocument(PomBuilder.toXML(document));

    bill.setConflictGroup(getConflictGroup(document));
    return Collections.singletonList(target);
  }

  private SingleProductCondition decodeSingleProductCondition(PromRule rule, PromotionItem promItem, HasThen hasThen) {
    SingleProductCondition target = new SingleProductCondition();
    com.hd123.baas.sop.service.api.promotion.condition.ProductCondition productCondition = rule.getPromotion().getProductCondition();
    ProductRange productRange = convertProductRange(
        productCondition.getItems(), productCondition.getExcludeItems(), productCondition.getEntityType(), ProductRange.SINGLE_MODE);
    if (productCondition.getItems().isEmpty()) {
      List<Condition> conditions = new ArrayList<>();
      Condition condition = new Condition();
      condition.setField(PomEntityType.category);
      condition.setOperator(Operator.equals);
      conditions.add(condition);

      PomEntity pomEntity = new PomEntity();
      pomEntity.setType(condition.getField());
      pomEntity.setUuid("");
      pomEntity.setCode("");
      pomEntity.setName("");
      pomEntity.setQpc(BigDecimal.ZERO);
      condition.setOperand(Arrays.asList(pomEntity));

      List<com.hd123.baas.sop.service.api.promotion.PomEntity> excludeItems = productCondition.getExcludeItems();
      if (excludeItems != null && excludeItems.isEmpty() == false) {
        Condition condition2 = new Condition();
        condition2.setField(PomEntityType.product);
        condition2.setOperator(Operator.not_equals);
        condition2.setOperand(excludeItems.stream().map(item -> {
          PomEntity pe = new PomProduct();
          pe.setType(PomEntityType.product);
          pe.setUuid(item.getUuid());
          pe.setCode(item.getCode());
          pe.setName(item.getName());
          pe.setQpc(ObjectUtils.defaultIfNull(item.getQpc(), BigDecimal.ONE));
          return pe;
        }).collect(Collectors.toList()));
        conditions.add(condition2);
      }
      productRange.setConditions(conditions);
    }
    target.setProduct(productRange);
    try {
      promItem.setProductUuid(getProductUuid(target));
      promItem.setQpc(BigDecimal.ZERO);
      promItem.setProductCondDoc(productConditionToXML(target));
      promItem.setProductExpression(PomConditionRPNCodec.encode(target));
    } catch (ExpressionException e) {
      throw new RuntimeException(e.getMessage());
    }
    hasThen.setThen(target);
    return target;
  }

  /**
   * 促销类型：组合折扣
   */
  private List<PromotionItem> buildItemsByMultiProduct(PromRule rule, PromotionBill bill) {
    PromotionItem target = new PromotionItem();
    target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), 0));
    target.setBill(bill);
    target.setCreated(new Date());
    target.setIsDM(false);

    PomDocument document = PomBuilder.newDocument();
    DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
    document.setExecution(dateRangeCondition);
    HasThen hasThen = dateRangeCondition;

    hasThen = decodeCycleCondition(rule, hasThen);
    hasThen = decodeMultiProductCondition(rule, target, hasThen);
    hasThen = decodeMemberCondition(rule, hasThen);

    PomExecutionSet executionSet = convert(rule.getPromotion().getExecutionSet());
    hasThen.setThen(executionSet);

    target.setPomDocument(document);
    target.setDocument(PomBuilder.toXML(document));

    bill.setConflictGroup(getConflictGroup(document));
    return Collections.singletonList(target);
  }

  /**
   * 促销类型：组合折扣
   */
  private List<PromotionItem> buildItemsByBasketProduct(PromRule rule, PromotionBill bill) {
    PromotionItem target = new PromotionItem();
    target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), 0));
    target.setBill(bill);
    target.setCreated(new Date());
    target.setIsDM(false);

    PomDocument document = PomBuilder.newDocument();
    DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
    document.setExecution(dateRangeCondition);
    HasThen hasThen = dateRangeCondition;

    hasThen = decodeCycleCondition(rule, hasThen);
    hasThen = decodeBasketCondition(rule, target, hasThen);
    hasThen = decodeMemberCondition(rule, hasThen);

    PomExecutionSet executionSet = convert(rule.getPromotion().getExecutionSet());
    hasThen.setThen(executionSet);

    target.setPomDocument(document);
    target.setDocument(PomBuilder.toXML(document));

    bill.setConflictGroup(getConflictGroup(document));
    return Collections.singletonList(target);
  }

  private MultiProductCondition decodeMultiProductCondition(PromRule rule, PromotionItem promItem, HasThen hasThen) {
    MultiProductCondition target = new MultiProductCondition();
    target.setFunction(MultiProductCondition.FUNCTION_AND);
    rule.getPromotion().getProductCondition().getItems().forEach(item -> {
      MultiProductCondition.Group group = new MultiProductCondition.Group();
      group.setQuantity(BigDecimal.ONE);
      ProductRange productRange = convertProductRange(
          Collections.singletonList(item), rule.getPromotion().getProductCondition().getExcludeItems(),
          EntityType.product, ProductRange.SINGLE_MODE);
      group.getProducts().add(productRange);
      target.getGroups().add(group);
    });
    try {
      promItem.setProductUuid("-");
      promItem.setQpc(BigDecimal.ZERO);
      promItem.setProductCondDoc(productConditionToXML(target));
      promItem.setProductExpression(PomConditionRPNCodec.encode(target));
    } catch (ExpressionException e) {
      throw new RuntimeException(e.getMessage());
    }
    hasThen.setThen(target);
    return target;
  }

  /**
   * 促销类型：清仓折扣
   */
  private List<PromotionItem> buildItemsByClearDiscount(PromRule rule, PromotionBill bill) {
    if ("ClearDiscountAll".equals(rule.getPromotion().getPromotionMode())) {
      return buildItemsByBasketProduct(rule, bill);
    }

    TimePeriodExecution timePeriodExecution = rule.getPromotion().getExecutionSet().getTimePeriodExecution();
    List<PromotionItem> items = new ArrayList<>();
    if (timePeriodExecution == null) {
      return items;
    }
    int lineNo = 0;
    for (TimePeriodExecution.Item item : timePeriodExecution.getItems()) {
      PromotionItem target = new PromotionItem();
      target.setLineNumber(lineNo);
      target.setUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), String.valueOf(lineNo++)));
      target.setBill(bill);
      target.setCreated(new Date());
      target.setIsDM(false);

      PomDocument document = PomBuilder.newDocument();
      DateRangeCondition dateRangeCondition = decodeDateRangeCondition(rule);
      document.setExecution(dateRangeCondition);
      HasThen hasThen = dateRangeCondition;

      hasThen = decodeCycleCondition(rule, hasThen);
      CycleCondition cycleCondition;
      if (hasThen instanceof CycleCondition) {
        cycleCondition = (CycleCondition) hasThen;
      } else {
        cycleCondition = new DailyCycleCondition();
        hasThen.setThen(cycleCondition);
        hasThen = cycleCondition;
      }

      DateRange period = new DateRange(item.getPeriod().getStart(), item.getPeriod().getFinish());
      cycleCondition.setPeriods(Collections.singletonList(period));

      hasThen = decodeBasketCondition(rule, target, hasThen);
      hasThen = decodeMemberCondition(rule, hasThen);

      PomExecutionSet executionSet = new PomExecutionSet();
      PomExecutionOption executionOption = new PomExecutionOption();
      hasThen.setThen(executionSet);
      executionSet.add(executionOption);

      GeneralExecution generalExecution = new GeneralExecution();
      generalExecution.setValue(item.getValue());
      generalExecution.setForm(StringUtil.toEnum(item.getForm().name(), GeneralExecution.Form.class));
      executionOption.getItems().add(generalExecution);

      target.setPomDocument(document);
      target.setDocument(PomBuilder.toXML(document));
      items.add(target);

      bill.setConflictGroup(getConflictGroup(document));
    }
    return items;
  }

  private DateRangeCondition decodeDateRangeCondition(PromRule rule) {
    DateRangeCondition dateRangeCondition = new DateRangeCondition();
    dateRangeCondition.setStart(rule.getDateRangeCondition().getDateRange().getBeginDate());
    dateRangeCondition.setFinish(DateUtils.addSeconds(rule.getDateRangeCondition().getDateRange().getEndDate(), 1));
    return dateRangeCondition;
  }

  private HasThen decodeCycleCondition(PromRule rule, HasThen hasThen) {
    CycleCondition cycleCondition = null;
    WeeklyCycleCondition weeklyCycleCondition = new WeeklyCycleCondition();
    if (rule.getDateRangeCondition().getTimeCycle() != null) {
      com.hd123.baas.sop.service.api.promotion.condition.DateRangeCondition.DateRangeCycle timeCycle = rule.getDateRangeCondition().getTimeCycle();
      if (timeCycle.getDayOfWeek() != null && timeCycle.getDayOfWeek().isEmpty() == false) {
        if (timeCycle.getBy() == com.hd123.baas.sop.service.api.promotion.condition.DateRangeCondition.By.week) {
          weeklyCycleCondition.setEvery(timeCycle.getEvery());
          if (timeCycle.getDayOfWeek() != null) {
            Set<DayOfWeek> dayOfWeeks = timeCycle.getDayOfWeek().stream().map(day -> StringUtil.toEnum(day.name(), DayOfWeek.class)).collect(Collectors.toSet());
            weeklyCycleCondition.setDayOfWeeks(dayOfWeeks);
          }
        }
        cycleCondition = weeklyCycleCondition;
      }
    }

    if (rule.getTimePeriodCondition() != null) {
      if (cycleCondition == null) {
        DailyCycleCondition dailyCycleCondition = new DailyCycleCondition();
        dailyCycleCondition.setEvery(1);
        cycleCondition = dailyCycleCondition;
      }
      for (TimePeriodCondition.TimeRange timeRange : rule.getTimePeriodCondition().getPeriods()) {
        cycleCondition.getPeriods().add(new DateRange(timeRange.getStart(), timeRange.getFinish()));
      }
    } else if (cycleCondition != null) {
      try {
        Date zeroTime = StringUtil.toDate("2020-01-01", "yyyy-MM-dd");
        cycleCondition.setPeriods(Collections.singletonList(new DateRange(zeroTime, zeroTime)));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    if (cycleCondition != null) {
      hasThen.setThen(cycleCondition);
      hasThen = cycleCondition;
    }
    return hasThen;
  }

  private HasThen decodePacketCondition(PromRule rule, HasThen hasThen) {
    com.hd123.baas.sop.service.api.promotion.condition.PacketCondition sourceCondition = rule.getPromotion().getPacketCondition();
    if (sourceCondition == null) {
      return hasThen;
    }

    PacketCondition targetCondition = new PacketCondition();
    targetCondition.setScope(StringUtil.toEnum(sourceCondition.getScope(), PacketCondition.Scope.class));
    targetCondition.setType(StringUtil.toEnum(sourceCondition.getType(), PacketCondition.Type.class));
    hasThen.setThen(targetCondition);
    return targetCondition;
  }

  private HasThen decodeMemberCondition(PromRule rule, HasThen hasThen) {
    if (rule.getOnlyMember() == Boolean.TRUE) {
      MemberCondition memberCondition = new MemberCondition();
      hasThen.setThen(memberCondition);

      MemberCondition.MemberCase memberCase = new MemberCondition.MemberCase();
      memberCase.setTypes(Collections.singletonList("会员"));
      memberCondition.getCases().add(memberCase);
      hasThen = memberCase;
    }
    return hasThen;
  }

  private String productConditionToXML(ProductCondition productCondition) {
    Document doc = DocumentHelper.createDocument();
    doc.setXMLEncoding("GBK");
    doc.add(productCondition.writeToXML(doc));
    return doc.asXML();
  }

  private ProductRange convertProductRange(
      List<com.hd123.baas.sop.service.api.promotion.condition.ProductCondition.Item> items,
      List<com.hd123.baas.sop.service.api.promotion.PomEntity> excludeItems,
      EntityType entityType, String mode) {
    ProductRange productRange = new ProductRange();
    productRange.setMode(mode);

    if (items != null && items.isEmpty() == false) {
      Condition condition = new Condition();
      condition.setField(entityType == null ? PomEntityType.product : StringUtil.toEnum(entityType.name(), PomEntityType.class));
      condition.setOperator(Operator.equals);
      List<PomEntity> operand = items.stream().map(item -> {
        PomEntity pomEntity = condition.getField() == PomEntityType.product ? new PomProduct() : new PomEntity();
        pomEntity.setType(condition.getField());
        pomEntity.setUuid(item.getUuid());
        pomEntity.setCode(item.getCode());
        pomEntity.setName(item.getName());
        pomEntity.setQpc(ObjectUtils.defaultIfNull(item.getQpc(), BigDecimal.ONE));
        return pomEntity;
      }).collect(Collectors.toList());
      condition.setOperand(operand);
      productRange.getConditions().add(condition);
    }

    if (excludeItems != null && excludeItems.isEmpty() == false) {
      Condition condition2 = new Condition();
      condition2.setField(PomEntityType.product);
      condition2.setOperator(Operator.not_equals);
      condition2.setOperand(excludeItems.stream().map(item -> {
        PomEntity pomEntity = new PomProduct();
        pomEntity.setType(PomEntityType.product);
        pomEntity.setUuid(item.getUuid());
        pomEntity.setCode(item.getCode());
        pomEntity.setName(item.getName());
        pomEntity.setQpc(ObjectUtils.defaultIfNull(item.getQpc(), BigDecimal.ONE));
        return pomEntity;
      }).collect(Collectors.toList()));
      productRange.getConditions().add(condition2);
    }
    return productRange;
  }

  /**
   * 转换 优惠集合：一般促销优惠、换购优惠、赠品优惠,组合优惠
   */
  private PomExecutionSet convert(ExecutionSet source) {
    PomExecutionSet target = new PomExecutionSet();
    if (source == null) {
      return target;
    }
    PomExecutionOption executionOption = new PomExecutionOption();
    target.getOptions().add(executionOption);
    if (source.getGeneralExecution() != null) {
      executionOption.getItems().add(convert(source.getGeneralExecution()));
    }
    if (source.getGiftExecution() != null) {
      executionOption.getItems().add(convert(source.getGiftExecution()));
    }
    if (source.getSpecialPriceExecution() != null) {
      executionOption.getItems().add(convert(source.getSpecialPriceExecution()));
    }

    if (source.getMultiPriceExecution() != null) {
      executionOption.getItems().add(convert(source.getMultiPriceExecution()));
    }
    return target;
  }

  private MultiPriceExecution convert(com.hd123.baas.sop.service.api.promotion.execution.MultiPriceExecution source) {

    MultiPriceExecution targetExecution = new MultiPriceExecution();
    List<MultiPriceExecution.ProductPrice> productPriceList= source.getItems().stream().map(i->{

      MultiPriceExecution.ProductPrice price = new MultiPriceExecution.ProductPrice();
      BeanUtils.copyProperties(i, price);
      price.setPrice(i.getPrmPrice());
      Condition condition = new Condition();
      condition.setField(PomEntityType.product);
      condition.setOperator(Operator.equals);
      condition.setOperand(new ArrayList<>());
      PomEntity pomEntity = new PomEntity(i.getUuid(),i.getCode(),i.getName(),PomEntityType.product);
      pomEntity.setQpc(i.getQpc());
      condition.getOperand().add(pomEntity);
      price.getConditions().add(condition);
      return price;

    }).collect(Collectors.toList());
    targetExecution.getProducts().addAll(productPriceList);

    return targetExecution;
  }

  /**
   * 转换 一般促销优惠
   */
  private GeneralExecution convert(com.hd123.baas.sop.service.api.promotion.execution.GeneralExecution source) {
    GeneralExecution targetExecution = new GeneralExecution();
    BeanUtils.copyProperties(source, targetExecution);
    targetExecution.setForm(StringUtil.toEnum(source.getForm().name(), GeneralExecution.Form.class));
    return targetExecution;
  }

  /**
   * 转换 换购优惠
   */
  private SpecialPriceExecution convert(com.hd123.baas.sop.service.api.promotion.execution.SpecialPriceExecution source) {
    SpecialPriceExecution specialPriceExecution = new SpecialPriceExecution();
    List<SpecialPriceExecution.GroupItem> groupItems = source.getItems().stream().map(item -> {
      SpecialPriceExecution.GroupItem groupItem = new SpecialPriceExecution.GroupItem();
      BeanUtils.copyProperties(item, groupItem);
      groupItem.setForm(StringUtil.toEnum(item.getForm().name(), SpecialPriceExecution.Form.class));
      PomEntity pomEntity = new PomProduct();
      BeanUtils.copyProperties(item.getEntity(), pomEntity);
      pomEntity.setType(PomEntityType.product);
      groupItem.setEntity(pomEntity);
      return groupItem;
    }).collect(Collectors.toList());
    SpecialPriceExecution.Group specialPriceGroup = new SpecialPriceExecution.Group();
    specialPriceGroup.setItems(groupItems);
    specialPriceExecution.setFunction(SpecialPriceExecution.FUNCTION);
    specialPriceExecution.getGroups().add(specialPriceGroup);
    return specialPriceExecution;
  }

  /**
   * 转换 赠品优惠
   */
  private GiftExecution convert(com.hd123.baas.sop.service.api.promotion.execution.GiftExecution source) {
    GiftExecution giftExecution = new GiftExecution();
    giftExecution.setFunction(GiftExecution.FUNCTION);
    if (source.getGiftType() == com.hd123.baas.sop.service.api.promotion.execution.GiftExecution.GiftType.one) {
      List<PomEntity> entities = source.getEntities().stream().map(entity -> {
        PomEntity pomEntity = new PomProduct();
        BeanUtils.copyProperties(entity, pomEntity);
        pomEntity.setType(PomEntityType.product);
        return pomEntity;
      }).collect(Collectors.toList());
      GiftExecution.Group giftGroup = new GiftExecution.Group();
      giftGroup.setEntities(entities);
      giftExecution.setFunction(GiftExecution.FUNCTION);
      giftExecution.getGroups().add(giftGroup);
    } else {
      List<GiftExecution.Group> giftGroups = source.getEntities().stream().map(entity -> {
        PomEntity pomEntity = new PomProduct();
        BeanUtils.copyProperties(entity, pomEntity);
        pomEntity.setType(PomEntityType.product);

        GiftExecution.Group giftGroup = new GiftExecution.Group();
        giftGroup.setEntities(Collections.singletonList(pomEntity));
        giftGroup.setQuantity(entity.getGiftQty());
        if (entity.getApportionRatio() != null) {
          giftGroup.setPortionScale(entity.getApportionRatio());
        }
        return giftGroup;
      }).collect(Collectors.toList());
      giftExecution.getGroups().addAll(giftGroups);
    }
    return giftExecution;
  }

  protected String getProductUuid(ProductCondition productCondition) {
    if (productCondition instanceof BasketCondition) {
      // 存在必选商品的时候，无法设置productUuid
      BasketCondition basketCondition = (BasketCondition) productCondition;
      if (basketCondition.getRequiredProducts() != null && basketCondition.getRequiredProducts().isEmpty() == false)
        return "-";
    }

    Set<PomEntity> entities = productCondition.getRefEntitySet(false);
    if (entities.size() == 1) {
      PomEntity entity = entities.iterator().next();
      if (entity instanceof PomProduct) {
        return entity.getUuid();
      }
    }
    return "-";
  }

  /**
   * 取得冲突检查分组。
   */
  private String getConflictGroup(PomDocument pom) {
    assert pom != null;

    List<PomFunction> functionList = pom.getFunctionList();
    if (functionList.contains(SingleProductCondition.FUNCTION))
      return SingleProductCondition.FUNCTION.getName();
    else if (functionList.contains(MultiProductCondition.FUNCTION))
      return MultiProductCondition.FUNCTION.getName();
    else if (functionList.contains(MultiProductCondition.FUNCTION_AND))
      return MultiProductCondition.FUNCTION.getName();
    else if (functionList.contains(MultiProductCondition.FUNCTION_OR))
      return MultiProductCondition.FUNCTION.getName();
    else if (functionList.contains(BasketCondition.FUNCTION))
      return BasketCondition.FUNCTION.getName();
    return null;
  }
}
