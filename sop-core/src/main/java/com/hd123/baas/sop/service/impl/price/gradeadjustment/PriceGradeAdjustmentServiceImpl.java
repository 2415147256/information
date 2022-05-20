package com.hd123.baas.sop.service.impl.price.gradeadjustment;

import java.util.*;
import java.util.stream.Collectors;

import com.hd123.baas.sop.service.api.entity.PriceGrade;
import com.hd123.baas.sop.service.api.grade.PriceGradeService;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustment;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceTaskMsg;
import com.hd123.rumba.commons.biz.query.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.service.api.entity.SkuGroup;
import com.hd123.baas.sop.service.api.entity.SkuPosition;
import com.hd123.baas.sop.service.api.group.SkuGroupService;
import com.hd123.baas.sop.service.api.position.SkuPositionService;
import com.hd123.baas.sop.service.api.price.gradeadjustment.*;
import com.hd123.baas.sop.service.dao.price.gradeadjustment.PriceGradeAdjustmentDaoBof;
import com.hd123.baas.sop.service.dao.price.gradeadjustment.PriceGradeAdjustmentLineDaoBof;
import com.hd123.baas.sop.service.dao.price.gradeadjustment.PriceGradeAdjustmentShopDaoBof;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/12.
 */
@Service
@Slf4j
public class PriceGradeAdjustmentServiceImpl implements PriceGradeAdjustmentService {

  @Autowired
  private PriceGradeAdjustmentDaoBof adjustmentDao;
  @Autowired
  private PriceGradeAdjustmentLineDaoBof adjustmentLineDao;
  @Autowired
  private PriceGradeAdjustmentShopDaoBof adjustmentShopDao;
  @Autowired
  private StoreService storeService;
  @Autowired
  private BillNumberMgr billNumberMgr;
  @Autowired
  private SkuGroupService groupService;
  @Autowired
  private SkuPositionService positionService;
  @Autowired
  private PriceGradeService priceGradeService;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  public PriceGradeAdjustment create(String tenant, String orgId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    PriceGradeAdjustment adjustment = new PriceGradeAdjustment();
    adjustment.setTenant(tenant);
    adjustment.setOrgId(orgId);
    adjustment.setUuid(UUID.randomUUID().toString());
    adjustment.setFlowNo(null); // 保存时再生成单号
    adjustment.setState(PriceGradeAdjustmentState.INIT);
    adjustment.setEffectiveStartDate(null);
    adjustment.setLines(new ArrayList<>());
    adjustment.setShops(new ArrayList<>());
    adjustmentDao.insert(tenant, adjustment, operateInfo);
    return adjustment;
  }

  @Override
  public void addLine(String tenant, String uuid, String skuGroup, String skuPosition, String priceGrade,
      OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(adjustment);

    PriceGradeAdjustmentLine check = adjustmentLineDao.getByGroupAndPosition(tenant, uuid, skuGroup, skuPosition);
    if (check != null) {
      throw new BaasException("对应的类别和分组已添加");
    }

    PriceGrade grade = priceGradeService.get(tenant, Integer.parseInt(priceGrade));
    if (grade == null) {
      throw new BaasException("价格级不存在。");
    }

    String groupName = "-";
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuGroup.Queries.UUID, Cop.EQUALS, skuGroup);
    SkuGroup group = groupService.get(tenant, skuGroup);
    if (group != null) {
      groupName = group.getName();
    }
    String positionName = "-";
    QueryDefinition positionQd = new QueryDefinition();
    positionQd.addByField(SkuPosition.Queries.UUID, Cop.EQUALS, skuPosition);
    List<SkuPosition> pos = positionService.query(tenant, positionQd).getRecords();
    if (CollectionUtils.isNotEmpty(pos)) {
      positionName = pos.get(0).getName();
    }

