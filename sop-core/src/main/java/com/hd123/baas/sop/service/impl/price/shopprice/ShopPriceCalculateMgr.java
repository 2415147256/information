package com.hd123.baas.sop.service.impl.price.shopprice;

import com.google.common.collect.Lists;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.config.BaasPriceSkuConfig;
import com.hd123.baas.sop.service.api.entity.PriceGrade;
import com.hd123.baas.sop.service.api.grade.PriceGradeService;
import com.hd123.baas.sop.service.api.group.SkuGroupCategory;
import com.hd123.baas.sop.service.api.group.SkuGroupService;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfig;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfigService;
import com.hd123.baas.sop.service.api.price.gradeadjustment.*;
import com.hd123.baas.sop.service.api.price.priceadjustment.*;
import com.hd123.baas.sop.service.api.price.pricepromotion.*;
import com.hd123.baas.sop.service.api.price.shopprice.*;
import com.hd123.baas.sop.service.api.price.shopprice.bean.*;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.*;
import com.hd123.baas.sop.service.dao.price.tempshopadjustment.TempShopPriceAdjustmentDaoBof;
import com.hd123.baas.sop.service.impl.price.priceadjustment.calculate.PriceCalculateMgr;
import com.hd123.baas.sop.service.impl.price.spel.SpelMgr;
import com.hd123.baas.sop.service.impl.price.spel.param.PriceFormulaParam;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceCalculateEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceCalculateMsg;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.pms.util.ElapsedTimer;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.*;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/19.
 */
@Component
@Slf4j
public class ShopPriceCalculateMgr {

  @Autowired
  private PriceGradeAdjustmentService priceGradeAdjustmentService;
  @Autowired
  private PriceAdjustmentService priceAdjustmentService;
  @Autowired
  private PricePromotionService pricePromotionService;
  @Autowired
  private StoreService storeService;
  @Autowired
  private ShopPricePromotionManagerService shopPricePromotionManagerService;
  @Autowired
  private ShopPriceGradeManagerService shopPriceGradeManagerService;
  @Autowired
  private PriceGradeService priceGradeService;
  @Autowired
  private ShopPriceGradeService shopPriceGradeService;
  @Autowired
  private ShopPricePromotionService shopPricePromotionService;
  @Autowired
  private ShopPriceManagerService shopPriceManagerService;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private SpelMgr spelMgr;
  @Autowired
  private PriceCalculateMgr priceCalculateMgr;
  @Autowired
  private ShopPriceJobService priceJobService;
  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private SkuGroupService skuGroupService;
  @Autowired
  private PriceSkuConfigService priceSkuConfigService;
  @Autowired
  private TempShopPriceManagerService tempShopPriceManagerService;
  @Autowired
  private TempShopPriceAdjustmentService tempShopPriceAdjustmentService;
  @Autowired
  private TempShopPriceAdjustmentDaoBof tempShopPriceAdjustmentDao;

  /**
   * ??????????????????????????????????????????????????????????????????
   * <p>
   * ????????????????????????????????????????????????????????????????????????????????????????????????????????????ShopPriceGradeManager
   */
  @Tx
  public void calculatePriceGradeManager(String tenant, String orgId, Date executeDate, OperateInfo operateInfo)
      throws BaasException {
    executeDate = DateUtils.truncate(executeDate, Calendar.DATE);
    Date executeDateAddOne = DateUtils.addDays(executeDate, 1); // ??????????????????
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceGradeAdjustment.Queries.ORG_ID, Cop.EQUALS, orgId);
    qd.addByField(PriceGradeAdjustment.Queries.STATE, Cop.EQUALS, PriceGradeAdjustmentState.AUDITED.name());
    qd.addByField(PriceGradeAdjustment.Queries.EFFECTIVE_START_DATE, Cop.LESS, executeDateAddOne);
    qd.addOrder(PriceGradeAdjustment.Queries.EFFECTIVE_START_DATE, QueryOrderDirection.desc);
    qd.addOrder(PriceGradeAdjustment.Queries.FLOW_NO, QueryOrderDirection.desc);
    QueryResult<PriceGradeAdjustment> gradeAdjustmentQueryResult = priceGradeAdjustmentService.query(tenant, qd);

