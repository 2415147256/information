package com.hd123.baas.sop.service.impl.price.priceadjustment;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.config.BaasPriceSkuConfig;
import com.hd123.baas.sop.service.api.entity.PUnv;
import com.hd123.baas.sop.service.api.group.SkuGroupService;
import com.hd123.baas.sop.service.api.price.PriceAdjustmentLineEdit;
import com.hd123.baas.sop.service.api.price.SkuDefine;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfig;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfigService;
import com.hd123.baas.sop.service.api.price.priceadjustment.*;
import com.hd123.baas.sop.service.dao.price.priceadjustment.PriceAdjustmentDaoBof;
import com.hd123.baas.sop.service.dao.price.priceadjustment.PriceAdjustmentLineDaoBof;
import com.hd123.baas.sop.service.dao.price.priceadjustment.PriceCompetitorLineDaoBof;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.baas.sop.service.impl.price.PUnvToPriceIncreaseRate;
import com.hd123.baas.sop.service.impl.price.priceadjustment.calculate.PriceCalculateMgr;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.price.AdjustmentLineWeekQtyMsg;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceTaskMsg;
import com.hd123.baas.sop.utils.CommonsUtilsV2;
import com.hd123.baas.sop.utils.CommonUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.hd123.baas.sop.evcall.exector.price.AdjustmentLineWeekQtyEvCallExecutor.ADJUSTMENT_LINE_WEEK_QTY_EXECUTOR_ID;

/**
 * @author zhengzewang on 2020/11/11.
 */
@Service
@Slf4j
public class PriceAdjustmentServiceImpl implements PriceAdjustmentService {

  @Autowired
  private PriceAdjustmentDaoBof priceAdjustmentDao;
  @Autowired
  private PriceAdjustmentLineDaoBof priceAdjustmentLineDao;
  @Autowired
  private PriceCompetitorLineDaoBof priceCompetitorLineDao;
  @Autowired
  private PriceSkuConfigService priceSkuConfigService;
  @Autowired
  private BillNumberMgr billNumberMgr;
  @Autowired
  private PriceCalculateMgr priceCalculateMgr;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private SkuGroupService skuGroupService;

  @Override
  @Tx
  public PriceAdjustment create(String tenant, String orgId, OperateInfo operateInfo) throws BaasException {
    PriceAdjustment adjustment = new PriceAdjustment();
    adjustment.setUuid(UUID.randomUUID().toString());
    adjustment.setEffectiveStartDate(null); //
    adjustment.setFlowNo(null); // 操作转保存的时候再给值
    adjustment.setState(PriceAdjustmentState.INIT);
    adjustment.setTenant(tenant);
    adjustment.setOrgId(orgId);
    priceAdjustmentDao.insert(tenant, adjustment, operateInfo);

    int page = 0;
    SkuFilter skuFilter = new SkuFilter();
    skuFilter.setDeletedEq(false);
    skuFilter.setPageSize(1000);

    while (true) {
      skuFilter.setPage(page++);
      skuFilter.setOrgIdEq(orgId);
      QueryResult<PriceSkuConfig> skuResult = priceSkuConfigService.query(tenant, orgId, skuFilter);
      if (skuResult.getRecords().isEmpty()) {
        break;
      }
      List<PriceAdjustmentLine> lines = skuResult.getRecords().stream().map(sc -> {
        PriceAdjustmentLine line = new PriceAdjustmentLine();
        line.setSku(sc.getSku());
        line.setSkuGroup(sc.getSkuGroup());
        line.setSkuGroupName(sc.getSkuGroupName());
        line.setSkuPosition(sc.getSkuPosition());
        line.setSkuPositionName(sc.getSkuPositionName());
        line.setSkuDefine(sc.getSkuDefine());
        line.setRaw(sc.getRaw());
        // 补充是否计算尾差
        line.setCalcTailDiff(sc.getCalcTailDiff());

        line.setHighInPrice(sc.getHighInPrice());
        line.setLowInPrice(sc.getLowInPrice());
        line.setHighBackGrossRate(sc.getHighBackGrossRate());
        line.setLowBackGrossRate(sc.getLowBackGrossRate());
        line.setHighFrontGrossRate(sc.getHighFrontGrossRate());
        line.setLowFrontGrossRate(sc.getLowFrontGrossRate());
        line.setHighMarketDiffRate(sc.getHighMarketDiffRate());
        line.setLowMarketDiffRate(sc.getLowMarketDiffRate());
        line.setHighPriceFloatRate(sc.getHighPriceFloatRate());
        line.setLowPriceFloatRate(sc.getLowPriceFloatRate());

        line.setSkuKv(sc.getKv());
        line.setSkuBv(sc.getBv());
        line.setSkuIncreaseRate(sc.getIncreaseRate());

        line.setIncreaseType(sc.getIncreaseType());
        line.setIncreaseRules(sc.getIncreaseRules());
        line.setExt(sc.getExt());

        return line;
      }).collect(Collectors.toList());
      priceAdjustmentLineDao.batchInsert(tenant, adjustment.getUuid(), lines, operateInfo);
    }

    return adjustment;
  }

