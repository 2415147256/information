package com.hd123.baas.sop.service.impl.price.pricepromotion;

import com.google.common.collect.Lists;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.config.PromotionActivityConfig;
import com.hd123.baas.sop.service.api.group.SkuGroupCategory;
import com.hd123.baas.sop.service.api.group.SkuGroupService;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfig;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfigService;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustment;
import com.hd123.baas.sop.service.api.price.pricepromotion.*;
import com.hd123.baas.sop.service.api.price.shopprice.ShopPricePromotionManagerService;
import com.hd123.baas.sop.service.api.price.shopprice.ShopPricePromotionService;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanService;
import com.hd123.baas.sop.service.dao.price.config.PriceSkuConfigDaoBof;
import com.hd123.baas.sop.service.dao.price.pricepromotion.PricePromotionDaoBof;
import com.hd123.baas.sop.service.dao.price.pricepromotion.PricePromotionLineDaoBof;
import com.hd123.baas.sop.service.dao.price.pricepromotion.PricePromotionShopDaoBof;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.price.*;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/13.
 */
@Service
@Slf4j
public class PricePromotionServiceImpl implements PricePromotionService {

  @Autowired
  private PricePromotionDaoBof promotionDao;
  @Autowired
  private PricePromotionLineDaoBof promotionLineDao;
  @Autowired
  private PricePromotionShopDaoBof promotionShopDao;
  @Autowired
  private PriceSkuConfigDaoBof priceSkuConfigDao;
  @Autowired
  private StoreService storeService;
  @Autowired
  private BillNumberMgr billNumberMgr;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private SubsidyPlanService subsidyPlanService;
  @Autowired
  private ShopPricePromotionManagerService shopPricePromotionManagerService;
  @Autowired
  private ShopPricePromotionService shopPricePromotionService;
  @Autowired
  private PriceSkuConfigService skuConfigService;
  @Autowired
  private SkuGroupService skuGroupService;
  @Autowired
  private BaasConfigClient configClient;