    for (PriceGradeAdjustment item : gradeAdjustmentQueryResult.getRecords()) {
      log.info("???????????????????????????????????????flowno={}", item.getFlowNo());
      List<PriceGradeAdjustmentShop> shops = priceGradeAdjustmentService.listShops(tenant, item.getUuid());
      QueryDefinition lineQd = new QueryDefinition();
      int page = 0;
      lineQd.setPageSize(1000);
      while (true) {
        lineQd.setPage(page++);
        QueryResult<PriceGradeAdjustmentLine> lineQueryResult = priceGradeAdjustmentService.queryLine(tenant,
            item.getUuid(), lineQd);
        if (lineQueryResult.getRecords().isEmpty()) {
          break;
        }
        List<ShopPriceGradeManager> shopPriceGradeManagers = new ArrayList<>();
        for (PriceGradeAdjustmentLine line : lineQueryResult.getRecords()) {
          for (PriceGradeAdjustmentShop shop : shops) {
            ShopPriceGradeManager shopPriceGradeManager = new ShopPriceGradeManager();
            shopPriceGradeManager.setOrgId(orgId);
            shopPriceGradeManager.setShop(shop.getShop());
            shopPriceGradeManager.setSkuGroup(line.getSkuGroup());
            shopPriceGradeManager.setSkuPosition(line.getSkuPosition());
            shopPriceGradeManager.setPriceGrade(line.getPriceGrade());
            shopPriceGradeManager.setSource(item.getUuid()); // ????????????
            shopPriceGradeManager.setSourceCreateTime(item.getCreateInfo().getTime());
            shopPriceGradeManager.setEffectiveStartDate(item.getEffectiveStartDate());
            shopPriceGradeManagers.add(shopPriceGradeManager);
          }
        }
        shopPriceGradeManagerService.batchInsert(tenant, shopPriceGradeManagers);
      }
      priceGradeAdjustmentService.publish(tenant, item.getUuid(), operateInfo);
    }
  }

  /**
   * ????????????????????????????????????
   * <p>
   * ???ShopPriceGradeManager?????????????????????????????????????????????????????????????????????????????????+??????+???????????????????????????ShopPriceGrade??????
   * <p>
   * ????????????ShopPriceGradeManager?????????????????????????????????????????????
   *
   * @param tenant
   *     ??????
   */
  // @Tx ??????????????????
  public void calculatePriceGrade(String tenant, String orgId) throws BaasException {
    Date today = DateUtils.truncate(new Date(), Calendar.DATE);
    Date tomorrow = DateUtils.addDays(today, 1);

    QueryDefinition managerQd = new QueryDefinition();
    managerQd.addByField(ShopPriceGradeManager.Queries.ORG_ID, Cop.EQUALS, orgId);
    managerQd.addByField(ShopPriceGradeManager.Queries.EFFECTIVE_START_DATE, Cop.LESS, tomorrow);
    managerQd.addOrder(ShopPriceGradeManager.Queries.EFFECTIVE_START_DATE, QueryOrderDirection.asc);
    managerQd.addOrder(ShopPriceGradeManager.Queries.SOURCE_CREATE_TIME, QueryOrderDirection.asc);
    managerQd.setPageSize(200);
    managerQd.setPage(0);
    while (true) {
      QueryResult<ShopPriceGradeManager> managerQueryResult = shopPriceGradeManagerService.query(tenant, managerQd);
      if (managerQueryResult.getRecords().isEmpty()) {
        // ????????????????????????????????????????????????
        break;
      }
      List<String> deletes = new ArrayList<>();
      // ?????????????????????????????????????????????????????? + ??????????????? + ???????????? ????????????????????????
      // ??????????????????????????????
      // List<ShopPriceGrade> grades = new ArrayList<>();
      HashMap<String, ShopPriceGrade> map = new HashMap<>();
      for (ShopPriceGradeManager manager : managerQueryResult.getRecords()) {
        // KEY = ?????? + ??????????????? + ????????????
        String key = manager.getShop() + manager.getSkuGroup() + manager.getSkuPosition();
        ShopPriceGrade grade = null;
        if (map.containsKey(key)) {
          grade = map.get(key);
          // ??????
          grade.setPriceGrade(manager.getPriceGrade());
          grade.setSourceCreateTime(manager.getSourceCreateTime());
          grade.setSource(manager.getSource());
        } else {
          grade = new ShopPriceGrade();
          grade.setOrgId(orgId);
          grade.setShop(manager.getShop());
          grade.setSkuGroup(manager.getSkuGroup());
          grade.setSkuPosition(manager.getSkuPosition());
          grade.setPriceGrade(manager.getPriceGrade());
          grade.setSourceCreateTime(manager.getSourceCreateTime());
          grade.setSource(manager.getSource());
        }
        // ??????
        map.put(key, grade);
        deletes.add(manager.getUuid());
      }
      // todo ??????ORG_ID
      shopPriceGradeService.batchSave(tenant, map.values());
      // ??????????????????????????????????????????
      shopPriceGradeManagerService.batchDelete(tenant, deletes);
    }
  }

  /**
   * ?????????????????????????????????????????????????????????????????????????????????
   * <p>
   * ????????????????????????????????????????????????????????????????????????????????????????????????????????????ShopPromotionPriceManager
   *
   * @param tenant
   *     ??????
   * @param executeDate
   *     ????????????
   */
  @Tx
  public void calculatePricePromotionManager(String tenant, String orgId, Date executeDate, OperateInfo operateInfo)
      throws BaasException {
    // ?????????????????????
    QueryResult<PricePromotion> promotionQueryResult = queryPricePromotion(tenant, orgId, executeDate);
    //
    // ??????
    for (PricePromotion promotion : promotionQueryResult.getRecords()) {
      // ??????????????????
      List<PricePromotionShop> shops = pricePromotionService.listShops(tenant, promotion.getUuid());
      QueryDefinition lineQd = new QueryDefinition();
      int page = 0;
      lineQd.setPageSize(200);
      while (true) {
        lineQd.setPage(page++);
        List<ShopPricePromotionManager> promotionPriceManagers = buildPricePromotionManager(tenant, orgId, promotion, shops, lineQd);
        if (CollectionUtils.isEmpty(promotionPriceManagers)) {
          break;
        }
        shopPricePromotionManagerService.batchInsert(tenant, promotionPriceManagers);
      }
      pricePromotionService.publish(tenant, promotion.getUuid(), operateInfo);
    }
  }

  /**
   * ????????????????????????????????????
   * <p>
   * 1. ????????????????????????????????????????????????<br/> 2. ???ShopPromotionPriceManager?????????????????????????????????????????????????????????+???????????????????????????ShopPromotionPrice???<br/>
   * 3. ???ShopPromotionPriceManager???????????????????????????????????????????????????
   *
   * @param tenant
   *     ??????
   */
  // @Tx ??????????????????
  public void calculatePricePromotion(String tenant, String orgId, Date executeDate) throws BaasException {
    Date today = DateUtils.truncate(new Date(), Calendar.DATE);
    shopPricePromotionManagerService.deleteBeforeDate(tenant, orgId, today);
  }

  /**
   * ??????????????????????????????????????????????????????
   * <p>
   * ??????ShopPriceGradeManager???ShopPriceGrade???ShopPromotionPriceManager???ShopPromotionPrice??????????????????
   *
   * @param tenant
   *     ??????
   * @param executeDate
   *     ????????????
   */
  @Tx
  public String calculatePriceAdjustment(String tenant, String orgId, Date executeDate, String taskId, OperateInfo operateInfo)
      throws BaasException {
    PriceAdjustment priceAdjustment = priceAdjustment(tenant, orgId, executeDate);
    if (priceAdjustment == null) {
      log.info("?????????????????????????????????????????????");
      return null;
    }
    H6Task h6Task = h6TaskService.get(tenant, taskId);
    if (h6Task == null) {
      log.info("H6Task??????????????????{}", taskId);
      return null;
    }
    ElapsedTimer timer = ElapsedTimer.getThreadInstance();
    log.info("??????????????????????????????={},uuid={}", priceAdjustment.getFlowNo(), priceAdjustment.getUuid());
    List<Store> list = queryStores(tenant, orgId, timer);
    for (Store store : list) {
      timer.start("\t??????????????????");
      ShopPriceJob priceJob = new ShopPriceJob();
      priceJob.setOrgId(orgId);
      priceJob.setShop(store.getId());
      priceJob.setShopCode(store.getCode());
      priceJob.setShopName(store.getName());
      priceJob.setTaskId(taskId);
      priceJob.setExecuteDate(executeDate);
      priceJob.setPriceAdjustment(priceAdjustment.getUuid());
      priceJobService.saveNew(tenant, priceJob, operateInfo);

      ShopPriceCalculateMsg msg = new ShopPriceCalculateMsg();
      msg.setTenant(tenant);
      msg.setShop(store.getId());
      msg.setShopCount(list.size());
      msg.setTaskId(taskId);
      msg.setExecuteDate(executeDate);
      msg.setPk(priceAdjustment.getUuid());
      publisher.publishForNormal(ShopPriceCalculateEvCallExecutor.SHOP_PRICE_CALCULATE_CREATE_EXECUTOR_ID, msg);
      timer.stop("\t??????????????????");
    }
    if (priceAdjustment.getState() != PriceAdjustmentState.PUBLISHED) {
      priceAdjustmentService.publish(tenant, priceAdjustment.getUuid(), operateInfo);
    }
    return priceAdjustment.getUuid();
  }

  @Tx
  public String calculateTempShopPriceAdjustment(String tenant, Date executeDate, String taskId,
      String tempShopPriceAdjustmentId, OperateInfo operateInfo) throws BaasException {
    TempShopPriceAdjustment tempShopPriceAdjustment = tempShopPriceAdjustmentService.get(tenant,
        tempShopPriceAdjustmentId);
    if (tempShopPriceAdjustment == null) {
      log.info("???????????????????????????????????????????????????");
      return null;
    }
    H6Task h6Task = h6TaskService.get(tenant, taskId);
    if (h6Task == null) {
      log.info("H6Task??????????????????{}", taskId);
      return null;
    }
    if (h6Task.getType() != H6TaskType.TEMP_SHOP) {
      log.info("H6Task?????????{}??????????????????TEMP_SHOP", taskId);
      return null;
    }
    log.info("???????????????????????????????????????={},uuid={}", tempShopPriceAdjustment.getFlowNo(), tempShopPriceAdjustment.getUuid());
    List<String> shopPriceStoreIds = new ArrayList<>();
    StoreFilter storeFilter = new StoreFilter();
    storeFilter.setOrgIdEq(tempShopPriceAdjustment.getOrgId());
    QueryResult<Store> shopResult = storeService.query(tenant, storeFilter);
    for (Store store : shopResult.getRecords()) {
      if (!checkShopGrade(tenant, tempShopPriceAdjustment.getOrgId(), store)) {
        continue;
      }
      shopPriceStoreIds.add(store.getId());

      ShopPriceJob priceJob = new ShopPriceJob();
      priceJob.setShop(store.getId());
      priceJob.setShopCode(store.getCode());
      priceJob.setShopName(store.getName());
      priceJob.setTaskId(taskId);
      priceJob.setExecuteDate(executeDate);
      priceJob.setPriceAdjustment(tempShopPriceAdjustment.getUuid());
      priceJobService.saveNew(tenant, priceJob, operateInfo);
    }
    // ??????????????????
    if (CollectionUtils.isNotEmpty(shopPriceStoreIds)) {
      shopPriceStoreIds.forEach(i -> {
        ShopPriceCalculateMsg msg = new ShopPriceCalculateMsg();
        msg.setTenant(tenant);
        msg.setShop(i);
        msg.setTaskId(taskId);
        msg.setShopCount(shopPriceStoreIds.size());
        msg.setExecuteDate(executeDate);
        msg.setPk(tempShopPriceAdjustment.getUuid());
        publisher.publishForNormal(ShopPriceCalculateEvCallExecutor.SHOP_PRICE_CALCULATE_CREATE_EXECUTOR_ID, msg);
      });
    }

    if (tempShopPriceAdjustment.getState() != TempShopPriceAdjustmentState.EFFECTED) {
      tempShopPriceAdjustmentService.publish(tenant, tempShopPriceAdjustment.getUuid(), operateInfo);
    }
    return tempShopPriceAdjustment.getUuid();
  }

  /**
   * ???????????????????????????
   *
   * @param tenant
   *     ??????
   */
  @Tx
  public void clearExpiredShopPrice(String tenant, String orgId) {
    Date today = new Date();
    today = DateUtils.truncate(today, Calendar.DATE);
    shopPriceManagerService.clearBeforeDate(tenant, orgId, today);
  }

  /**
   * @param tenant
   *     ??????
   * @param executeDate
   *     ????????????
   * @param uuid
   *     ?????????????????????uuid
   * @param operateInfo
   *     ?????????
   * @throws BaasException
   *     ??????
   */
  @Tx
  public void expiredAdjustment(String tenant, String orgId, Date executeDate, String uuid, OperateInfo operateInfo)
      throws BaasException {
    if (StringUtils.isBlank(uuid)) {
      log.info("?????????????????????????????????????????????");
      return;
    }
    // ???????????????
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceAdjustment.Queries.ORG_ID, Cop.EQUALS, orgId);
    qd.addByField(PriceAdjustment.Queries.STATE, Cop.EQUALS, PriceAdjustmentState.PUBLISHED.name()); // ?????????
    qd.addByField(PriceAdjustment.Queries.EFFECTIVE_START_DATE, Cop.LESS + Cop.EQUALS, executeDate);
    qd.addByField(PriceAdjustment.Queries.UUID, Cop.NOT + Cop.EQUALS, uuid);
    // ???????????????????????????????????????executeDate+1?????????????????????
    QueryResult<PriceAdjustment> result = priceAdjustmentService.query(tenant, qd);
    if (CollectionUtils.isNotEmpty(result.getRecords())) {
      for (PriceAdjustment record : result.getRecords()) {
        priceAdjustmentService.expire(tenant, record.getUuid(), operateInfo);
      }
    }
  }

  @Tx
  public void expiredGradeAdjustment(String tenant, String orgId, OperateInfo operateInfo) throws BaasException {
    Date today = new Date();
    today = DateUtils.truncate(today, Calendar.DATE);

    // ???????????????????????????????????????1. ???????????????????????????????????????????????? 2. shopPriceGrade????????????????????????
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceGradeAdjustment.Queries.ORG_ID, Cop.EQUALS, orgId);
    qd.addByField(PriceGradeAdjustment.Queries.STATE, Cop.EQUALS, PriceGradeAdjustmentState.PUBLISHED.name()); // ?????????
    qd.addByField(PriceGradeAdjustment.Queries.EFFECTIVE_START_DATE, Cop.LESS, today); // ?????????????????????
    qd.addByOperation(PriceGradeAdjustment.Queries.NOT_INT_SHOP_PRICE_GRADE);
    QueryResult<PriceGradeAdjustment> result = priceGradeAdjustmentService.query(tenant, qd);
    for (int i = 0; i < result.getRecords().size(); i++) {
      priceGradeAdjustmentService.expire(tenant, result.getRecords().get(i).getUuid(), operateInfo);
    }
  }

  @Tx
  public void expiredPricePromotion(String tenant, String orgId, OperateInfo operateInfo) throws BaasException {
    Date today = new Date();
    today = DateUtils.truncate(today, Calendar.DATE);

    // ???????????????????????????????????????1. ???????????????????????????????????????????????? 2. shop_price_promotion????????????????????????
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PricePromotion.Queries.ORG_ID, Cop.EQUALS, orgId);
    qd.addByField(PricePromotion.Queries.STATE, Cop.EQUALS, PricePromotionState.PUBLISHED.name()); // ?????????
    qd.addByField(PricePromotion.Queries.EFFECTIVE_END_DATE, Cop.LESS, today); // ?????????????????????
    // qd.addByOperation(PricePromotion.Queries.NOT_INT_SHOP_PRICE_PROMOTION);
    QueryResult<PricePromotion> result = pricePromotionService.query(tenant, qd);
    for (int i = 0; i < result.getRecords().size(); i++) {
      pricePromotionService.expire(tenant, result.getRecords().get(i).getUuid(), operateInfo);
    }
  }

  @Tx
  public List<ShopPriceManager> calculateShopPriceAdjustment(String tenant, String shop, Date executeDate,
      PriceAdjustment adjustment, Boolean isSave, OperateInfo operateInfo) throws BaasException {
    ElapsedTimer elapsedTimer = ElapsedTimer.getThreadInstance();
    elapsedTimer.start("calculateShopPriceAdjustment");
    try {
      // ?????????????????????
      // if (shopPriceManagerService.isExecute(tenant, shop, executeDate)) {
      // log.info("???????????????????????????");
      // return;
      // }
      List<ShopPriceManager> priceManagers = new ArrayList<>();
      StoreFilter storeFilter = new StoreFilter();
      storeFilter.setIdIn(Collections.singletonList(shop));
      QueryResult<Store> storeQueryResult = storeService.query(tenant, storeFilter);
      if (storeQueryResult.getRecords().isEmpty()) {
        log.info("??????<{}>??????????????????", shop);
        return priceManagers;
      }
      Store store = storeQueryResult.getRecords().get(0);
      priceCalculateMgr.begin(tenant, adjustment.getOrgId(), operateInfo);
      QueryDefinition lineQd = new QueryDefinition();
      // ???????????????????????????????????????????????????????????????1000
      elapsedTimer.start("\tqueryLines");
      log.info("calculateShopPriceAdjustment???queryLines start?????????={}", shop);
      QueryResult<PriceAdjustmentLine> lineQueryResult = priceAdjustmentService.queryLine(tenant, adjustment.getUuid(),
          lineQd);
      elapsedTimer.stop("\tqueryLines");
      log.info("calculateShopPriceAdjustment???queryLines stop?????????={}", shop);

      if (lineQueryResult.getRecords().isEmpty()) {
        return priceManagers;
      }

      List<String> skuIds = lineQueryResult.getRecords()
          .stream()
          .map(l -> l.getSku().getId())
          .collect(Collectors.toList());
      elapsedTimer.start("\tqueryLatest");
      log.info("calculateShopPriceAdjustment???queryLatest start?????????={}", shop);
      List<ShopPriceManager> managerQueryResult = shopPriceManagerService.queryLatest(tenant, shop, executeDate,
          skuIds);
      elapsedTimer.stop("\tqueryLatest");
      log.info("calculateShopPriceAdjustment???queryLatest stop?????????={}", shop);
      Map<String, ShopPriceManager> priceManagerMap = managerQueryResult.stream()
          .collect(Collectors.toMap(p -> p.getSku().getId(), p -> p));

      Map<String, String> skuPositionMap = lineQueryResult.getRecords().stream().
          filter(i -> i.getSku().getQpc().compareTo(BigDecimal.ONE) == 0 && i.getSkuPosition() != null).collect(
          Collectors.toMap(p -> p.getSku().getGoodsGid(), PriceAdjustmentLine::getSkuPosition)
      );

      String defSkuPriceGrade = priceGrade3(tenant);
      Map<String, String> shopSkuPriceGrade = priceGrade2(tenant, shop);
      List<ShopPriceGradeManager> shopPriceGradeManagers = priceGrade0(tenant, shop, executeDate);

      BaasPriceSkuConfig priceSkuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
      // ??????????????????????????????
      List<ShopPricePromotion> shopPricePromotions = new ArrayList<>();
      String uuid = UUID.randomUUID().toString();
      int index = 0;
      for (PriceAdjustmentLine line : lineQueryResult.getRecords()) {
        index++;
        ShopPriceManager priceManager = new ShopPriceManager();
        priceManager.setOrgId(adjustment.getOrgId());
        priceManager.setEffectiveDate(executeDate);
        priceManager.setEffectiveEndDate(executeDate);
        priceManager.setSku(line.getSku());
        priceManager.setShop(shop);
        priceManager.setShopCode(store.getCode());
        priceManager.setShopName(store.getName());
        priceManager.setInPrice(line.getSkuInPrice());
        priceManager.setBasePrice(line.getSkuBasePrice());
        priceManager.setCalcTailDiff(line.getCalcTailDiff());
        // ??????
        // priceManager.setShopPrice(priceManager.getBasePrice());
        elapsedTimer.start("\tpromotion");
        List<ShopPricePromotion> promotions = promotionList(tenant, shop, executeDate, line.getSku().getId());
        elapsedTimer.stop("\tpromotion");
        log.info("calculateShopPriceAdjustment???promotion stop?????????={},line={}", shop, index);
        if (CollectionUtils.isNotEmpty(promotions)) {
          // ????????????????????????
          ShopPricePromotion lowestPricePromotion = null;
          for (ShopPricePromotion promotion : promotions) {
            BigDecimal shopPrice = null;
            if (line.getSku().getQpc().compareTo(new BigDecimal(priceSkuConfig.getSkuQpcOfPrice())) == 0
                && promotion != null && promotion.getEffectiveEndDate() != null
                && promotion.getEffectiveEndDate().getTime() >= executeDate.getTime()) {
              if (promotion.getType() == PricePromotionLineType.FIX) {
                shopPrice = new BigDecimal(promotion.getRule());
              } else {
                // ??????
                PriceFormulaParam param = new PriceFormulaParam();
                param.setInPrice(line.getSkuInPrice());
                param.setShopPrice(line.getSkuBasePrice());
                shopPrice = spelMgr.calculate(promotion.getRule(), param);
              }
            }
            if (promotion != null
                && (priceManager.getShopPrice() == null || priceManager.getShopPrice().compareTo(shopPrice) > 0)) {
              lowestPricePromotion = promotion;
              priceManager.setShopPrice(shopPrice);
              priceManager.setEffectiveEndDate(lowestPricePromotion.getEffectiveEndDate());
              priceManager.setPromotionSource(lowestPricePromotion.getSource());
            }

            if (priceManager.getShopPrice() != null
                && priceManager.getShopPrice().compareTo(priceManager.getBasePrice()) != 0) {
              elapsedTimer.start("\tpriceCalculateMgr.calculate");
              line.setSkuShopPrice(priceManager.getShopPrice());
              // ??????salePrice
              priceCalculateMgr.calculate(tenant, adjustment.getOrgId(), line, false);
              elapsedTimer.stop("\tpriceCalculateMgr.calculate");
            }
          }
          if (lowestPricePromotion != null) {
            shopPricePromotions.add(lowestPricePromotion);
          }
        }
        elapsedTimer.start("\tSkuPosition");
        String skuPosition = line.getSkuPosition();
        if (StringUtils.isEmpty(skuPosition)) {
          skuPosition = skuPositionMap.get(line.getSku().getGoodsGid());
          // ??????????????????
          if (priceSkuConfig.isEnableChecker()) {
            String tempSkuPosition = "";
            List<PriceAdjustmentLine> lines = priceAdjustmentService.listBySkuGid(tenant, adjustment.getUuid(),
                line.getSku().getGoodsGid());
            if (CollectionUtils.isNotEmpty(lines)) {
              for (PriceAdjustmentLine i : lines) {
                if (i.getSku().getQpc().compareTo(BigDecimal.ONE) == 0 && i.getSkuPosition() != null) {
                  tempSkuPosition = i.getSkuPosition();
                }
              }
            }
            log.info("????????????skuPosition???????????????={}?????????{} VS ??????{}", skuPosition.equals(tempSkuPosition), tempSkuPosition, skuPosition);
          }
        }
        elapsedTimer.stop("\tSkuPosition");

        elapsedTimer.start("\tpriceGrade");
        log.info("calculateShopPriceAdjustment???priceGrade start?????????={},line={}", shop, index);
        String priceGrade = priceGrade1(shopPriceGradeManagers, line.getSkuGroup(), skuPosition);
        if (priceGrade == null) {
          priceGrade = shopSkuPriceGrade.get(buildPriceGradeKey(line.getSkuGroup(), skuPosition));
        }
        if (priceGrade == null) {
          priceGrade = defSkuPriceGrade;
        }
        // ??????????????????
        if (priceSkuConfig.isEnableChecker()) {
          String tempPriceGrade = priceGrade(tenant, shop, executeDate, line.getSkuGroup(), skuPosition);
          log.info("????????????priceGrade???????????????={}?????????{} VS ??????{}", priceGrade.equals(tempPriceGrade), tempPriceGrade, priceGrade);
        }
        elapsedTimer.stop("\tpriceGrade");
        log.info("calculateShopPriceAdjustment???priceGrade stop?????????={},line={}", shop, index);
        String finalPriceGrade = priceGrade;
        PriceGradeSalePrice salePrice = line.getPriceGrades()
            .stream()
            .filter(p -> finalPriceGrade.equals(p.getGrade().getId()))
            .findFirst()
            // ??????
            .orElse(null);
        if (salePrice == null) {
          priceManager.setSalePrice(null);
        } else {
          priceManager.setSalePrice(salePrice.getPrice());
        }

        ShopPriceManager yesterday = priceManagerMap.get(line.getSku().getId());
        int changed = (yesterday == null) ? 7 : 0;

        if (yesterday != null) {
          if (!equals(priceManager.getBasePrice(), yesterday.getBasePrice())) {
            changed = changed | 1;
          }
          if (!equals(priceManager.getSalePrice(), yesterday.getSalePrice())) {
            changed = changed | 2;
          }
          if (!equals(priceManager.getEffectiveDate(), yesterday.getEffectiveDate())
              || !equals(priceManager.getEffectiveEndDate(), yesterday.getEffectiveEndDate())
              || !equals(priceManager.getShopPrice(), yesterday.getShopPrice())
              || !equals(priceManager.getPromotionSource(), yesterday.getPromotionSource())) {
            changed = changed | 4;
          }
        }

        priceManager.setChanged(changed);
        priceManagers.add(priceManager);
      }
      long start = System.currentTimeMillis();
      if (CollectionUtils.isNotEmpty(shopPricePromotions)) {
        shopPricePromotionService.batchSave(tenant, shopPricePromotions);
      }
      long end = System.currentTimeMillis();
      log.info("uuid={},shop={},overtime={}", uuid, shop, (end - start));
      // ?????????
      BigDecimal arrears = new BigDecimal("0.09");
      if (priceSkuConfig != null || priceSkuConfig.getArrears() > 0) {
        arrears = new BigDecimal(String.valueOf(priceSkuConfig.getArrears())).divide(new BigDecimal("100"), 2,
            BigDecimal.ROUND_HALF_UP);
      }

      if (priceSkuConfig != null && priceSkuConfig.getSkuQpcOfPrice() != null
          && priceSkuConfig.getSkuQpcOfPrice() != 0) {
        Integer qpcOfPrice = priceSkuConfig.getSkuQpcOfPrice();
        // ????????????
        // ????????????????????? ,KEY = GID
        Map<String, ShopPriceManager> tempPriceManagerMap = priceManagers.stream()
            .filter(i -> i.getSku().getQpc().compareTo(new BigDecimal(qpcOfPrice)) == 0)
            .collect(Collectors.toMap(i -> i.getSku().getGoodsGid(), i -> i));
        for (ShopPriceManager item : priceManagers) {
          String gid = item.getSku().getGoodsGid();
          BigDecimal qpc = item.getSku().getQpc();

          ShopPriceManager priceManager = tempPriceManagerMap.get(gid);
          if (priceManager == null) {
            continue;
          }
          // ?????????
          if (priceManager.getSku().getQpc().compareTo(new BigDecimal(qpcOfPrice)) != 0) {
            // ????????????
            item.setChanged(priceManager.getChanged());
            //
            item.setBasePrice(resetPrice(priceManager.getBasePrice(), qpc, qpcOfPrice));
            item.setShopPrice(resetPrice(priceManager.getShopPrice(), qpc, qpcOfPrice));
            item.setSalePrice(
                resetPrice(priceManager.getSalePrice(), qpc, qpcOfPrice, priceManager.getCalcTailDiff(), arrears));
          }
        }
        if (isSave) {
          elapsedTimer.start("\tbatchSave");
          log.info("calculateShopPriceAdjustment???batchSave start?????????={}", shop);
          shopPriceManagerService.batchSave(tenant, shop, executeDate, priceManagers);
          log.info("calculateShopPriceAdjustment???batchSave start?????????={}", shop);

          elapsedTimer.stop("\tbatchSave");
        }
      }
      priceCalculateMgr.end();
      return priceManagers;
    } finally {
      elapsedTimer.stop("calculateShopPriceAdjustment");
    }
  }

  /**
   * ?????????????????????
   */
  @Tx
  public void calculateTempShopPriceAdjustment(String tenant, String shop, Date executeDate,
      TempShopPriceAdjustment tempShopPriceAdjustment, PriceAdjustment adjustment) throws BaasException {
    ElapsedTimer elapsedTimer = ElapsedTimer.getThreadInstance();
    elapsedTimer.start("calculateTempShopPriceAdjustment");
    try {
      if (adjustment == null) {
        log.info("???????????????????????????");
        return;
      }
      log.info("??????????????????????????????????????????={},??????={},TempShopPriceAdjustment={},PriceAdjustment={}", tenant, shop,
          tempShopPriceAdjustment.getUuid(), adjustment.getUuid());
      StoreFilter storeFilter = new StoreFilter();
      storeFilter.setIdIn(Arrays.asList(shop));
      QueryResult<Store> storeQueryResult = storeService.query(tenant, storeFilter);
      if (storeQueryResult.getRecords().isEmpty()) {
        log.info("??????<{}>??????????????????", shop);
        return;
      }
      Store store = storeQueryResult.getRecords().get(0);

      List<TempShopPriceAdjustmentLine> tempLines = tempShopPriceAdjustmentService.getLines(tenant,
          tempShopPriceAdjustment.getUuid());
      if (tempLines.isEmpty()) {
        log.info("???????????????<{}>???????????????????????????", tempShopPriceAdjustment.getUuid());
        return;
      }
      QueryDefinition lineQd = new QueryDefinition();
      // ???????????????????????????????????????????????????????????????1000
      elapsedTimer.start("\tqueryLines");
      QueryResult<PriceAdjustmentLine> lineQueryResult = priceAdjustmentService.queryLine(tenant, adjustment.getUuid(),
          lineQd);
      elapsedTimer.stop("\tqueryLines");
      if (lineQueryResult.getRecords().isEmpty()) {
        log.info("?????????<{}>???????????????????????????", adjustment.getUuid());
        return;
      }
      BaasPriceSkuConfig priceSkuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
      // ?????????????????????
      Map<String, BigDecimal> tempShopPriceMap = tempLines.stream()
          .collect(
              Collectors.toMap(TempShopPriceAdjustmentLine::getSkuId, TempShopPriceAdjustmentLine::getBaseShopPrice));
      List<TempShopPriceManager> priceManagers = new ArrayList<>();
      for (PriceAdjustmentLine line : lineQueryResult.getRecords()) {
        BigDecimal baseShopPrice = tempShopPriceMap.get(line.getSku().getId());
        if (baseShopPrice == null) {
          continue;
        }
        log.info("???????????????????????????<id={},code={}>", line.getSku().getId(), line.getSku().getCode());
        TempShopPriceManager priceManager = new TempShopPriceManager();
        priceManager.setEffectiveDate(executeDate);
        priceManager.setEffectiveEndDate(executeDate);
        priceManager.setSkuId(line.getSku().getId());
        priceManager.setSkuCode(line.getSku().getCode());
        priceManager.setSkuName(line.getSku().getName());
        priceManager.setSkuGid(line.getSku().getGoodsGid());
        priceManager.setSkuQpc(line.getSku().getQpc());
        priceManager.setShop(shop);
        priceManager.setShopCode(store.getCode());
        priceManager.setShopName(store.getName());
        priceManager.setBasePrice(baseShopPrice);
        priceManager.setTenant(tenant);
        priceManager.setOrgId(tempShopPriceAdjustment.getOrgId());

        List<ShopPricePromotion> promotions = promotionList(tenant, shop, executeDate, line.getSku().getId());
        if (CollectionUtils.isNotEmpty(promotions)) {
          // ????????????????????????
          ShopPricePromotion lowestPricePromotion = null;
          BigDecimal lowestShopPrice = null;
          for (ShopPricePromotion promotion : promotions) {
            if (line.getSku().getQpc().compareTo(new BigDecimal(priceSkuConfig.getSkuQpcOfPrice())) != 0
                || promotion == null || promotion.getEffectiveEndDate() == null
                || promotion.getEffectiveEndDate().getTime() < executeDate.getTime()) {
              continue;
            }
            BigDecimal shopPrice = null;
            if (promotion.getType() == PricePromotionLineType.FIX) {
              shopPrice = new BigDecimal(promotion.getRule());
            } else {
              // ??????
              PriceFormulaParam param = new PriceFormulaParam();
              param.setInPrice(line.getSkuInPrice());
              param.setShopPrice(baseShopPrice);
              shopPrice = spelMgr.calculate(promotion.getRule(), param);
            }
            if (lowestPricePromotion == null) {
              lowestPricePromotion = promotion;
              lowestShopPrice = shopPrice;
              priceManager.setShopPrice(shopPrice);
              priceManager.setEffectiveEndDate(lowestPricePromotion.getEffectiveEndDate());
              priceManager.setPromotionSource(lowestPricePromotion.getSource());
            }

            if (lowestShopPrice.compareTo(shopPrice) > 0) {
              lowestPricePromotion = promotion;
              lowestShopPrice = shopPrice;
              priceManager.setShopPrice(shopPrice);
              priceManager.setEffectiveEndDate(lowestPricePromotion.getEffectiveEndDate());
              priceManager.setPromotionSource(lowestPricePromotion.getSource());
            }
          }
        }
        priceManagers.add(priceManager);
      }
      if (CollectionUtils.isNotEmpty(priceManagers)) {
        tempShopPriceManagerService.batchSave(tenant, shop, executeDate, priceManagers);
      }
    } finally {
      elapsedTimer.stop("calculateTempShopPriceAdjustment");
    }

  }

  private BigDecimal resetPrice(BigDecimal price, BigDecimal qpc, Integer qpcOfPrice) {
    return price.multiply(qpc).divide(new BigDecimal(qpcOfPrice), 2, BigDecimal.ROUND_HALF_UP);
  }

  private BigDecimal resetPrice(BigDecimal price, BigDecimal qpc, Integer qpcOfPrice, boolean calcTailDiff,
      BigDecimal arrears) {
    price = resetPrice(price, qpc, qpcOfPrice);
    if (calcTailDiff) {
      price = priceCalculateMgr.calcArrears(price, arrears);
    }
    return price;
  }

  private static boolean equals(BigDecimal price1, BigDecimal price2) {
    if (price1 == null) {
      return price2 == null;
    }
    if (price2 == null) {
      return false;
    }
    return price1.compareTo(price2) == 0;
  }

  public static boolean equals(Date d1, Date d2) {
    if (d1 == null || d2 == null) {
      return false;
    }
    return d1.equals(d2);
  }

  public static boolean equals(String s1, String s2) {
    if (s1 == null || s2 == null) {
      return false;
    }
    return s1.equals(s2);
  }

  private List<ShopPricePromotion> promotionList(String tenant, String shop, Date executeDate, String skuId) {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ShopPricePromotionManager.Queries.SHOP, Cop.EQUALS, shop);
    qd.addByField(ShopPricePromotionManager.Queries.SKU_ID, Cop.EQUALS, skuId);
    qd.addByField(ShopPricePromotionManager.Queries.EFFECTIVE_START_DATE, Cop.LESS, DateUtils.addDays(executeDate, 1));
    qd.addByField(ShopPricePromotionManager.Queries.EFFECTIVE_END_DATE, Cop.GREATER_OR_EQUALS, executeDate);
    QueryOrder modifyQueryOrder = new QueryOrder(ShopPricePromotionManager.Queries.SOURCE_LAST_MODIFIED,
        QueryOrderDirection.desc);
    qd.setOrders(Lists.newArrayList(modifyQueryOrder));
    QueryResult<ShopPricePromotionManager> result = shopPricePromotionManagerService.query(tenant, qd);
    if (!result.getRecords().isEmpty()) {
      return (List) result.getRecords();
    }
    ShopPricePromotion shopPricePromotion = shopPricePromotionService.get(tenant, shop, skuId);
    if (shopPricePromotion != null) {
      return Arrays.asList(shopPricePromotion);
    }
    return null;
  }

  private String priceGrade(String tenant, String shop, Date executeDate, String skuGroup, String skuPosition)
      throws BaasException {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ShopPriceGradeManager.Queries.SHOP, Cop.EQUALS, shop);
    qd.addByField(ShopPriceGradeManager.Queries.SKU_GROUP, Cop.EQUALS, skuGroup);
    qd.addByField(ShopPriceGradeManager.Queries.SKU_POSITION, Cop.EQUALS, skuPosition);
    qd.addByField(ShopPriceGradeManager.Queries.EFFECTIVE_START_DATE, Cop.LESS, DateUtils.addDays(executeDate, 1));
    qd.addOrder(ShopPriceGradeManager.Queries.EFFECTIVE_START_DATE, QueryOrderDirection.desc); // ??????????????????
    qd.setPage(0);
    qd.setPageSize(1);
    QueryResult<ShopPriceGradeManager> result = shopPriceGradeManagerService.query(tenant, qd);
    if (!result.getRecords().isEmpty()) {
      return result.getRecords().get(0).getPriceGrade();
    }

    ShopPriceGrade grade = shopPriceGradeService.get(tenant, shop, skuGroup, skuPosition);
    if (grade != null) {
      return grade.getPriceGrade();
    }
    PriceGrade priceGrade = priceGradeService.getDftPriceGrade(tenant);
    if (priceGrade != null) {
      return priceGrade.getUuid() + "";
    }
    return null;
  }

  private List<ShopPriceGradeManager> priceGrade0(String tenant, String shop, Date executeDate) {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ShopPriceGradeManager.Queries.SHOP, Cop.EQUALS, shop);
    qd.addByField(ShopPriceGradeManager.Queries.EFFECTIVE_START_DATE, Cop.LESS, DateUtils.addDays(executeDate, 1));
    qd.addOrder(ShopPriceGradeManager.Queries.EFFECTIVE_START_DATE, QueryOrderDirection.desc); // ??????????????????
    qd.addOrder(ShopPriceGradeManager.Queries.SOURCE_CREATE_TIME, QueryOrderDirection.desc);
    QueryResult<ShopPriceGradeManager> result = shopPriceGradeManagerService.query(tenant, qd);
    if (!result.getRecords().isEmpty()) {
      return result.getRecords();
    }
    return null;
  }

  private String priceGrade1(List<ShopPriceGradeManager> list, String skuGroup, String skuPosition) {
    if (list != null) {
      for (ShopPriceGradeManager item : list) {
        if (item.getSkuGroup().equals(skuGroup) && item.getSkuPosition().equals(skuPosition)) {
          return item.getPriceGrade();
        }
      }
    }
    return null;
  }

  private Map<String, String> priceGrade2(String tenant, String shop) {
    List<ShopPriceGrade> list = shopPriceGradeService.listByShop(tenant, shop);
    if (list != null) {
      return list.stream().collect(Collectors.toMap(i -> buildPriceGradeKey(i.getSkuGroup(), i.getSkuPosition()), ShopPriceGrade::getPriceGrade));
    }
    return new HashMap<>();
  }

  private String priceGrade3(String tenant) throws BaasException {
    PriceGrade priceGrade = priceGradeService.getDftPriceGrade(tenant);
    if (priceGrade != null) {
      return priceGrade.getUuid() + "";
    }
    return null;
  }

  private String buildPriceGradeKey(String skuGroup, String skuPosition) {
    return skuGroup + "_" + skuPosition;
  }

  /**
   * ?????????????????????????????????????????????????????????
   *
   * @param tenant
   *     ??????
   * @param executeDate
   *     ????????????
   * @return ??????????????????????????????
   */
  private PriceAdjustment priceAdjustment(String tenant, String orgId, Date executeDate) {
    // ?????????????????????????????????0??????????????????????????????????????????
    Date furthestEffectiveStartDate = DateUtils.addDays(executeDate, 1);
    // ?????????????????????
    QueryDefinition qd = new QueryDefinition();
    qd.setPage(0);
    qd.setPageSize(1);
    qd.addByField(PriceAdjustment.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PriceAdjustment.Queries.ORG_ID, Cop.EQUALS, orgId);
    qd.addByField(PriceAdjustment.Queries.STATE, Cop.IN, PriceAdjustmentState.AUDITED.name(),
        PriceAdjustmentState.PUBLISHED.name());
    // ??????????????????????????????????????????????????????
    qd.addByField(PriceAdjustment.Queries.EFFECTIVE_START_DATE, Cop.LESS, furthestEffectiveStartDate);
    qd.addOrder(PriceAdjustment.Queries.EFFECTIVE_START_DATE, QueryOrderDirection.desc); // ????????????????????????????????????????????????
    qd.addOrder(PriceAdjustment.Queries.FLOW_NO, QueryOrderDirection.desc); // ??????
    QueryResult<PriceAdjustment> result = priceAdjustmentService.query(tenant, qd);
    if (!result.getRecords().isEmpty()) {
      return result.getRecords().get(0);
    }

    return null;
  }

  public TempShopPriceAdjustment tempShopPriceAdjustment(String tenant, String orgId, Date executeDate) {
    // ?????????????????????????????????0??????????????????????????????????????????
    Date furthestEffectiveStartDate = DateUtils.addDays(executeDate, 1);
    // ?????????????????????
    QueryDefinition qd = new QueryDefinition();
    qd.setPage(0);
    qd.setPageSize(1);
    qd.addByField(TempShopPriceAdjustment.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(TempShopPriceAdjustment.Queries.ORG_ID, Cop.EQUALS, orgId);
    qd.addByField(TempShopPriceAdjustment.Queries.STATE, Cop.IN, TempShopPriceAdjustmentState.CONFIRMED.name(),
        TempShopPriceAdjustmentState.EFFECTED.name());
    // ??????????????????????????????????????????????????????
    qd.addByField(TempShopPriceAdjustment.Queries.EFFECTIVE_START_DATE, Cop.LESS, furthestEffectiveStartDate);
    qd.addOrder(TempShopPriceAdjustment.Queries.EFFECTIVE_START_DATE, QueryOrderDirection.desc); // ????????????????????????????????????????????????
    qd.addOrder(TempShopPriceAdjustment.Queries.FLOW_NO, QueryOrderDirection.desc); // ??????
    QueryResult<TempShopPriceAdjustment> result = tempShopPriceAdjustmentService.query(tenant, qd);
    if (!result.getRecords().isEmpty()) {
      return result.getRecords().get(0);
    }

    return null;
  }

  private List<Store> queryStores(String tenant, String orgId, ElapsedTimer timer) throws BaasException {
    List<Store> list = new ArrayList<>();
    StoreFilter storeFilter = new StoreFilter();
    storeFilter.setOrgIdEq(DefaultOrgIdConvert.toMasDefOrgId(orgId));
    QueryResult<Store> shopResult = storeService.query(tenant, storeFilter);
    for (Store store : shopResult.getRecords()) {
      boolean notHaGrade;
      QueryDefinition qd = new QueryDefinition();
      qd.addByField(ShopPriceGradeManager.Queries.SHOP, Cop.EQUALS, store.getId());
      qd.addByField(ShopPriceGradeManager.Queries.ORG_ID, Cop.EQUALS, orgId);
      qd.setPage(0);
      qd.setPageSize(1);
      timer.start("\tshopPriceGradeManagerService.query");
      notHaGrade = shopPriceGradeManagerService.query(tenant, qd).getRecords().isEmpty();
      timer.stop("\tshopPriceGradeManagerService.query");
      if (notHaGrade) {
        qd = new QueryDefinition();
        qd.addByField(ShopPriceGrade.Queries.SHOP, Cop.EQUALS, store.getId());
        qd.setPage(0);
        qd.setPageSize(1);
        timer.start("\tshopPriceGradeService.query");
        notHaGrade = shopPriceGradeService.query(tenant, qd).getRecords().isEmpty();
        timer.stop("\tshopPriceGradeService.query");
      }
      if (notHaGrade) {
        // ??????????????????????????????
        log.info("???????????????????????????????????????store={}", JsonUtil.objectToJson(store));
        continue;
      }
      list.add(store);
    }
    return list;
  }

  private List<ShopPricePromotionManager> buildPricePromotionManager(String tenant, String orgId, PricePromotion promotion, List<PricePromotionShop> shops, QueryDefinition lineQd) throws BaasException {
    QueryResult<PricePromotionLine> lineQueryResult = pricePromotionService.queryLine(tenant, promotion.getUuid(), lineQd);
    if (lineQueryResult.getRecords().isEmpty()) {
      return null;
    }
    BaasPriceSkuConfig priceSkuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
    List<ShopPricePromotionManager> promotionPriceManagers = new ArrayList<>();
    for (PricePromotionLine line : lineQueryResult.getRecords()) {
      List<PriceSku> priceSkus = new ArrayList<>();
      // ???????????????????????????????????????????????????????????????????????????
      if (PricePromotionType.FULL_DISCOUNT_PRMT.equals(promotion.getType())) {
        String skuGroup = line.getSkuGroup();
        List<SkuGroupCategory> skuGroupCategories = skuGroupService.listByGroupId(tenant, skuGroup);
        if (CollectionUtils.isEmpty(skuGroupCategories)) {
          log.info("??????????????????????????????????????????={}", skuGroup);
          continue;
        }
        List<String> categoryIds = skuGroupCategories.stream()
            .map(s -> s.getCategory().getId())
            .collect(Collectors.toList());
        if (priceSkuConfig != null && "V2".equalsIgnoreCase(priceSkuConfig.getQueryPriceSkuMgr())) {
          long sts = System.currentTimeMillis();
          priceSkus = getPriceSkusV2(tenant, orgId, categoryIds);
          long ets1 = System.currentTimeMillis();
          if (priceSkuConfig.isEnableChecker()) {
            List<PriceSku> old = getPriceSkus(tenant, orgId, categoryIds);
            long ets2 = System.currentTimeMillis();
            log.info("??????PriceSku??????????????????vs????????????{} vs {}", (ets1 - sts), (ets2 - ets1));
            log.info("??????PriceSku??????????????????vs????????????{} vs {}", JSONUtil.safeToJson(priceSkus), JSONUtil.safeToJson(old));
          }
        } else {
          priceSkus = getPriceSkus(tenant, orgId, categoryIds);
        }
      } else {
        // ????????????????????????
        priceSkus.add(line.getSku());
      }
      // ??????
      if (CollectionUtils.isNotEmpty(priceSkus)) {
        for (PricePromotionShop shop : shops) {
          for (PriceSku sku : priceSkus) {
            ShopPricePromotionManager item = buildShopPricePromotionManager(shop.getShop(), sku, line, promotion);
            promotionPriceManagers.add(item);
          }
        }
      }
    }
    return promotionPriceManagers;
  }

  private List<PriceSku> getPriceSkus(String tenant, String orgId, List<String> categoryIds) throws BaasException {
    SkuFilter skuFilter = new SkuFilter();
    skuFilter.setCategoryIdIn(categoryIds);
    QueryResult<PriceSkuConfig> result = priceSkuConfigService.query(tenant, orgId, skuFilter);
    return result.getRecords().stream().map(PriceSkuConfig::getSku).collect(Collectors.toList());
  }

  private List<PriceSku> getPriceSkusV2(String tenant, String orgId, List<String> categoryIds) throws BaasException {
    return priceSkuConfigService.getPriceSkusByCategoryIds(tenant, orgId, categoryIds);
  }

  private QueryResult<PricePromotion> queryPricePromotion(String tenant, String orgId, Date executeDate) {
    executeDate = DateUtils.truncate(executeDate, Calendar.DATE);
    Date executeDateAddOne = DateUtils.addDays(executeDate, 1); // ??????????????????
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PricePromotion.Queries.ORG_ID, Cop.EQUALS, orgId);
    qd.addByField(PricePromotion.Queries.STATE, Cop.EQUALS, PricePromotionState.AUDITED.name());
    qd.addByField(PricePromotion.Queries.EFFECTIVE_START_DATE, Cop.LESS, executeDateAddOne);
    qd.addOrder(PricePromotion.Queries.FLOW_NO, QueryOrderDirection.desc);
    qd.addOrder(PricePromotion.Queries.EFFECTIVE_START_DATE, QueryOrderDirection.desc);
    return pricePromotionService.query(tenant, qd);
  }

  private ShopPricePromotionManager buildShopPricePromotionManager(String shop, PriceSku sku, PricePromotionLine line,
      PricePromotion promotion) {
    ShopPricePromotionManager item = new ShopPricePromotionManager();
    item.setOrgId(promotion.getOrgId());
    item.setShop(shop);
    item.setSku(sku);
    item.setType(line.getType());
    item.setRule(line.getRule());
    item.setSource(promotion.getUuid());
    item.setPricePromotionType(promotion.getType().name());
    item.setEffectiveStartDate(promotion.getEffectiveStartDate());
    item.setEffectiveEndDate(promotion.getEffectiveEndDate());
    item.setSourceLastModified(promotion.getLastModifyInfo().getTime());
    item.setPricePromotionType(promotion.getType().name());
    item.setSource(promotion.getUuid());
    return item;
  }

  public void cancelTempShopAdjustment(String tenant, String orgId, Date executeDate, OperateInfo operateInfo) {
    TempShopPriceAdjustment tempShopPriceAdjustment = tempShopPriceAdjustment(tenant, orgId, executeDate);
    if (tempShopPriceAdjustment == null) {
      log.info("???????????????????????????????????????????????????");
      return;
    }
    tempShopPriceAdjustmentDao.cancel(tenant, orgId, executeDate, tempShopPriceAdjustment.getUuid(), operateInfo);
  }

  private boolean checkShopGrade(String tenant, String orgId, Store store) {
    boolean notHaGrade;
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ShopPriceGradeManager.Queries.SHOP, Cop.EQUALS, store.getId());
    qd.setPage(0);
    qd.setPageSize(1);
    notHaGrade = shopPriceGradeManagerService.query(tenant, qd).getRecords().isEmpty();
    if (notHaGrade) {
      qd = new QueryDefinition();
      qd.addByField(ShopPriceGrade.Queries.SHOP, Cop.EQUALS, store.getId());
      qd.setPage(0);
      qd.setPageSize(1);
      notHaGrade = shopPriceGradeService.query(tenant, qd).getRecords().isEmpty();
    }
    if (notHaGrade) {
      // ??????????????????????????????
      log.info("???????????????????????????????????????store={}", JsonUtil.objectToJson(store));
      return false;
    }
    return true;
  }
}