  @Override
  @Tx
  public void modifyInPrice(String tenant, String uuid, String lindId, BigDecimal inPrice, BigDecimal initInPrice,
      OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.hasText(lindId, "lindId");
    Assert.notNull(inPrice, "inPrice");
    long sts = System.currentTimeMillis();
    if (inPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new BaasException("目标采购价不能小于0");
    }
    if (initInPrice != null && initInPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new BaasException("初始采购价不能小于0");
    }
    PriceAdjustmentLine adjustmentLine = priceAdjustmentLineDao.get(tenant, uuid, lindId);
    PriceAdjustment adjustment = priceAdjustmentDao.get(tenant, adjustmentLine.getOwner());
    List<PriceAdjustmentLine> modifiedLines = changeInPrice(tenant, adjustment.getOrgId(), adjustmentLine, inPrice);
    if (initInPrice != null) {
      adjustmentLine.setSkuInitInPrice(initInPrice);
    }
    priceAdjustmentLineDao.batchUpdate(tenant, uuid, modifiedLines);
    CommonsUtilsV2.outTs(uuid, "编辑目标采购价，行ID=" + lindId, sts);
  }

  @Override
  public void modifyBasePrice(String tenant, String uuid, String lindId, BigDecimal basePrice, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.hasText(lindId, "lindId");
    Assert.notNull(basePrice, "basePrice");
    PriceAdjustmentLine adjustmentLine = priceAdjustmentLineDao.get(tenant, uuid, lindId);
    if (adjustmentLine == null) {
      throw new BaasException("对应的信息不存在");
    }
    changeBasePrice(adjustmentLine, basePrice);
    priceAdjustmentLineDao.update(tenant, uuid, adjustmentLine, operateInfo);
  }

  @Override
  public void modifyLinePrice(String tenant, String uuid, String lindId, PriceAdjustmentLineEdit edit,
      OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.hasText(lindId, "lindId");
    Assert.notNull(edit, "edit");
    Assert.notNull(edit.getInPrice(), "edit.inPrice");

    PriceAdjustmentLine adjustmentLine = priceAdjustmentLineDao.get(tenant, uuid, lindId);
    if (adjustmentLine == null) {
      throw new BaasException("对应的信息不存在");
    }
    PriceAdjustment adjustment = priceAdjustmentDao.get(tenant, adjustmentLine.getOwner());
    changeIncreaseRate(adjustmentLine, edit.getIncreaseRate());
    List<PriceAdjustmentLine> modifiedLines = changeInPrice(tenant, adjustment.getOrgId(), adjustmentLine,
        edit.getInPrice());
    changeBasePrice(adjustmentLine, edit.getBasePrice());
    adjustmentLine.setRemark(edit.getRemark());

    if (edit.getFirstPriceGrade() != null && edit.getSecondPriceGrade() != null) {
      List<PUnv> pUnvs = skuGroupService.computeGradeList(tenant, edit.getFirstPriceGrade(),
          edit.getSecondPriceGrade());
      List<PriceIncreaseRate> old = adjustmentLine.getPriceRangeIncreaseRates();
      adjustmentLine.setPriceRangeIncreaseRates(ConverterUtil.convert(pUnvs, new PUnvToPriceIncreaseRate()));
      adjustmentLine.setPrePriceRangeIncreaseRates(old);
    }
    // 计算试算单行价格
    computeSalePrice(tenant, uuid, false, operateInfo, modifiedLines);

    priceAdjustmentLineDao.batchUpdate(tenant, uuid, modifiedLines);
  }