    PriceGradeAdjustmentLine line = new PriceGradeAdjustmentLine();
    line.setTenant(tenant);
    line.setOwner(uuid);
    line.setSkuGroup(skuGroup);
    line.setSkuGroupName(groupName);
    line.setSkuPosition(skuPosition);
    line.setSkuPositionName(positionName);
    // todo
    line.setPriceGrade(priceGrade);
    line.setPriceGradeName(grade.getName());
    adjustmentLineDao.insert(tenant, uuid, line);
  }

  @Override
  public void batchAddLine(String tenant, String uuid, List<PriceGradeAdjustmentLine> lines, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(lines,"lines");
    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(adjustment);
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceGradeAdjustmentLine.Queries.OWNER,Cop.EQUALS,uuid);
    List<PriceGradeAdjustmentLine> adjustmentLines = this.queryLine(tenant, uuid,qd).getRecords();
    Map<String, PriceGradeAdjustmentLine> groupPositionMap = adjustmentLines.stream().collect(Collectors.toMap(s -> s.getSkuGroup() + s.getSkuPosition(), s -> s));
    if (groupPositionMap != null){
      for (PriceGradeAdjustmentLine line : lines) {
        String key = line.getSkuGroup() + line.getSkuPosition();
        PriceGradeAdjustmentLine existAdjustmentLine = groupPositionMap.get(key);
        if (existAdjustmentLine != null){
          throw new BaasException("对应的类别[{0}]和分组[{1}]已添加",existAdjustmentLine.getSkuGroupName(),existAdjustmentLine.getSkuPositionName());
        }
      }
    }
    List<SkuGroup> groups = groupService.list(tenant);
    Map<String, SkuGroup> groupMap = groups.stream().collect(Collectors.toMap(s -> String.valueOf(s.getUuid()), s -> s));
    List<SkuPosition> positions = positionService.list(tenant);
    Map<String, SkuPosition> positionMap = positions.stream().collect(Collectors.toMap(s -> String.valueOf(s.getUuid()), s -> s));
    List<PriceGrade> grades = priceGradeService.list(tenant);
    Map<String, PriceGrade> gradeMap = grades.stream().collect(Collectors.toMap(s -> String.valueOf(s.getUuid()), s -> s));
    for (PriceGradeAdjustmentLine line : lines) {
      SkuGroup skuGroup = groupMap.get(line.getSkuGroup());
      if (skuGroup == null){
        throw new BaasException("类别{0}不存在",skuGroup.getUuid());
      }
      line.setSkuGroupName(skuGroup.getName());
      SkuPosition skuPosition = positionMap.get(line.getSkuPosition());
      if (skuPosition == null){
        throw new BaasException("商品定位{0}不存在",skuPosition.getName());
      }
      line.setSkuPositionName(skuPosition.getName());
      PriceGrade priceGrade = gradeMap.get(line.getPriceGrade());
      if(priceGrade == null){
        throw new BaasException("价格级{0}不存在",priceGrade.getUuid());
      }
      line.setPriceGradeName(priceGrade.getName());
    }
    adjustmentLineDao.batchInsert(tenant,uuid,lines);
  }

  @Override
  public void saveLine(String tenant, String uuid, PriceGradeAdjustmentLine line, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(line, "line");
    Assert.hasText(line.getSkuGroup(), "line.skuGroup");
    Assert.hasText(line.getSkuPosition(), "line.skuPosition");
    Assert.hasText(line.getPriceGrade(), "line.priceGrade");

    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(adjustment);

    PriceGradeAdjustmentLine ever = adjustmentLineDao.getByGroupAndPosition(tenant, uuid, line.getSkuGroup(),
        line.getSkuPosition());
    if (ever != null) {
      ever.setPriceGrade(line.getPriceGrade());
      ever.setPriceGradeName(line.getPriceGradeName());
      adjustmentLineDao.update(tenant, uuid, ever);
    } else {
      adjustmentLineDao.insert(tenant, uuid, line);
    }
  }

  @Override
  public void deleteLines(String tenant, String uuid, Collection<String> lineIds, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(adjustment);
    //
    adjustmentLineDao.batchDelete(tenant, uuid, lineIds);
  }

  @Override
  @Tx
  public void editLineGrade(String tenant, String uuid, Collection<String> lineIds, String priceGrade,
      OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(priceGrade);
    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(adjustment);
    if (CollectionUtils.isEmpty(lineIds)) {
      return;
    }
    //
    List<PriceGradeAdjustmentLine> lines = adjustmentLineDao.list(tenant, uuid, lineIds);
    if (lines.isEmpty()) {
      return;
    }

    PriceGrade grade = priceGradeService.get(tenant, Integer.parseInt(priceGrade));
    if (grade == null) {
      throw new BaasException("价格级不存在");
    }

    for (PriceGradeAdjustmentLine line : lines) {
      line.setPriceGrade(priceGrade);
      line.setPriceGradeName(grade.getName());
      adjustmentLineDao.update(tenant, uuid, line);
    }
  }

  @Override
  @Tx
  public void saveAndSubmit(String tenant, PriceGradeAdjustment adjustment, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(adjustment, "adjustment");
    Assert.hasText(adjustment.getUuid(), "adjustment.uuid");
    Assert.notNull(adjustment.getEffectiveStartDate(), "adjustment.effectiveStartDate");

    PriceGradeAdjustment ever = this.get(tenant, adjustment.getUuid(),PriceGradeAdjustment.FETCH_SHOP);
    if (ever == null) {
      throw new BaasException("单据不存在");
    }
    QueryDefinition lineQd = new QueryDefinition();
    lineQd.addByOperation(PriceGradeAdjustmentLine.Queries.PRICE_GRADE_IS_NULL);
    if (adjustmentLineDao.queryCount(tenant, adjustment.getUuid(), lineQd) > 0) {
      throw new BaasException("存在未设置价格级的行");
    }

    if (adjustmentLineDao.queryCount(tenant, adjustment.getUuid(), new QueryDefinition()) <= 0) {
      throw new BaasException("至少添加一行");
    }
    List<PriceGradeAdjustmentShop> shops = ever.getShops();
    if (CollectionUtils.isEmpty(shops)){
      throw new BaasException("未关联任何门店");
    }
    List<String> shopIds = shops.stream().map(PriceGradeAdjustmentShop::getShop).collect(Collectors.toList());
    // 查询门店是否存在未来生效的单据
    checkShopRelate(tenant, ever.getOrgId(), shopIds);

    adjustment.setEffectiveStartDate(DateUtils.truncate(adjustment.getEffectiveStartDate(), Calendar.DATE));
    adjustment.setCreateInfo(ever.getCreateInfo());
    adjustment.setState(ever.getState());
    adjustment.setFlowNo(ever.getFlowNo());
    checkEdit(adjustment);
    if (adjustment.getState() == PriceGradeAdjustmentState.INIT) {
      adjustment.setState(PriceGradeAdjustmentState.CONFIRMED);
      adjustment.setFlowNo(billNumberMgr.generateGradeAdjustmentFlowNo(tenant));
    }
    adjustmentDao.update(tenant, adjustment, operateInfo);
  }

  @Override
  public void audit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    if (adjustment.getState() != PriceGradeAdjustmentState.CONFIRMED) {
      throw new BaasException("当前状态不可审核");
    }
    if (adjustmentLineDao.queryCount(tenant, uuid, new QueryDefinition()) <= 0) {
      throw new BaasException("至少添加一行");
    }
    if (adjustmentShopDao.queryCount(tenant, uuid, new QueryDefinition()) <= 0) {
      throw new BaasException("未关联任何门店");
    }
    if (adjustment.getEffectiveStartDate()
        .before(DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE))) {
      throw new BaasException("生效时间最早是二天后");
    }
    adjustmentDao.changeState(tenant, uuid, PriceGradeAdjustmentState.AUDITED, operateInfo);
    // 新增时间的校验
    Date endDate = DateUtils.addDays(new Date(), PriceAdjustment.MIN_EFFECTIVE_DAYS);
    endDate = DateUtils.truncate(endDate, Calendar.DATE);
    // 最后的时间是：明天的00：00：00
    if (adjustment.getEffectiveStartDate().getTime() == endDate.getTime()){
      // 实时推送
      publishShopPriceEvCall(tenant,adjustment.getOrgId(), adjustment.getEffectiveStartDate());
    }
  }

  @Override
  public void cancel(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(reason, "作废原因");
    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    if (adjustment.getState() != PriceGradeAdjustmentState.CONFIRMED
        && adjustment.getState() != PriceGradeAdjustmentState.AUDITED) {
      throw new BaasException("当前状态不可作废");
    }

    adjustmentDao.cancel(tenant, uuid, reason, operateInfo);
  }

  @Override
  public void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    if (adjustment.getState() == PriceGradeAdjustmentState.PUBLISHED) {
      log.info("单据已发布，忽略");
      return;
    }
    if (adjustment.getState() != PriceGradeAdjustmentState.AUDITED) {
      throw new BaasException("当前状态不可发布");
    }
    adjustmentDao.changeState(tenant, uuid, PriceGradeAdjustmentState.PUBLISHED, operateInfo);
  }

  @Override
  public void expire(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    if (adjustment.getState() == PriceGradeAdjustmentState.EXPIRED) {
      log.info("单据已过期，忽略");
      return;
    }
    if (adjustment.getState() != PriceGradeAdjustmentState.PUBLISHED) {
      throw new BaasException("当前状态不可发布");
    }
    adjustmentDao.changeState(tenant, uuid, PriceGradeAdjustmentState.EXPIRED, operateInfo);
  }

  @Override
  public PriceGradeAdjustment get(String tenant, String uuid, String... fetchParts) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    PriceGradeAdjustment adjustment = adjustmentDao.get(tenant, uuid);
    if (adjustment != null) {
      List<PriceGradeAdjustment> adjustments = new ArrayList<>();
      adjustments.add(adjustment);
      fetch(tenant, adjustments, fetchParts);
    }
    return adjustment;
  }

  @Override
  public QueryResult<PriceGradeAdjustment> query(String tenant, QueryDefinition qd, String... fetchParts) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    if (CollectionUtils.isEmpty(qd.getOrders())) {
      List<QueryOrder> orders = new ArrayList<>();
      orders.add(new QueryOrder(PriceGradeAdjustment.Queries.CREATED, QueryOrderDirection.desc));
      qd.setOrders(orders);
    }
    QueryResult<PriceGradeAdjustment> result = adjustmentDao.query(tenant, qd);
    fetch(tenant, result.getRecords(), fetchParts);
    return result;
  }

  private void fetch(String tenant, Collection<PriceGradeAdjustment> adjustments, String[] fetchParts) {
    if (CollectionUtils.isEmpty(adjustments)) {
      return;
    }
    if (fetchParts == null || fetchParts.length == 0) {
      return;
    }
    if (ArrayUtils.contains(fetchParts, PriceGradeAdjustment.FETCH_SHOP)) {
      List<String> uuids = adjustments.stream().map(PriceGradeAdjustment::getUuid).collect(Collectors.toList());
      Map<String, PriceGradeAdjustment> adjustmentMap = adjustments.stream()
              .collect(Collectors.toMap(p -> p.getUuid(), p -> p));
      List<PriceGradeAdjustmentShop> shops = adjustmentShopDao.listByOwners(tenant, uuids);
      shops.stream().forEach(s -> {
        PriceGradeAdjustment p = adjustmentMap.get(s.getOwner());
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

  @Override
  public QueryResult<PriceGradeAdjustmentLine> queryLine(String tenant, String uuid, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(qd, "qd");
    return adjustmentLineDao.query(tenant, uuid, qd);
  }

  @Override
  @Tx
  public void relateShops(String tenant, String uuid, List<String> shopIds) throws BaasException {
    Assert.notNull(tenant);
    Assert.notNull(uuid);
    Assert.notNull(shopIds);
    if (CollectionUtils.isEmpty(shopIds)) {
      // 传空，清理
      adjustmentShopDao.deleteByOwner(tenant, uuid);
      return;
    }

    PriceGradeAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    checkEdit(adjustment);

    // 查询门店是否存在未来生效的单据
    checkShopRelate(tenant, adjustment.getOrgId(), shopIds);

    StoreFilter filter = new StoreFilter();
    filter.setIdIn(shopIds);
    QueryResult<Store> storeResult = storeService.query(tenant, filter);
    Map<String, Store> storeMap = storeResult.getRecords().stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
    List<PriceGradeAdjustmentShop> adjustmentShops = new ArrayList<>();
    for (String shopId : shopIds) {
      PriceGradeAdjustmentShop adjustmentShop = new PriceGradeAdjustmentShop();
      adjustmentShop.setTenant(tenant);
      adjustmentShop.setOwner(uuid);
      Store store = storeMap.get(shopId);
      if (store == null) {
        throw new BaasException("门店<{0}>不存在", shopId);
      }
      adjustmentShop.setShop(shopId);
      adjustmentShop.setShopCode(store.getCode());
      adjustmentShop.setShopName(store.getName());
      adjustmentShops.add(adjustmentShop);
    }
    adjustmentShopDao.deleteByOwner(tenant, uuid);
    adjustmentShopDao.batchInsert(tenant, uuid, adjustmentShops);
  }

  // 查询门店是否存在待审核/待生效的调整单
  private void checkShopRelate(String tenant, String orgId, List<String> shopIds) throws BaasException {
    // 查询门店是否存在未来生效的单据
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceGradeAdjustment.Queries.ORG_ID, Cop.EQUALS, orgId);
    qd.addByField(PriceGradeAdjustment.Queries.STATE, Cop.IN, PriceGradeAdjustmentState.CONFIRMED.name(),
        PriceGradeAdjustmentState.AUDITED.name());
    qd.addByOperation(PriceGradeAdjustment.Queries.SHOP_IN, shopIds.toArray());
    qd.addByField(PriceGradeAdjustment.Queries.EFFECTIVE_START_DATE, Cop.AFTER, new Date());
    QueryResult<PriceGradeAdjustment> result = adjustmentDao.query(tenant, qd);
    if (result.getRecords().size() > 0) {
      throw new BaasException("门店存在待审核/待生效的调整单");
    }
  }

  @Override
  public List<PriceGradeAdjustmentShop> listShops(String tenant, String uuid) {
    Assert.notNull(tenant);
    Assert.notNull(uuid);
    return adjustmentShopDao.list(tenant, uuid);
  }

  private void checkEdit(PriceGradeAdjustment adjustment) throws BaasException {
    if (adjustment.getState() == PriceGradeAdjustmentState.INIT
        || adjustment.getState() == PriceGradeAdjustmentState.CONFIRMED) {
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
  private void publishShopPriceEvCall(String tenant,String orgId, Date executeDate) {
    ShopPriceTaskMsg msg = new ShopPriceTaskMsg();
    msg.setTenant(tenant);
    msg.setOrgId(orgId);
    msg.setExecuteDate(executeDate);
    publisher.publishForNormal(ShopPriceEvCallExecutor.SHOP_PRICE_CREATE_EXECUTOR_ID, msg);
  }

}