  @Override
  public QueryResult<PricePromotion> query(String tenant, QueryDefinition qd, String... fetchParts) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    QueryResult<PricePromotion> result = promotionDao.query(tenant, qd);
    fetch(tenant, result.getRecords(), fetchParts);
    return result;
  }

  @Override
  public PricePromotion get(String tenant, String uuid, String... fetchParts) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    PricePromotion promotion = promotionDao.get(tenant, uuid);
    if (promotion != null) {
      List<PricePromotion> promotions = new ArrayList<>();
      promotions.add(promotion);
      fetch(tenant, promotions, fetchParts);
    }
    return promotion;
  }

  @Override
  public List<PricePromotion> list(String tenant, List<String> uuids, String... fetchParts) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(uuids, "uuids");
    List<PricePromotion> list = promotionDao.list(tenant, uuids);
    fetch(tenant, list, fetchParts);
    return list;
  }

  private void fetch(String tenant, Collection<PricePromotion> promotions, String[] fetchParts) {
    if (CollectionUtils.isEmpty(promotions)) {
      return;
    }
    if (fetchParts == null || fetchParts.length == 0) {
      return;
    }
    Map<String, PricePromotion> promotionMap = promotions.stream().collect(Collectors.toMap(Entity::getUuid, p -> p));
    if (ArrayUtils.contains(fetchParts, PricePromotion.FETCH_LINE)) {
      promotions.forEach(i -> {
        String uuid = i.getUuid();
        QueryResult<PricePromotionLine> query = promotionLineDao.query(tenant, uuid, new QueryDefinition());
        i.setLines(query.getRecords());
      });
    }
    if (ArrayUtils.contains(fetchParts, PricePromotion.FETCH_SHOP)) {
      if (CollectionUtils.isNotEmpty(promotionMap.keySet())) {
        List<PricePromotionShop> shops = promotionShopDao.listByOwners(tenant, promotionMap.keySet());
        shops.forEach(s -> {
          PricePromotion p = promotionMap.get(s.getOwner());
          if (p == null) {
            return;
          }
          if (p.getShops() == null) {
            p.setShops(new ArrayList<>());
          }
          p.getShops().add(s);
        });
      }
    }
  }

  @Override
  public QueryResult<PricePromotionLine> queryLine(String tenant, String uuid, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(qd, "qd");
    return promotionLineDao.query(tenant, uuid, qd);
  }

  @Override
  public PricePromotion create(String tenant, String orgId, String type, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operateInfo, "operateInfo");
    Assert.notNull(type, "促销单类型");
    PricePromotion promotion = new PricePromotion();
    promotion.setAllShops(true);
    if (StringUtils.isBlank(type)) {
      type = PricePromotionType.SKU_LIMIT_PRMT.name();
    }
    promotion.setType(PricePromotionType.valueOf(type));
    promotion.setState(PricePromotionState.INIT);
    promotion.setFlowNo(null); // 草稿状态不需要这个
    promotion.setUuid(UUID.randomUUID().toString());
    promotion.setOrgId(orgId);
    promotionDao.insert(tenant, promotion, operateInfo);
    return promotion;
  }

  @Override
  @Tx
  public void saveAndSubmit(String tenant, PricePromotion promotion, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(promotion, "promotion");
    Assert.hasText(promotion.getUuid(), "promotion.uuid");
    Assert.notNull(promotion.getEffectiveStartDate(), "promotion.effectiveStartDate");
    Assert.notNull(promotion.getEffectiveEndDate(), "promotion.effectiveEndDate");

    PricePromotion ever = this.get(tenant, promotion.getUuid());
    if (ever == null) {
      throw new BaasException("单据不存在");
    }
    if (ever.getType() == null) {
      throw new BaasException("请选择促销类型");
    }
    promotion.setType(ever.getType());

    promotion.setEffectiveStartDate(DateUtils.truncate(promotion.getEffectiveStartDate(), Calendar.DATE));
    promotion.setEffectiveEndDate(DateUtils.truncate(promotion.getEffectiveEndDate(), Calendar.DATE));
    // 生效开始时间 <= 结束时间
    if (promotion.getEffectiveEndDate().before(promotion.getEffectiveStartDate())) {
      throw new BaasException("生效结束时间必须晚于开始时间");
    }

    promotion.setOrgId(ever.getOrgId());
    promotion.setCreateInfo(ever.getCreateInfo());
    promotion.setState(ever.getState());
    promotion.setFlowNo(ever.getFlowNo());
    checkEdit(promotion);
    if (promotion.getState() == PricePromotionState.INIT) {
      promotion.setState(PricePromotionState.CONFIRMED);
      promotion.setFlowNo(billNumberMgr.generatePricePromotionFlowNo(tenant));
    }
    promotionDao.update(tenant, promotion, operateInfo);

    // 如果总部承担比例为0，则自动审核
/*    if (promotion.getHeadSharingRate().compareTo(BigDecimal.ZERO) == 0) {
      PricePromotionAutoAuditMsg msg = new PricePromotionAutoAuditMsg();
      msg.setUuid(promotion.getUuid());
      msg.setTenant(tenant);
      msg.setTraceId(MDC.get("trace_id"));
      publisher.publishForNormal(PricePromotionAutoAuditEvCallExecutor.PRICE_PROMOTION_AUTO_AUDIT_EXECUTOR_ID, msg);
    }*/
  }

  @Override
  public void addLines(String tenant, String uuid, Collection<String> skuIds, PricePromotionLineType type, String rule,
                       String skuGroup, String skuGroupName, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(type, "type");
    Assert.hasText(rule, "rule");
    if (CollectionUtils.isEmpty(skuIds) && StringUtils.isBlank(skuGroup)) {
      return;
    }
    PricePromotion promotion = this.get(tenant, uuid);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    if (promotion.getType() == null) {
      throw new BaasException("请先选择单据促销类型");
    }
    checkEdit(promotion);
    // 比较
    List<PricePromotionLine> updates = new ArrayList<>();
    List<PricePromotionLine> inserts = new ArrayList<>();
    if (PricePromotionType.FULL_DISCOUNT_PRMT.equals(promotion.getType())) {
      Assert.notNull(skuGroup, "全场折扣促销下,自定义类别不允许为空");
      Assert.notNull(skuGroupName, "全场折扣促销下,自定义类别名称不允许为空");
      addFullDiscount(tenant, uuid, type, rule, skuGroup, skuGroupName, updates, inserts);
    } else {
      addSkulimt(tenant, promotion.getOrgId(), uuid, skuIds, type, rule, updates, inserts);
    }

    if (PricePromotionType.FULL_DISCOUNT_PRMT.equals(promotion.getType())) {
      updates.stream().forEach(i -> {
        PriceSku sku = i.getSku();
        sku.setId("*");
        sku.setCode("*");
        sku.setQpc(BigDecimal.ONE);
        sku.setName("");
      });
      inserts.stream().forEach(i -> {
        PriceSku sku = i.getSku();
        sku.setId("*");
        sku.setCode("*");
        sku.setQpc(BigDecimal.ONE);
        sku.setName("");
      });
    }

    if (!updates.isEmpty()) {
      promotionLineDao.batchUpdate(tenant, uuid, updates);
    }
    if (!inserts.isEmpty()) {
      promotionLineDao.batchInsert(tenant, uuid, inserts);
    }
  }

  @Override
  public void addLines(String tenant, String uuid, Collection<String> skuIds, PricePromotionLineType type, String rule,
                       OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(type, "type");
    Assert.hasText(rule, "rule");
    if (CollectionUtils.isEmpty(skuIds)) {
      return;
    }
    addLineCheckPromotion(tenant, uuid);
    PricePromotion promotion = this.get(tenant, uuid);
    // 比较
    List<PricePromotionLine> updates = new ArrayList<>();
    List<PricePromotionLine> inserts = new ArrayList<>();
    addSkulimt(tenant, promotion.getOrgId(), uuid, skuIds, type, rule, updates, inserts);
    if (!updates.isEmpty()) {
      promotionLineDao.batchUpdate(tenant, uuid, updates);
    }
    if (!inserts.isEmpty()) {
      promotionLineDao.batchInsert(tenant, uuid, inserts);
    }
  }

  @Override
  public void addLines(String tenant, String uuid, List<PricePromotionGroupRule> rules, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    if (CollectionUtils.isEmpty(rules)) {
      return;
    }
    for (PricePromotionGroupRule rule : rules) {
      Assert.notNull(rule.getSkuGroup(), "group");
      Assert.notNull(rule.getSkuGroupName(), "groupName");
      Assert.notNull(rule.getType(), "type");
      Assert.notNull(rule.getRule(), "rule");
    }
    addLineCheckPromotion(tenant, uuid);
    // 比较
    List<PricePromotionLine> updates = new ArrayList<>();
    List<PricePromotionLine> inserts = new ArrayList<>();
    for (PricePromotionGroupRule rule : rules) {
      addFullDiscount(tenant, uuid, rule.getType(), rule.getRule(), rule.getSkuGroup(), rule.getSkuGroupName(), updates,
          inserts);
    }
    if (!updates.isEmpty()) {
      promotionLineDao.batchUpdate(tenant, uuid, updates);
    }
    if (!inserts.isEmpty()) {
      promotionLineDao.batchInsert(tenant, uuid, inserts);
    }
  }

  private void addLineCheckPromotion(String tenant, String uuid) throws BaasException {
    PricePromotion promotion = this.get(tenant, uuid);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    if (promotion.getType() == null) {
      throw new BaasException("请先选择单据促销类型");
    }
    checkEdit(promotion);
  }

  private void addFullDiscount(String tenant, String uuid, PricePromotionLineType type, String rule, String skuGroup,
                               String skuGroupName, List<PricePromotionLine> updates, List<PricePromotionLine> inserts) throws BaasException {
    PricePromotionLine line = promotionLineDao.getBySkuGroup(tenant, uuid, skuGroup);
    if (line != null) {
      updates.add(line);
    } else {
      line = new PricePromotionLine();
      inserts.add(line);
    }
    PriceSku sku = new PriceSku();
    sku.setId("*");
    sku.setCode("*");
    sku.setQpc(BigDecimal.ONE);
    sku.setName("");
    line.setSku(sku);
    line.setRule(rule);
    line.setType(type);
    line.setOwner(uuid);
    line.setTenant(tenant);
    line.setSkuGroupName(skuGroupName);
    line.setSkuGroup(skuGroup);
  }

  private void addSkulimt(String tenant, String orgId, String uuid, Collection<String> skuIds,
                          PricePromotionLineType type,
                          String rule, List<PricePromotionLine> updates, List<PricePromotionLine> inserts) throws BaasException {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceSku.Queries.ID, Cop.IN, skuIds.toArray());
    qd.addByField(PriceSku.Queries.ORG_ID, Cop.EQUALS, orgId);
    QueryResult<PriceSku> skuQueryResult = priceSkuConfigDao.query(tenant, qd);
    Map<String, PriceSku> skuMap = skuQueryResult.getRecords()
        .stream()
        .collect(Collectors.toMap(s -> s.getId(), s -> s));
    List<PricePromotionLine> lines = promotionLineDao.listBySkuIds(tenant, uuid, skuMap.keySet());
    Map<String, PricePromotionLine> existMap = lines.stream()
        .collect(Collectors.toMap(l -> l.getSku().getId(), l -> l));

    for (String sku : skuIds) {
      if (!skuMap.containsKey(sku)) {
        throw new BaasException("添加的商品不存在，sku={0}", sku);
      }
      PricePromotionLine line = existMap.get(sku);
      if (line == null) {
        line = new PricePromotionLine();
        inserts.add(line);
      } else {
        updates.add(line);
      }
      line.setSku(skuMap.get(sku));
      line.setRule(rule);
      line.setType(type);
      line.setOwner(uuid);
      line.setTenant(tenant);
    }
  }

  @Override
  public void batchSaveLine(String tenant, String uuid, Collection<PricePromotionLine> lines, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    PricePromotion promotion = this.get(tenant, uuid);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(promotion);

    List<String> skuIds = lines.stream().map(l -> l.getSku().getId()).collect(Collectors.toList());
    List<PricePromotionLine> exists = promotionLineDao.listBySkuIds(tenant, uuid, skuIds);
    Map<String, PricePromotionLine> existMap = exists.stream()
        .collect(Collectors.toMap(l -> l.getSku().getId(), l -> l));

    // 比较
    List<PricePromotionLine> updates = new ArrayList<>();
    List<PricePromotionLine> inserts = new ArrayList<>();
    lines.stream().forEach(l -> {
      PricePromotionLine line = existMap.get(l.getSku().getId());
      if (line == null) {
        inserts.add(l);
      } else {
        line.setSku(l.getSku());
        line.setType(l.getType());
        line.setRule(l.getRule());
        updates.add(line);
      }
    });

    if (!updates.isEmpty()) {
      promotionLineDao.batchUpdate(tenant, uuid, updates);
    }
    if (!inserts.isEmpty()) {
      promotionLineDao.batchInsert(tenant, uuid, inserts);
    }
  }

  @Override
  public void deleteLines(String tenant, String uuid, Collection<String> lineIds, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    if (CollectionUtils.isEmpty(lineIds)) {
      return;
    }
    PricePromotion promotion = this.get(tenant, uuid);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(promotion);
    promotionLineDao.batchDelete(tenant, uuid, lineIds);
  }

  @Override
  public void editLine(String tenant, String uuid, String lineId, PricePromotionLineType type, String rule,
                       OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.hasText(lineId, "lineId");
    PricePromotion promotion = this.get(tenant, uuid);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(promotion);
    PricePromotionLine promotionLine = promotionLineDao.get(tenant, uuid, lineId);
    if (promotionLine == null) {
      throw new BaasException("待修改的信息不存在");
    }
    promotionLine.setType(type);
    promotionLine.setRule(rule);
    promotionLineDao.update(tenant, uuid, promotionLine);
  }

  @Override
  @Tx
  public void audit(String tenant, String orgId, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    PricePromotion promotion = this.get(tenant, uuid, PricePromotion.FETCH_ALL);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    if (promotion.getState() != PricePromotionState.CONFIRMED) {
      throw new BaasException("当前状态不可审核");
    }
    if (promotionLineDao.queryCount(tenant, uuid, new QueryDefinition()) <= 0) {
      throw new BaasException("至少添加一行");
    }
    Date twoDate = DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE);
    if (promotion.getEffectiveStartDate().before(twoDate)) {
      throw new BaasException("生效时间最早是二天后");
    }
    if (promotion.getEffectiveStartDate().equals(twoDate)) {
      Date today = new Date();
      PromotionActivityConfig config = configClient.getConfig(tenant, PromotionActivityConfig.class, orgId);
      Date latestAuditTime = decodeTime(today, config.getLatestAuditTime());
      if (today.after(latestAuditTime)) {
        throw new BaasException("无法在<{0}>之后审核日期为<{1}>促销单", config.getLatestAuditTime(),
            DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(promotion.getEffectiveStartDate()));
      }
    }
    // 生效开始时间 <= 结束时间
    if (promotion.getEffectiveEndDate().before(promotion.getEffectiveStartDate())) {
      throw new BaasException("生效结束时间必须晚于开始时间");
    }
    if (promotion.isAllShops()) {
      StoreFilter filter = new StoreFilter();
      filter.setOrgIdEq(orgId);
      QueryResult<Store> storeResult = storeService.query(tenant, filter);
      List<PricePromotionShop> promotionShops = storeResult.getRecords().stream().map(store -> {
        PricePromotionShop shop = new PricePromotionShop();
        shop.setTenant(tenant);
        shop.setOwner(uuid);
        shop.setShop(store.getId());
        shop.setShopCode(store.getCode());
        shop.setShopName(store.getName());
        return shop;
      }).collect(Collectors.toList());
      promotionShopDao.deleteByOwner(tenant, uuid);
      promotionShopDao.batchInsert(tenant, uuid, promotionShops);
    } else if (promotionShopDao.queryCount(tenant, uuid, new QueryDefinition()) <= 0) {
      throw new BaasException("至少关联一个门店");
    }
    splitPricePromotion(tenant, orgId, uuid, operateInfo);
    // 新增时间的校验
    Date endDate = DateUtils.addDays(new Date(), PriceAdjustment.MIN_EFFECTIVE_DAYS);
    endDate = DateUtils.truncate(endDate, Calendar.DATE);
    // 最后的时间是：明天的00：00：00
    if (promotion.getEffectiveStartDate().getTime() == endDate.getTime()) {
      // 实时推送
      publishShopPriceEvCall(tenant, promotion.getOrgId(), promotion.getEffectiveStartDate());
    }
  }


  private void splitPricePromotion(String tenant, String orgId, String uuid, OperateInfo operateInfo) throws BaasException {
    PricePromotion promotion = this.get(tenant, uuid, PricePromotion.FETCH_ALL);

    List<PricePromotion> newPromotionList = new ArrayList<>();

    List<PricePromotionLine> lines = promotion.getLines();
    List<PricePromotionShop> shops = promotion.getShops();
    if (promotion.isAllShops()) {
      StoreFilter filter = new StoreFilter();
      filter.setPage(0);
      filter.setPageSize(Integer.MAX_VALUE);
      filter.setOrgIdEq(orgId);
      QueryResult<Store> query = storeService.query(tenant, filter);
      List<PricePromotionShop> all = new ArrayList<>();
      query.getRecords().stream().forEach(s -> {
        PricePromotionShop shop = new PricePromotionShop();
        shop.setShop(s.getId());
        shop.setOwner(uuid);
        shop.setShopCode(s.getCode());
        shop.setShopName(s.getName());
        shop.setTenant(tenant);
        all.add(shop);
      });
    }
    for (PricePromotionShop shop : shops) {
      PricePromotion newPricePromotion = new PricePromotion();
      BeanUtils.copyProperties(promotion, newPricePromotion);
      newPricePromotion.setUuid(UUID.randomUUID().toString());
      newPricePromotion.setFlowNo(billNumberMgr.generatePricePromotionFlowNo(tenant));
      newPricePromotion.setState(PricePromotionState.AUDITED);
      newPricePromotion.setCreateInfo(promotion.getCreateInfo());
      newPricePromotion.setLastModifyInfo(operateInfo);
      newPricePromotion.setAllShops(false);

      PricePromotionShop newShop = new PricePromotionShop();
      BeanUtils.copyProperties(shop, newShop);
      newShop.setOwner(newPricePromotion.getUuid());
      newShop.setUuid(UUID.randomUUID().toString());
      newPricePromotion.setShops(Lists.newArrayList(newShop));

      List<PricePromotionLine> newLines = new ArrayList<>();
      for (PricePromotionLine line : lines) {
        PricePromotionLine l = new PricePromotionLine();
        BeanUtils.copyProperties(line, l);
        l.setOwner(newPricePromotion.getUuid());
        l.setUuid(UUID.randomUUID().toString());
        newLines.add(l);
      }
      newPricePromotion.setLines(newLines);
      newPricePromotion.setOrgId(promotion.getOrgId());
      newPromotionList.add(newPricePromotion);
    }

    // 存在督导承担的部分才会关联补贴计划
    BigDecimal supervisorSharingRate = promotion.getSupervisorSharingRate();
    if (supervisorSharingRate != null && supervisorSharingRate.compareTo(BigDecimal.ZERO) > 0) {
      if (isNotAllScope(tenant, promotion.getOrgId())) {
        subsidyPlanService.checkShopExistPlan(tenant,
            shops.stream().map(PricePromotionShop::getShop).collect(Collectors.toList()),
            promotion.getEffectiveStartDate(), promotion.getEffectiveEndDate());
      } else {
        subsidyPlanService.checkShopExistPlan(tenant, promotion.getOrgId(),
            shops.stream().map(PricePromotionShop::getShop).collect(Collectors.toList()),
            promotion.getEffectiveStartDate(), promotion.getEffectiveEndDate());
      }

      List<String> uuids = newPromotionList.stream().map(PricePromotion::getUuid).collect(Collectors.toList());
      subsidyPlanService.relation(tenant, ActivityType.PRICE_PROMOTION, uuids);
    }

    promotionDao.batchInsert(tenant, newPromotionList);

    List<PricePromotionShop> newShopList = newPromotionList.stream()
        .flatMap(s -> s.getShops().stream())
        .collect(Collectors.toList());
    promotionShopDao.batchInsert(tenant, newShopList);

    List<PricePromotionLine> newLineList = newPromotionList.stream()
        .flatMap(s -> s.getLines().stream())
        .collect(Collectors.toList());
    promotionLineDao.batchInsert(tenant, newLineList);

    cancel(tenant, promotion.getUuid(), "按照门店拆分促销单作废原单", operateInfo);

    for (PricePromotion pricePromotion : newPromotionList) {
      PricePromotionAuditedMsg msg = new PricePromotionAuditedMsg();
      msg.setUuid(pricePromotion.getUuid());
      msg.setTenant(tenant);
      publisher.publishForNormal(PricePromotionAuditedEvCallExecutor.PRICE_PROMOTION_AUDITED_EXECUTOR_ID, msg);
    }
  }

  private boolean isAllScope(String tenant, String orgId) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    return false;
  }

  private boolean isNotAllScope(String tenant, String orgId) {
    return !isAllScope(tenant, orgId);
  }

  private QueryResult<ConflictPromotionLine> checkReplace(String tenant, PricePromotion promotion, int page, int size)
      throws BaasException {
    List<String> shops;
    if (promotion.isAllShops()) {
      StoreFilter filter = new StoreFilter();
      filter.setOrgIdEq(promotion.getOrgId());
      QueryResult<Store> storeResult = storeService.query(tenant, filter);
      shops = storeResult.getRecords().stream().map(Store::getId).collect(Collectors.toList());
    } else {
      shops = promotion.getShops().stream().map(PricePromotionShop::getShop).collect(Collectors.toList());
    }
    PricePromotionType type = promotion.getType();
    List<String> lineSkuIds = null;
    List<String> lineGroups = null;
    if (PricePromotionType.FULL_DISCOUNT_PRMT == type) {
      lineGroups = promotion.getLines().stream().map(PricePromotionLine::getSkuGroup).collect(Collectors.toList());
      List<SkuGroupCategory> skuGroupCategories = new ArrayList<>();
      for (String lineGroup : lineGroups) {
        List<SkuGroupCategory> categories = skuGroupService.listByGroupId(tenant, lineGroup);
        if (CollectionUtils.isNotEmpty(categories)) {
          skuGroupCategories.addAll(categories);
        }
      }
      List<String> categoryIds = skuGroupCategories.stream()
          .map(s -> s.getCategory().getId())
          .collect(Collectors.toList());
      SkuFilter skuFilter = new SkuFilter();
      skuFilter.setCategoryIdIn(categoryIds);
      skuFilter.setDeletedEq(false);
      skuFilter.setPageSize(10000);
      QueryResult<PriceSkuConfig> skuQueryResult = skuConfigService.query(tenant, promotion.getOrgId(), skuFilter);
      if (CollectionUtils.isNotEmpty(skuQueryResult.getRecords())) {
        lineSkuIds = skuQueryResult.getRecords().stream().map(s -> s.getSku().getId()).collect(Collectors.toList());
      }
    } else {
      lineSkuIds = promotion.getLines().stream().map(s -> s.getSku().getId()).collect(Collectors.toList());
      SkuFilter skuFilter = new SkuFilter();
      skuFilter.setIdIn(lineSkuIds);
      skuFilter.setDeletedEq(false);
      skuFilter.setPageSize(10000);
      QueryResult<PriceSkuConfig> skuQueryResult = skuConfigService.query(tenant, promotion.getOrgId(), skuFilter);
      if (CollectionUtils.isNotEmpty(skuQueryResult.getRecords())) {
        lineGroups = skuQueryResult.getRecords().stream().map(PriceSkuConfig::getSkuGroup).collect(Collectors.toList());
      }
    }
    return promotionLineDao.listConflict(tenant, promotion.getOrgId(), promotion.getUuid(), shops, lineSkuIds, lineGroups,
        promotion.getEffectiveStartDate(), promotion.getEffectiveEndDate(), page * size, size);
  }

  @Override
  public void cancel(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    PricePromotion promotion = this.get(tenant, uuid);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    if (promotion.getState() != PricePromotionState.CONFIRMED && promotion.getState() != PricePromotionState.AUDITED) {
      throw new BaasException("当前状态不可作废");
    }
    promotionDao.canceled(tenant, uuid, reason, operateInfo);
  }

  @Override
  public void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    PricePromotion promotion = this.get(tenant, uuid);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    if (promotion.getState() != PricePromotionState.AUDITED) {
      throw new BaasException("当前状态不可发布");
    }
    promotionDao.changeState(tenant, uuid, PricePromotionState.PUBLISHED, operateInfo);
  }

  @Override
  public void expire(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    PricePromotion promotion = this.get(tenant, uuid);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    if (promotion.getState() == PricePromotionState.EXPIRED) {
      log.info("当前已过期，忽略");
      return;
    }
    if (promotion.getState() != PricePromotionState.PUBLISHED) {
      throw new BaasException("当前状态不可过期");
    }
    promotionDao.changeState(tenant, uuid, PricePromotionState.EXPIRED, operateInfo);
  }

  @Override
  @Tx
  public void terminate(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(reason, "reason");
    PricePromotion promotion = this.get(tenant, uuid, PricePromotion.FETCH_SHOP);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    if (promotion.getState() == PricePromotionState.TERMINATE) {
      log.info("当前已终止，忽略");
      return;
    }
    if (CollectionUtils.isEmpty(promotion.getShops()) || promotion.getShops().size() > 1) {
      log.error("历史数据促销单<{}>,无法终止", promotion.getUuid());
      return;
    }
    promotionDao.changeState(tenant, uuid, PricePromotionState.TERMINATE, reason, operateInfo);
    shopPricePromotionService.delete(tenant, uuid);
    shopPricePromotionManagerService.deleteBySource(tenant, uuid);
    PricePromotionTerminateMsg msg = new PricePromotionTerminateMsg();
    msg.setUuid(uuid);
    msg.setTenant(tenant);
    publisher.publishForNormal(PricePromotionTerminateEvCallExecutor.PRICE_PROMOTION_TERMINATE_EXECUTOR_ID, msg);
  }

  @Override
  public void relateShops(String tenant, String orgId, String uuid, List<String> shopIds) throws BaasException {
    Assert.notNull(tenant);
    Assert.notNull(uuid);
    Assert.notNull(shopIds);
    if (CollectionUtils.isEmpty(shopIds)) {
      // 传空，清理门店
      promotionShopDao.deleteByOwner(tenant, uuid);
      return;
    }

    PricePromotion promotion = this.get(tenant, uuid);
    if (promotion == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(promotion);
    StoreFilter filter = new StoreFilter();
    filter.setIdIn(shopIds);
    filter.setOrgIdEq(orgId);
    QueryResult<Store> storeResult = storeService.query(tenant, filter);
    Map<String, Store> storeMap = storeResult.getRecords().stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
    List<PricePromotionShop> promotionShops = new ArrayList<>();
    for (String shopId : shopIds) {
      PricePromotionShop promotionShop = new PricePromotionShop();
      promotionShop.setTenant(tenant);
      promotionShop.setOwner(uuid);
      Store store = storeMap.get(shopId);
      if (store == null) {
        throw new BaasException("门店<{0}>不存在", shopId);
      }
      promotionShop.setShop(shopId);
      promotionShop.setShopCode(store.getCode());
      promotionShop.setShopName(store.getName());
      promotionShops.add(promotionShop);
    }
    promotionShopDao.deleteByOwner(tenant, uuid);
    promotionShopDao.batchInsert(tenant, uuid, promotionShops);
  }

  @Override
  public List<PricePromotionShop> listShops(String tenant, String uuid) {
    Assert.notNull(tenant);
    Assert.notNull(uuid);
    return promotionShopDao.list(tenant, uuid);
  }

  @Override
  public QueryResult<ConflictPromotionLine> conflictRemind(String tenant, PricePromotion promotion, int page, int size)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(promotion.getUuid(), "uuid");
    if (CollectionUtils.isEmpty(promotion.getLines())) {
      throw new BaasException("至少添加一行");
    }
    if (!promotion.isAllShops() && CollectionUtils.isEmpty(promotion.getShops())) {
      throw new BaasException("至少添加一个门店");
    }
    if (promotion.getEffectiveStartDate().before(DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE))) {
      throw new BaasException("生效时间最早是二天后");
    }
    // 生效开始时间 <= 结束时间
    if (promotion.getEffectiveEndDate().before(promotion.getEffectiveStartDate())) {
      throw new BaasException("生效结束时间必须晚于开始时间");
    }
    return checkReplace(tenant, promotion, page, size);
  }

  private void checkEdit(PricePromotion promotion) throws BaasException {
    if (promotion.getState() == PricePromotionState.INIT || promotion.getState() == PricePromotionState.CONFIRMED) {
      return;
    }
    throw new BaasException("当前状态不可编辑");
  }

  /**
   * 门店商品价格计算
   *
   * @param tenant
   *          租户
   */
  private void publishShopPriceEvCall(String tenant, String orgId, Date executeDate) {
    ShopPriceTaskMsg msg = new ShopPriceTaskMsg();
    msg.setTenant(tenant);
    msg.setExecuteDate(executeDate);
    msg.setOrgId(orgId);
    publisher.publishForNormal(ShopPriceEvCallExecutor.SHOP_PRICE_CREATE_EXECUTOR_ID, msg);
  }

  private Date decodeTime(Date date, String time) {
    if (time == null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      return calendar.getTime();
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    String[] split = time.split(":");

    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
    calendar.set(Calendar.MINUTE, Integer.parseInt(split[1]));
    if (split.length > 2) {
      calendar.set(Calendar.SECOND, Integer.parseInt(split[2]));
    } else {
      calendar.set(Calendar.SECOND, 0);
    }
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }
}