  @Tx
  @Override
  public void update(String tenant, String owner, PriceAdjustmentLine line, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(line, "line");
    Assert.hasText(line.getUuid(), "line.uuid");
    Assert.notNull(operateInfo, "operateInfo");

    priceAdjustmentLineDao.update(tenant, owner, line, operateInfo);
  }

  @Override
  @Tx
  public void stepOne(String tenant, PriceAdjustment adjustment, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(adjustment, "adjustment");
    Assert.hasText(adjustment.getUuid(), "adjustment.uuid");
    Assert.notNull(adjustment.getEffectiveStartDate(), "adjustment.effectiveStartDate");

    PriceAdjustment ever = this.get(tenant, adjustment.getUuid());
    if (ever == null) {
      throw new BaasException("单据不存在");
    }
    if (ever.getState() != PriceAdjustmentState.INIT && ever.getState() != PriceAdjustmentState.CONFIRMED) {
      throw new BaasException("当前状态不允许编辑");
    }

    // 只要日期
    adjustment.setEffectiveStartDate(DateUtils.truncate(adjustment.getEffectiveStartDate(), Calendar.DATE));
    ever.setEffectiveStartDate(adjustment.getEffectiveStartDate());
    stepOneCheck(tenant, ever);
    priceAdjustmentDao.update(tenant, ever, operateInfo);
    // setDefaultPriceIncreaseRule(tenant, ever.getUuid());
  }

  private void stepOneCheck(String tenant, PriceAdjustment ever) throws BaasException {
    if (ever.getEffectiveStartDate()
        .before(DateUtils.truncate(DateUtils.addDays(new Date(), PriceAdjustment.MIN_EFFECTIVE_DAYS), Calendar.DATE))) {
      throw new BaasException("生效时间不正确。");
    }

    List<PriceAdjustmentLine> lines = priceAdjustmentLineDao.list(tenant, ever.getUuid());
    if (CollectionUtils.isEmpty(lines)) {
      throw new BaasException("行数据不能为空.");
    }
    BaasPriceSkuConfig baasPriceSkuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
    for (PriceAdjustmentLine item : lines) {
      // 如果配置：商品定价规格范围，并且大于0，仅校验当前值
      if (baasPriceSkuConfig != null //
          && baasPriceSkuConfig.getSkuQpcOfPrice() != null //
          && baasPriceSkuConfig.getSkuQpcOfPrice() > 0 //
          && item.getSku().getQpc().compareTo(new BigDecimal(baasPriceSkuConfig.getSkuQpcOfPrice())) != 0) {
        continue;
      }
      // 到店价
      if (item.getSkuBasePrice() == null || item.getSkuBasePrice().compareTo(BigDecimal.ZERO) == 0) {
        throw new BaasException("商品<{0}>到店价为空", item.getSku().getName());
      }
      // 自定义类别
      if (StringUtils.isEmpty(item.getSkuGroup())) {
        throw new BaasException("商品<{0}>类别为空", item.getSku().getName());
      }
      // 商品定位
      if (StringUtils.isEmpty(item.getSkuPosition())) {
        throw new BaasException("商品<{0}>商品定位为空", item.getSku().getName());
      }
    }
  }

  @Override
  public void batchModifyRule(String tenant, String uuid, Collection<String> lineIds, PriceIncreaseType increaseType,
      List<PriceIncreaseRule> increaseRules, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    if (CollectionUtils.isEmpty(lineIds)) {
      return;
    }
    List<PriceAdjustmentLine> lines = priceAdjustmentLineDao.list(tenant, uuid, lineIds);
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    lines.forEach(line -> {
      line.setIncreaseType(increaseType);
      line.setIncreaseRules(increaseRules);
    });
    //更新尾差
    BaasPriceSkuConfig baasPriceSkuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
    if (baasPriceSkuConfig.getSkuQpcOfPrice() > 0) {
      // 更新尾差值
      Map<String, PriceAdjustmentLine> calcDiffMap = lines.stream()
          .filter(i -> i.getSku().getQpc().compareTo(new BigDecimal(baasPriceSkuConfig.getSkuQpcOfPrice())) == 0)
          .collect(Collectors.toMap(i -> i.getSku().getGoodsGid(), i -> i));
      for (PriceAdjustmentLine line : lines) {
        String goodsGid = line.getSku().getGoodsGid();
        if (calcDiffMap.containsKey(goodsGid)) {
          PriceAdjustmentLine priceLine = calcDiffMap.get(goodsGid);
          if (PriceIncreaseType.FIX.equals(priceLine.getIncreaseType())) {
            line.setCalcTailDiff(false);
          } else {
            line.setCalcTailDiff(priceLine.getCalcTailDiff());
          }
        }
      }
    }
    priceAdjustmentLineDao.batchUpdate(tenant, uuid, lines);
  }

  /**
   * 批量更新试算单
   */
  @Override
  @Tx
  public void batchModify(String tenant, String uuid, List<PriceAdjustmentLine> lines, OperateInfo operateInfo)
      throws BaasException {

    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    priceAdjustmentLineDao.batchUpdate(tenant, uuid, lines);
  }

  @Override
  @Tx
  public void calSalePrice(String tenant, String uuid, Boolean reload, OperateInfo operateInfo) throws BaasException {

    List<PriceAdjustmentLine> lines = priceAdjustmentLineDao.list(tenant, uuid);
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    computeSalePrice(tenant, uuid, reload, operateInfo, lines);

    priceAdjustmentLineDao.batchUpdate(tenant, uuid, lines);
  }

  /** 计算各个价格级售价，不持久化 */
  private void computeSalePrice(String tenant, String uuid, Boolean reload, OperateInfo operateInfo,
      List<PriceAdjustmentLine> lines)
      throws BaasException {
    PriceAdjustment priceAdjustment = priceAdjustmentDao.get(tenant, uuid);
    priceCalculateMgr.begin(tenant, priceAdjustment.getOrgId(), operateInfo);
    BaasPriceSkuConfig baasPriceSkuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
    Integer qpcOfPrice = 0;
    if (baasPriceSkuConfig != null && baasPriceSkuConfig.getSkuQpcOfPrice() != null
        && baasPriceSkuConfig.getSkuQpcOfPrice() != 0) {
      qpcOfPrice = baasPriceSkuConfig.getSkuQpcOfPrice();
    }

    if (qpcOfPrice > 0) {
      // 更新尾差值
      Map<String, PriceAdjustmentLine> calcDiffMap = lines.stream()
          .filter(i -> i.getSku().getQpc().compareTo(new BigDecimal(baasPriceSkuConfig.getSkuQpcOfPrice())) == 0)
          .collect(Collectors.toMap(i -> i.getSku().getGoodsGid(), i -> i));
      for (PriceAdjustmentLine line : lines) {
        String goodsGid = line.getSku().getGoodsGid();
        if (calcDiffMap.containsKey(goodsGid)) {
          PriceAdjustmentLine priceLine = calcDiffMap.get(goodsGid);
          if (PriceIncreaseType.FIX.equals(priceLine.getIncreaseType())) {
            line.setCalcTailDiff(false);
          } else {
            line.setCalcTailDiff(priceLine.getCalcTailDiff());
          }
        }
      }
    }

    // 缓存已定价的商品，key =gid
    Map<String, PriceAdjustmentLine> hasSalePriceMap = new HashMap<>();
    for (PriceAdjustmentLine line : lines) {
      priceCalculateMgr.calculate(tenant, priceAdjustment.getOrgId(), line, reload);
      // 定价的规格
      BigDecimal qpc = line.getSku().getQpc();
      if (CollectionUtils.isNotEmpty(line.getPriceGrades()) && qpc.compareTo(new BigDecimal(qpcOfPrice)) == 0) {
        hasSalePriceMap.put(line.getSku().getGoodsGid(), line);
      }
    }

    // 刷新价格级，设置了不等于0
    if (qpcOfPrice != 0) {
      for (PriceAdjustmentLine line : lines) {
        BigDecimal qpc = line.getSku().getQpc();
        // 相等，忽略
        if (qpc.compareTo(new BigDecimal(qpcOfPrice)) == 0) {
          continue;
        }
        PriceAdjustmentLine hasSalePriceLine = hasSalePriceMap.get(line.getSku().getGoodsGid());
        if (hasSalePriceLine != null && hasSalePriceLine.getPriceGrades() != null) {
          // 设置值
          line.setPriceGrades(new ArrayList<>());
          // 循环
          for (PriceGradeSalePrice i : hasSalePriceLine.getPriceGrades()) {
            PriceGradeSalePrice item = new PriceGradeSalePrice();
            // 复制价格级
            PriceGrade grade = new PriceGrade();
            grade.setId(i.getGrade().getId());
            grade.setName(i.getGrade().getName());
            item.setGrade(grade);
            BigDecimal price = i.getPrice()
                .multiply(qpc)
                .divide(new BigDecimal(qpcOfPrice), 2, BigDecimal.ROUND_HALF_UP);
            item.setPrice(price);
            // 添加
            line.getPriceGrades().add(item);
          }
          ;
        }
        priceCalculateMgr.calcTailDiff(tenant, line, line.getPriceGrades());
      }
    }
    priceCalculateMgr.end();
  }

  @Override
  @Tx
  public void stepTwo(String tenant, String uuid, OperateInfo operateInfo)
      throws BaasException, IllegalAccessException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    PriceAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    if (adjustment.getState() != PriceAdjustmentState.INIT && adjustment.getState() != PriceAdjustmentState.CONFIRMED) {
      throw new BaasException("当前状态不允许编辑");
    }
    // 先计算，后检查，试算单行若加价率存在，则取行中的加价率进行计算，若不存在，取基础设置中的加价率进行计算
    calSalePrice(tenant, uuid, false, operateInfo);
    // 检查
    stepTwoCheck(tenant, adjustment);

    if (adjustment.getState() == PriceAdjustmentState.INIT) {
      adjustment.setState(PriceAdjustmentState.CONFIRMED);
      adjustment.setFlowNo(billNumberMgr.generatePriceAdjustmentFlowNo(tenant));
    }
    priceAdjustmentDao.update(tenant, adjustment, operateInfo);

    AdjustmentLineWeekQtyMsg msg = new AdjustmentLineWeekQtyMsg();
    msg.setOwner(uuid);
    msg.setTenant(tenant);
    publisher.publishForNormal(ADJUSTMENT_LINE_WEEK_QTY_EXECUTOR_ID, msg);
  }

  @Override
  public List<PriceAdjustmentLine> tryCal(String tenant, String lineId, TryCalSalePrice tryCalSalePrice)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(lineId, "lineId");
    Assert.notNull(tryCalSalePrice, "tryCalSalePrice");

    if (tryCalSalePrice.getInPrice() != null) {
      if (tryCalSalePrice.getInPrice().compareTo(BigDecimal.ZERO) < 0) {
        throw new BaasException("目标采购价不能小于0");
      }
    }

    PriceAdjustmentLine adjustmentLine = priceAdjustmentLineDao.get(tenant, lineId);
    if (adjustmentLine == null) {
      throw new BaasException("对应的信息不存在");
    }
    PriceAdjustment adjustment = priceAdjustmentDao.get(tenant, adjustmentLine.getOwner());
    List<PriceAdjustmentLine> affectedLines = new ArrayList<>();

    // 设置新的价格带加价率
    if (tryCalSalePrice.getFirstPriceGrade() != null && tryCalSalePrice.getSecondPriceGrade() != null) {
      List<PUnv> pUnvs = skuGroupService.computeGradeList(tenant, tryCalSalePrice.getFirstPriceGrade(),
          tryCalSalePrice.getSecondPriceGrade());
      adjustmentLine.setPriceRangeIncreaseRates(ConverterUtil.convert(pUnvs, new PUnvToPriceIncreaseRate()));
    }

    boolean changedRate = false;
    if (tryCalSalePrice.getIncreaseRate() != null) {
      changeIncreaseRate(adjustmentLine, tryCalSalePrice.getIncreaseRate());
      changedRate = true;
    }

    if (tryCalSalePrice.getInPrice() != null) {
      List<PriceAdjustmentLine> modifiedLines = changeInPrice(tenant, adjustment.getOrgId(), adjustmentLine,
          tryCalSalePrice.getInPrice());
      affectedLines.addAll(modifiedLines);
    } else {
      // 仅修改了后台加价率，要重新计算商品的到店价
      if (changedRate && adjustmentLine.getSkuInPrice() != null) {
        List<PriceAdjustmentLine> modifiedLines = changeInPrice(tenant, adjustment.getOrgId(), adjustmentLine,
            adjustmentLine.getSkuInPrice());
        affectedLines.addAll(modifiedLines);
      }

    }
    if (affectedLines.isEmpty()) {
      affectedLines.add(adjustmentLine);
    }

    computeSalePrice(tenant, adjustmentLine.getOwner(), false, null, affectedLines);

    return affectedLines;
  }

  private void stepTwoCheck(String tenant, PriceAdjustment adjustment) throws BaasException {
    List<PriceAdjustmentLine> lines = priceAdjustmentLineDao.list(tenant, adjustment.getUuid());
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    for (PriceAdjustmentLine line : lines) {
      BaasPriceSkuConfig baasPriceSkuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
      // qpc > 0
      if (baasPriceSkuConfig != null && baasPriceSkuConfig.getSkuQpcOfPrice() == 1
          && line.getSku().getQpc().compareTo(BigDecimal.ONE) != 0) {
        continue;
      }
      if (CollectionUtils.isEmpty(line.getPriceGrades())) {
        if (line.getIncreaseType() == null || CollectionUtils.isEmpty(line.getIncreaseRules())) {
          throw new BaasException("商品<{0}>加价规则为空", line.getSku().getName());
        }
        if (StringUtils.isEmpty(line.getSkuPosition())) {
          throw new BaasException("商品<{0}>定位为空", line.getSku().getName());
        }
        if (StringUtils.isEmpty(line.getSkuPosition())) {
          throw new BaasException("商品<{0}>定位为空", line.getSku().getName());
        }
      }
    }
  }

  @Override
  public QueryResult<PriceAdjustment> query(String tenant, QueryDefinition qd, String... fetchParts) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    QueryResult<PriceAdjustment> result = priceAdjustmentDao.query(tenant, qd);
    fetchParts(tenant, result.getRecords(), fetchParts);
    return result;
  }

  private void fetchParts(String tenant, List<PriceAdjustment> records, String[] fetchParts) {
    if (records.isEmpty() || fetchParts == null) {
      return;
    }
    for (String fetchPart : fetchParts) {
      if (PriceAdjustmentService.ADJUSTMENT_LINE_PART.equals(fetchPart)) {
        Map<String, PriceAdjustment> map = records.stream().collect(Collectors.toMap(s -> s.getUuid(), s -> s));
        List<PriceAdjustmentLine> list = priceAdjustmentLineDao.list(tenant, map.keySet());
        if (CollectionUtils.isEmpty(list)) {
          break;
        }
        Map<String, List<PriceAdjustmentLine>> lineMap = list.stream()
            .collect(Collectors.groupingBy(PriceAdjustmentLine::getOwner));
        for (String s : map.keySet()) {
          List<PriceAdjustmentLine> priceAdjustmentLines = lineMap.get(s);
          map.get(s).setLines(priceAdjustmentLines);
        }
      }
    }
  }

  @Override
  public PriceAdjustment get(String tenant, String uuid, String... fetchParts) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    return priceAdjustmentDao.get(tenant, uuid);
  }

  @Override
  public QueryResult<PriceAdjustmentLine> queryLine(String tenant, String uuid, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(qd, "qd");
    QueryResult<PriceAdjustmentLine> result = priceAdjustmentLineDao.query(tenant, uuid, qd);
    return result;
  }

  @Override
  public PriceAdjustmentLine getLine(String tenant, String lineId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(lineId, "lineId");

    return priceAdjustmentLineDao.getLine(tenant, lineId);
  }

  @Override
  public List<PriceAdjustmentLine> listBySkuGid(String tenant, String uuid, String skuGid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    return priceAdjustmentLineDao.listByGid(tenant, uuid, skuGid);
  }

  @Override
  public List<PriceCompetitorLine> competitorLineList(String tenant, String owner) {
    return priceCompetitorLineDao.list(tenant, owner);
  }

  @Tx
  @Override
  public void changeCompetitorIgnore(String tenant, String owner, Map<String, Boolean> skuIgnoreMap) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(skuIgnoreMap, "skuIgnoreMap");

    if (skuIgnoreMap.isEmpty()) {
      return;
    }
    List<String> ignoreTrue = new ArrayList<>();
    List<String> ignoreFalse = new ArrayList<>();

    for (Map.Entry<String, Boolean> entry : skuIgnoreMap.entrySet()) {
      String skuId = entry.getKey();
      Boolean value = entry.getValue();
      if (value != null) {
        if (Boolean.TRUE.equals(value)) {
          ignoreTrue.add(skuId);
        } else {
          ignoreFalse.add(skuId);
        }
      }
    }

    priceCompetitorLineDao.changeIgnore(tenant, owner, ignoreTrue, true);
    priceCompetitorLineDao.changeIgnore(tenant, owner, ignoreFalse, false);

  }

  @Override
  @Tx
  public void audit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    PriceAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment.getState() != PriceAdjustmentState.CONFIRMED) {
      throw new BaasException("当前状态不允许审核");
    }

    stepOneCheck(tenant, adjustment);
    stepTwoCheck(tenant, adjustment);
    // 检查单据是否能审核，标准是
    List<PriceAdjustment> list = priceAdjustmentDao.list(tenant, adjustment.getOrgId(), PriceAdjustmentState.AUDITED,
        adjustment.getEffectiveStartDate());
    if (CollectionUtils.isNotEmpty(list)) {
      throw new BaasException("存在待发布的单据，单号={0}", list.get(0).getFlowNo());
    }

    priceAdjustmentDao.changeState(tenant, uuid, PriceAdjustmentState.AUDITED, operateInfo);

    // 新增时间的校验
    Date endDate = DateUtils.addDays(new Date(), PriceAdjustment.MIN_EFFECTIVE_DAYS);
    endDate = DateUtils.truncate(endDate, Calendar.DATE);
    log.info("准备核对生效时间：EffectiveStartDate={}，endDate={}", adjustment.getEffectiveStartDate(), endDate);
    // 最后的时间是：明天的00：00：00
    if (adjustment.getEffectiveStartDate().getTime() == endDate.getTime()) {
      // 实时推送
      publishShopPriceEvCall(tenant, adjustment.getOrgId(), adjustment.getEffectiveStartDate());
    }
  }

  @Override
  public void cancel(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(reason, "取消原因");
    PriceAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    if (adjustment.getState() == PriceAdjustmentState.CANCELED) {
      log.info("单据已取消");
      return;
    }
    if (adjustment.getState() != PriceAdjustmentState.CONFIRMED
        && adjustment.getState() != PriceAdjustmentState.AUDITED) {
      throw new BaasException("当前状态不允许取消");
    }
    // 新增时间的校验
    Date endDate = DateUtils.addDays(new Date(), PriceAdjustment.MIN_EFFECTIVE_DAYS);
    endDate = DateUtils.truncate(endDate, Calendar.DATE);
    // 最后的时间是：后天的00：00：00
    if (adjustment.getEffectiveStartDate().before(endDate)) {
      throw new BaasException("当前生效日期不可取消。");
    }

    priceAdjustmentDao.cancel(tenant, uuid, reason, operateInfo);
  }

  @Override
  public void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    PriceAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    if (adjustment.getState() == PriceAdjustmentState.PUBLISHED) {
      log.info("单据已取消");
      return;
    }
    if (adjustment.getState() != PriceAdjustmentState.AUDITED) {
      throw new BaasException("当前状态不允许发布");
    }

    priceAdjustmentDao.changeState(tenant, uuid, PriceAdjustmentState.PUBLISHED, operateInfo);
  }

  @Override
  public void expire(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    PriceAdjustment adjustment = this.get(tenant, uuid);
    if (adjustment == null) {
      throw new BaasException("单据不存在");
    }
    if (adjustment.getState() == PriceAdjustmentState.EXPIRED) {
      log.info("单据已过期");
      return;
    }
    if (adjustment.getState() != PriceAdjustmentState.PUBLISHED) {
      throw new BaasException("当前状态不允许过期");
    }

    priceAdjustmentDao.changeState(tenant, uuid, PriceAdjustmentState.EXPIRED, operateInfo);
  }

  @Override
  public PriceAdjustment getLastEffective(String tenant, String orgId, Date effectiveStartDate, String... fetchParts) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(effectiveStartDate, "effectiveStartDate");
    PriceAdjustment lastEffective = priceAdjustmentDao.getLastEffective(tenant, orgId, effectiveStartDate);
    if (lastEffective == null) {
      return null;
    }
    for (String fetchPart : fetchParts) {
      if (PriceAdjustmentService.ADJUSTMENT_LINE_PART.equals(fetchPart)) {
        List<PriceAdjustmentLine> lines = priceAdjustmentLineDao.list(tenant, lastEffective.getUuid());
        lastEffective.setLines(lines);
      }
    }
    return lastEffective;
  }

  public PriceAdjustment getEffective(String tenant, String orgId, Date effectiveStartDate, String... fetchParts) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(effectiveStartDate, "effectiveStartDate");
    PriceAdjustment lastEffective = priceAdjustmentDao.getEffective(tenant, orgId, effectiveStartDate);
    if (lastEffective == null) {
      return null;
    }
    for (String fetchPart : fetchParts) {
      if (PriceAdjustmentService.ADJUSTMENT_LINE_PART.equals(fetchPart)) {
        List<PriceAdjustmentLine> lines = priceAdjustmentLineDao.list(tenant, lastEffective.getUuid());
        lastEffective.setLines(lines);
      }
    }
    return lastEffective;
  }

  /**
   * 修改采购价，返回所有修改过的行，包括line本身
   */
  private List<PriceAdjustmentLine> changeInPrice(String tenant, String orgId, PriceAdjustmentLine line,
      BigDecimal inPrice)
      throws BaasException {
    ChangeInPrice changeInPrice = new ChangeInPrice();
    AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
    autowireCapableBeanFactory.autowireBean(changeInPrice);
    return changeInPrice.change(tenant, orgId, line, inPrice);
  }

  /**
   * 修改到店价
   */
  private void changeBasePrice(PriceAdjustmentLine line, BigDecimal basePrice) {
    // TODO 容差值校验
    if (basePrice != null && line.getSkuDefine().equals(SkuDefine.SPLITBYPART_RAW)) {
      line.setSkuBasePrice(basePrice.setScale(2, RoundingMode.HALF_UP));
    }
  }

  /**
   * 修改后台加价率
   */
  private void changeIncreaseRate(PriceAdjustmentLine line, BigDecimal increaseRate) {
    BigDecimal oldSkuIncreaseRate = line.getSkuIncreaseRate();
    if (increaseRate != null && increaseRate.compareTo(BigDecimal.ZERO) > 0) {
      line.setSkuIncreaseRate(increaseRate.setScale(4, RoundingMode.HALF_UP));
    }
    if (!CommonUtils.valueEquals(oldSkuIncreaseRate, line.getSkuIncreaseRate())) {
      line.setPreSkuIncreaseRate(oldSkuIncreaseRate);
    }
  }

  /**
   * 门店商品价格计算
   *
   * @param tenant
   *     租户
   */
  private void publishShopPriceEvCall(String tenant, String orgId, Date executeDate) {
    ShopPriceTaskMsg msg = new ShopPriceTaskMsg();
    msg.setTenant(tenant);
    msg.setOrgId(orgId);
    msg.setExecuteDate(executeDate);
    log.info("准备实时推送消息：{}", JsonUtil.objectToJson(msg));
    publisher.publishForNormal(ShopPriceEvCallExecutor.SHOP_PRICE_CREATE_EXECUTOR_ID, msg);
  }

}
