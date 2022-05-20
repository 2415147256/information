package com.hd123.baas.sop.service.impl.price.config;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.basedata.category.CategoryFilter;
import com.hd123.baas.sop.service.api.basedata.category.CategoryService;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.basedata.sku.DSkuBom;
import com.hd123.baas.sop.service.dao.basedata.SkuBomDaoBof;
import com.hd123.baas.sop.config.BaasPriceSkuConfig;
import com.hd123.baas.sop.config.H6SkuBomDefineConfig;
import com.hd123.baas.sop.service.api.entity.PriceGrade;
import com.hd123.baas.sop.service.api.entity.SkuGroup;
import com.hd123.baas.sop.service.api.entity.SkuGroupCategoryAssoc;
import com.hd123.baas.sop.service.api.entity.SkuPosition;
import com.hd123.baas.sop.service.api.grade.PriceGradeService;
import com.hd123.baas.sop.service.api.group.SkuGroupCategory;
import com.hd123.baas.sop.service.api.group.SkuGroupService;
import com.hd123.baas.sop.service.api.position.SkuPositionService;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.SkuDefine;
import com.hd123.baas.sop.service.api.price.config.BaseConfigParam;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfig;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfigService;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseRule;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseType;
import com.hd123.baas.sop.service.dao.price.config.PriceSkuConfigDaoBof;
import com.hd123.baas.sop.common.OrgConstants;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasPageResponse;
import com.hd123.baas.sop.remote.rsmas.category.RsCategory;
import com.hd123.baas.sop.remote.rsmas.category.RsCategoryFilter;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.mpa.api.common.ObjectNodeUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Service
@Slf4j
public class PriceSkuConfigServiceImpl implements PriceSkuConfigService {

  @Autowired
  private PriceSkuConfigDaoBof priceSkuConfigDao;
  @Autowired
  private SkuPositionService skuPositionService;
  @Autowired
  private SkuGroupService skuGroupService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private SkuBomDaoBof skuBomDao;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private SkuService skuService;
  @Autowired
  private PriceGradeService priceGradeService;
  @Autowired
  private RsMasClient rsMasClient;

  @Override
  @Tx
  public void batchSave(String tenant, String orgId, Collection<PriceSkuConfig> configs, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.hasText(orgId, "orgId");
    List<String> skuIds = configs.stream().map(p -> p.getSku().getId()).collect(Collectors.toList());
    List<PriceSkuConfig> everList = priceSkuConfigDao.listBySkuIds(tenant, orgId, skuIds);
    List<PriceSkuConfig> inserts = new ArrayList<>();
    List<PriceSkuConfig> updates = new ArrayList<>();
    Map<String, PriceSkuConfig> skuIdConfig = everList.stream()
        .collect(Collectors.toMap(c -> c.getSku().getId(), c -> c));
    for (PriceSkuConfig c : configs) {
      PriceSkuConfig ever = skuIdConfig.get(c.getSku().getId());
      if (ever == null) {
        // 新增设置默认值
        BigDecimal groupToleranceValue = null;
        if (c.getToleranceValue() == null) {
          List<SkuGroupCategory> skuGroupCategories = skuGroupService.listByCategoryId(tenant,
              c.getSku().getCategory().getId());
          SkuGroupCategory skuGroupCategory = skuGroupCategories.stream()
              .filter(s -> s.getCategory().getId().equals(c.getSku().getCategory().getId()))
              .findFirst()
              .orElse(null);
          groupToleranceValue = skuGroupCategory != null ? skuGroupCategory.getSkuGroup().getToleranceValue() : null;
        }
        c.setToleranceValue(c.getToleranceValue() == null ? groupToleranceValue : c.getToleranceValue());
        c.setBv(c.getBv() == null ? BigDecimal.ZERO : c.getBv());
        c.setKv(c.getKv() == null ? BigDecimal.ONE : c.getKv());
        c.setIncreaseRate(c.getIncreaseRate() == null ? BigDecimal.ONE : c.getIncreaseRate());
        c.setCalcTailDiff(c.getCalcTailDiff() == null ? true : c.getCalcTailDiff());
        c.setSkuPosition(c.getSkuPosition());
        c.setHighInPrice(c.getHighInPrice() == null ? BigDecimal.ZERO : c.getHighInPrice());
        c.setLowInPrice(c.getLowInPrice() == null ? BigDecimal.ZERO : c.getLowInPrice());
        c.setHighBackGrossRate(c.getHighBackGrossRate() == null ? BigDecimal.ZERO : c.getHighBackGrossRate());
        c.setLowBackGrossRate(c.getLowBackGrossRate() == null ? BigDecimal.ZERO : c.getLowBackGrossRate());
        c.setHighFrontGrossRate(c.getHighFrontGrossRate() == null ? BigDecimal.ZERO : c.getHighFrontGrossRate());
        c.setLowFrontGrossRate(c.getLowFrontGrossRate() == null ? BigDecimal.ZERO : c.getLowFrontGrossRate());
        c.setHighMarketDiffRate(c.getHighMarketDiffRate() == null ? BigDecimal.ZERO : c.getHighMarketDiffRate());
        c.setLowMarketDiffRate(c.getLowMarketDiffRate() == null ? BigDecimal.ZERO : c.getLowMarketDiffRate());
        c.setHighPriceFloatRate(c.getHighPriceFloatRate() == null ? BigDecimal.ZERO : c.getHighPriceFloatRate());
        c.setLowPriceFloatRate(c.getLowPriceFloatRate() == null ? BigDecimal.ZERO : c.getLowPriceFloatRate());
        inserts.add(c);
      } else {
        // 空值不更新
        ever.setToleranceValue(c.getToleranceValue() != null ? c.getToleranceValue() : ever.getToleranceValue());
        ever.setKv(c.getKv() != null ? c.getKv() : ever.getKv());
        ever.setBv(c.getBv() != null ? c.getBv() : ever.getBv());
        ever.setIncreaseRate(c.getIncreaseRate() != null ? c.getIncreaseRate() : ever.getIncreaseRate());
        ever.setCalcTailDiff(c.getCalcTailDiff() != null ? c.getCalcTailDiff() : ever.getCalcTailDiff());
        ever.setSkuPosition(c.getSkuPosition() != null ? c.getSkuPosition() : ever.getSkuPosition());
        ever.setHighInPrice(c.getHighInPrice() != null ? c.getHighInPrice() : ever.getHighInPrice());
        ever.setLowInPrice(c.getLowInPrice() != null ? c.getLowInPrice() : ever.getLowInPrice());
        ever.setHighBackGrossRate(
            c.getHighBackGrossRate() != null ? c.getHighBackGrossRate() : ever.getHighBackGrossRate());
        ever.setLowBackGrossRate(
            c.getLowBackGrossRate() != null ? c.getLowBackGrossRate() : ever.getLowBackGrossRate());
        ever.setHighFrontGrossRate(
            c.getHighFrontGrossRate() != null ? c.getHighFrontGrossRate() : ever.getHighFrontGrossRate());
        ever.setLowFrontGrossRate(
            c.getLowFrontGrossRate() != null ? c.getLowFrontGrossRate() : ever.getLowFrontGrossRate());
        ever.setHighMarketDiffRate(
            c.getHighMarketDiffRate() != null ? c.getHighMarketDiffRate() : ever.getHighMarketDiffRate());
        ever.setLowMarketDiffRate(
            c.getLowMarketDiffRate() != null ? c.getLowMarketDiffRate() : ever.getLowMarketDiffRate());
        ever.setHighPriceFloatRate(
            c.getHighPriceFloatRate() != null ? c.getHighPriceFloatRate() : ever.getHighPriceFloatRate());
        ever.setLowPriceFloatRate(
            c.getLowPriceFloatRate() != null ? c.getLowPriceFloatRate() : ever.getLowPriceFloatRate());
        updates.add(ever);
      }
    }
    priceSkuConfigDao.batchInsert(tenant, inserts, operateInfo);
    priceSkuConfigDao.batchUpdate(tenant, updates, operateInfo);
  }

  @Override
  @Tx
  public void batchEditPosition(String tenant, String orgId, Collection<String> skuIds, String skuPosition,
      String skuPositionGradeId,
      OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.hasText(skuPosition, "skuPosition");
    Assert.notNull(operateInfo, "operateInfo");
    if (CollectionUtils.isEmpty(skuIds)) {
      return;
    }
    List<PriceSkuConfig> inserts = new ArrayList<>();
    List<PriceSkuConfig> updates = new ArrayList<>();

    List<PriceSku> skus = priceSkuConfigDao.getSkus(tenant, orgId, skuIds);
    List<PriceSkuConfig> skuConfigs = priceSkuConfigDao.listBySkuIds(tenant, orgId, skuIds);
    Map<String, PriceSkuConfig> skuIdMap = skuConfigs.stream()
        .collect(Collectors.toMap(c -> c.getSku().getId(), c -> c));
    PriceGrade priceGrade = null;
    if (StringUtils.isNotBlank(skuPositionGradeId)) {
      priceGrade = priceGradeService.get(tenant, Integer.parseInt(skuPositionGradeId));
    }

    for (PriceSku sku : skus) {
      PriceSkuConfig ever = skuIdMap.get(sku.getId());
      if (ever != null) {
        if (Objects.isNull(ever.getExt())) {
          ever.setExt(ObjectNodeUtil.createObjectNode());
        }
        ever.setSkuPosition(skuPosition);
        setPositionGradeId(ever, priceGrade);
        updates.add(ever);
      } else {
        PriceSkuConfig config = new PriceSkuConfig();
        config.setTenant(tenant);
        config.setOrgId(orgId);
        config.setSku(sku);
        config.setSkuPosition(skuPosition);
        setPositionGradeId(config, priceGrade);
        inserts.add(config);
      }
    }

    priceSkuConfigDao.batchInsert(tenant, inserts, operateInfo);
    priceSkuConfigDao.batchUpdate(tenant, updates, operateInfo);

  }

  private void setPositionGradeId(PriceSkuConfig config, PriceGrade priceGrade) {
    if (priceGrade != null) {
      config.getExt().put(PriceSkuConfig.Ext.POSITION_GRADE_ID, priceGrade.getUuid());
      config.getExt().put(PriceSkuConfig.Ext.POSITION_GRADE_NAME, priceGrade.getName());
    } else {
      if (config.getExt() != null) {
        config.getExt().remove(PriceSkuConfig.Ext.POSITION_GRADE_ID);
        config.getExt().remove(PriceSkuConfig.Ext.POSITION_GRADE_NAME);
      }
    }
  }

  @Override
  @Tx
  public void batchEditBaseParam(String tenant, String orgId, Collection<String> skuIds, BaseConfigParam param,
      OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(param, "param");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(operateInfo, "operateInfo");
    if (CollectionUtils.isEmpty(skuIds)) {
      return;
    }
    BigDecimal increaseRate = param.getIncreaseRate();
    if (increaseRate != null
        && (increaseRate.compareTo(BigDecimal.ONE) >= 0 || increaseRate.compareTo(new BigDecimal(-1)) < 0)) {
      throw new BaasException("后台加价率不能超出[-100%,100%)范围");
    }
    List<PriceSkuConfig> inserts = new ArrayList<>();
    List<PriceSkuConfig> updates = new ArrayList<>();

    List<PriceSku> skus = priceSkuConfigDao.getSkus(tenant, orgId, skuIds);
    List<PriceSkuConfig> skuConfigs = priceSkuConfigDao.listBySkuIds(tenant, orgId, skuIds);
    Map<String, PriceSkuConfig> skuIdMap = skuConfigs.stream()
        .collect(Collectors.toMap(c -> c.getSku().getId(), c -> c));

    for (PriceSku sku : skus) {
      PriceSkuConfig ever = skuIdMap.get(sku.getId());
      if (ever != null) {
        ever.setCalcTailDiff(param.getCalcTailDiff() != null ? param.getCalcTailDiff() : ever.getCalcTailDiff());
        ever.setIncreaseRate(param.getIncreaseRate() != null ? param.getIncreaseRate() : ever.getIncreaseRate());
        ever.setToleranceValue(
            param.getToleranceValue() != null ? param.getToleranceValue() : ever.getToleranceValue());
        ever.setBv(param.getBv() != null ? param.getBv() : ever.getBv());
        ever.setKv(param.getKv() != null ? param.getKv() : ever.getKv());
        ever.setTenant(tenant);
        updates.add(ever);
      } else {
        PriceSkuConfig config = new PriceSkuConfig();
        config.setSku(sku);
        config.setCalcTailDiff(param.getCalcTailDiff() == null ? false : param.getCalcTailDiff());
        config.setIncreaseRate(param.getIncreaseRate());
        config.setToleranceValue(param.getToleranceValue());
        config.setBv(param.getBv());
        config.setKv(param.getKv());
        inserts.add(config);
      }
    }

    priceSkuConfigDao.batchInsert(tenant, inserts, operateInfo);
    priceSkuConfigDao.batchUpdate(tenant, updates, operateInfo);
  }

  @Override
  public void save(String tenant, PriceSkuConfig config, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(config.getOrgId(), "orgId");
    Assert.notNull(config, "商品基本配置");
    Assert.notNull(config.getSku(), "sku");
    Assert.hasText(config.getSku().getId(), "sku.id");
    PriceSku sku = priceSkuConfigDao.getSku(tenant, config.getOrgId(), config.getSku().getId());
    if (sku == null) {
      throw new BaasException("商品不存在，请刷新重试");
    }
    if (config.getIncreaseRate() != null) {
      checkIncreaseRate(config.getIncreaseRate());
    }
    PriceSkuConfig ever = priceSkuConfigDao.getBySkuId(tenant, config.getOrgId(), config.getSku().getId());
    if (ever != null) {
      if (!StringUtil.isNullOrBlank(config.getSkuPosition())) {
        ever.setSkuPosition(config.getSkuPosition());
      }
      if (config.getCalcTailDiff() != null) {
        ever.setCalcTailDiff(config.getCalcTailDiff());
      }
      if (config.getIncreaseRate() != null) {
        ever.setIncreaseRate(config.getIncreaseRate());
      }
      if (config.getToleranceValue() != null) {
        ever.setToleranceValue(config.getToleranceValue());
      }
      if (config.getBv() != null) {
        ever.setBv(config.getBv());
      }
      if (config.getKv() != null) {
        ever.setKv(config.getKv());
      }
      if (config.getHighInPrice() != null) {
        ever.setHighInPrice(config.getHighInPrice());
      }
      if (config.getLowInPrice() != null) {
        ever.setLowInPrice(config.getLowInPrice());
      }
      if (config.getHighBackGrossRate() != null) {
        ever.setHighBackGrossRate(config.getHighBackGrossRate());
      }
      if (config.getLowBackGrossRate() != null) {
        ever.setLowBackGrossRate(config.getLowBackGrossRate());
      }
      if (config.getHighFrontGrossRate() != null) {
        ever.setHighFrontGrossRate(config.getHighFrontGrossRate());
      }
      if (config.getLowFrontGrossRate() != null) {
        ever.setLowFrontGrossRate(config.getLowFrontGrossRate());
      }
      if (config.getHighMarketDiffRate() != null) {
        ever.setHighMarketDiffRate(config.getHighMarketDiffRate());
      }
      if (config.getLowMarketDiffRate() != null) {
        ever.setLowMarketDiffRate(config.getLowMarketDiffRate());
      }
      if (config.getHighPriceFloatRate() != null) {
        ever.setHighPriceFloatRate(config.getHighPriceFloatRate());
      }
      if (config.getLowPriceFloatRate() != null) {
        ever.setLowPriceFloatRate(config.getLowPriceFloatRate());
      }
      if (config.getExt() != null) {
        ever.setExt(config.getExt());
      }
      ever.setTenant(tenant);
      priceSkuConfigDao.update(tenant, ever, operateInfo);
    } else {
      priceSkuConfigDao.insert(tenant, config, operateInfo);
    }
  }

  /**
   * 校验后台加价率范围[-1,1)
   */
  private static void checkIncreaseRate(BigDecimal increaseRate) throws BaasException {
    if (increaseRate == null) {
      throw new BaasException("后台加价率为空");
    }
    if (increaseRate.compareTo(BigDecimal.ONE) >= 0 || increaseRate.compareTo(new BigDecimal(-1)) < 0) {
      throw new BaasException("后台加价率不能超出[-100%,100%)范围");
    }
  }

  @Override
  public QueryResult<PriceSkuConfig> query(String tenant, SkuFilter skuFilter, String... fetchParts)
      throws BaasException {
    return query(tenant, null, skuFilter, fetchParts);
  }

  @Override
  public QueryResult<PriceSkuConfig> query(String tenant, String orgId, SkuFilter skuFilter, String... fetchParts)
      throws BaasException {
    long start = System.currentTimeMillis();
    // 查询商品
    QueryResult<Sku> skuQueryResult = getSkuQueryResult(tenant, orgId, skuFilter);
    // 转换
    QueryResult<PriceSkuConfig> result = new QueryResult<>();
    result.setRecords(skuQueryResult.getRecords().stream().map(s -> {
      PriceSkuConfig config = new PriceSkuConfig();
      PriceSku sku = convertToPriceSku(tenant, s);
      config.setSku(sku);
      return config;
    }).collect(Collectors.toList()));
    result.setPage(skuQueryResult.getPage());
    result.setRecordCount(skuQueryResult.getRecordCount());
    result.setMore(skuQueryResult.isMore());
    result.setPageCount(skuQueryResult.getPageCount());
    result.setPageSize(skuQueryResult.getPageSize());

    if (result.getRecords().isEmpty()) {
      return result;
    }
    long start2 = System.currentTimeMillis();
    // 获取bom
    QueryDefinition bomQd = new QueryDefinition();
    bomQd.addCondition2(DSkuBom.Queries.TENANT, Cop.EQUALS, tenant);
    QueryResult<DSkuBom> bomQueryResult = skuBomDao.query(bomQd);
    long end2 = System.currentTimeMillis();
    log.info("获取bom执行时间：{}ms", end2 - start2);

    // 读取配置文件
    H6SkuBomDefineConfig h6SkuBomDefineConfig = getH6SkuBomDefineConfig(tenant);
    Map<String, String> rawMap = h6SkuBomDefineConfig.getRawMap();

    Map<String, PriceSkuTemplateBom> templateBomMap = new HashMap<>();
    if (bomQueryResult != null && CollectionUtils.isNotEmpty(bomQueryResult.getRecords())) {
      List<DSkuBom> boms = bomQueryResult.getRecords();
      for (DSkuBom bom : boms) {
        PriceSkuBom priceSkuBom = JsonUtil.jsonToObject(bom.getBom(), PriceSkuBom.class);
        // 计算2个属性，商品类型，比例关系
        if (PriceSkuBom.PriceSkuBomType.splitByPart.name().equals(priceSkuBom.getType())) {
          // 处理分割类型
          // 如果是分割品，需要知道原料品id，一个分割品有多个原料id
          // 如果是原料品，需要知道分割品的关系，一个原料品
          // 先处理原料品
          if (CollectionUtils.isNotEmpty(priceSkuBom.getRaw())) {
            for (PriceSkuBom.BomRawLine rawLine : priceSkuBom.getRaw()) {
              PriceSkuTemplateBom templateBom = new PriceSkuTemplateBom();
              templateBom.setGdGid(rawLine.getGdGid());
              // 标识原料品
              templateBom.setSkuDefine(SkuDefine.SPLITBYPART_RAW);
              BigDecimal qty = rawLine.getQty();
              if (CollectionUtils.isNotEmpty(priceSkuBom.getFinish())) {
                templateBom.setFinish(new ArrayList<>());
                for (PriceSkuBom.BomFinishLine finishLine : priceSkuBom.getFinish()) {
                  // 原料品的属性
                  PriceSkuTemplateBom.PriceSkuTemplateBomLine tl = new PriceSkuTemplateBom.PriceSkuTemplateBomLine();
                  tl.setGdGid(finishLine.getGdGid());
                  // 保留6位
                  tl.setRate(finishLine.getQty().divide(qty, 6, BigDecimal.ROUND_HALF_UP));
                  templateBom.getFinish().add(tl);
                  // 产成品
                  PriceSkuTemplateBom templateBom2 = new PriceSkuTemplateBom();
                  templateBom2.setGdGid(finishLine.getGdGid());
                  // 标识原料品
                  templateBom2.setSkuDefine(SkuDefine.SPLITBYPART_FINISH);
                  if (rawMap.containsKey(finishLine.getGdGid())) {
                    templateBom2.setRaw(rawMap.get(finishLine.getGdGid()));
                    // 产出品
                    templateBomMap.put(finishLine.getGdGid(), templateBom2);
                  }
                }
              }
              // 原料品
              templateBomMap.put(rawLine.getGdGid(), templateBom);
            }
          }
        }
      }
    }
    // 更新值
    for (PriceSkuConfig config : result.getRecords()) {
      if (config.getSku().getQpc().compareTo(BigDecimal.ONE) == 0) {
        // 相同
        if (templateBomMap.containsKey(config.getSku().getGoodsGid())) {
          PriceSkuTemplateBom templateBom = templateBomMap.get(config.getSku().getGoodsGid());
          config.setSkuDefine(templateBom.getSkuDefine());
          config.setRaw(JsonUtil.objectToJson(templateBom));
        }
      }
    }
    long start3 = System.currentTimeMillis();
    // fetch config 商品配置信息
    List<PriceSkuConfig> origins = result.getRecords();
    List<String> skuIds = skuQueryResult.getRecords().stream().map(Sku::getId).collect(Collectors.toList());
    List<PriceSkuConfig> configs = priceSkuConfigDao.listBySkuIds(tenant, orgId, skuIds);
    Map<String, PriceSkuConfig> configMap = configs.stream().collect(Collectors.toMap(c -> c.getSku().getId(), c -> c));
    List<PriceSkuConfig> targets = new ArrayList<>();
    for (PriceSkuConfig config : origins) {
      PriceSkuConfig dbConfig = configMap.get(config.getSku().getId());
      if (dbConfig != null) {
        dbConfig.setSku(config.getSku());
        dbConfig.setSkuDefine(config.getSkuDefine());
        dbConfig.setRaw(config.getRaw());
        targets.add(dbConfig);
      } else {
        targets.add(config);
      }
    }
    long end3 = System.currentTimeMillis();
    log.info("fetch config执行时间：{}ms", end3 - start3);

    // fetch positionName
    long start4 = System.currentTimeMillis();
    List<SkuPosition> positionList = skuPositionService.list(tenant, orgId);
    if (CollectionUtils.isNotEmpty(positionList)) {
      Map<String, String> positionMap = positionList.stream()
          .collect(Collectors.toMap(s -> s.getUuid() + "", s -> s.getName()));
      targets.forEach(s -> {
        s.setSkuPositionName(positionMap.get(s.getSkuPosition()));
      });
    }
    long end4 = System.currentTimeMillis();
    log.info("fetch positionName执行时间：{}ms", end4 - start4);
    // fetch groupName category
    long start5 = System.currentTimeMillis();
    List<SkuGroup> skuGroups = skuGroupService.list(tenant, orgId);
    if (CollectionUtils.isNotEmpty(skuGroups)) {
      Map<Integer, SkuGroup> groupMap = skuGroups.stream().collect(Collectors.toMap(s -> s.getUuid(), s -> s));
      CategoryFilter categoryFilter = new CategoryFilter();
      categoryFilter.setPage(0);
      categoryFilter.setPageSize(Integer.MAX_VALUE);
      categoryFilter.setFetchParts(Category.PART_CHILDREN);
      // 分类
      List<Category> all = categoryService.query(tenant, categoryFilter).getRecords();
      // 价格组和分类关联关系 赋值
      List<SkuGroupCategoryAssoc> assocs = skuGroupService.queryAllAssoc(tenant, orgId);
      targets.stream().forEach(s -> {
        String categoryId = s.getSku() != null
            ? s.getSku().getCategory() != null ? s.getSku().getCategory().getId() : null
            : null;
        Integer groupId = queryGroupId(assocs, all, categoryId);
        Category category = all.stream().filter(c -> c.getId().equals(categoryId)).findFirst().orElse(null);
        s.setCategory(category);
        s.setSkuGroup(groupId == null ? null : groupId + "");
        SkuGroup group = groupMap.get(groupId);
        s.setSkuGroupName(group == null ? null : group.getName());
        s.setSkuGroupToleranceValue(group == null ? null : group.getToleranceValue());
      });
    }
    long end5 = System.currentTimeMillis();
    log.info("fetch groupName category执行时间：{}ms", end5 - start5);

    result.setRecords(targets);
    log.info("接口执行时间：{}ms", end5 - start);
    return result;
  }

  private QueryResult<Sku> getSkuQueryResult(String tenant, String orgId, SkuFilter skuFilter) throws BaasException {
    addFilterCondition(tenant, orgId, skuFilter);
    long sts = System.currentTimeMillis();
    skuFilter.setFetchParts(Sku.PART_CATEGORY);
    if (orgId != null) {
      skuFilter.setOrgIdEq(orgId);
    }
    log.info("商品查询请求参数：{}", JsonUtil.objectToJson(skuFilter));
    QueryResult<Sku> skuQueryResult = skuService.query(tenant, skuFilter);
    long ets = System.currentTimeMillis();
    log.info("查询sku表执行时间：{}ms", ets - sts);
    return skuQueryResult;
  }

  @Override
  public QueryResult<PriceSku> querySku(String tenant, QueryDefinition qd) {
    return priceSkuConfigDao.query(tenant, qd);
  }

  @Override
  public PriceSkuConfig getBySkuId(String tenant, String orgId, String skuId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(skuId, "skuId");
    return priceSkuConfigDao.getBySkuId(tenant, orgId, skuId);
  }

  @Override
  public QueryResult<PriceSkuConfig> querySkuConfig(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    return priceSkuConfigDao.querySkuConfig(tenant, qd);
  }

  @Override
  public void batchModifyRule(String tenant, String orgId, Collection<String> skuIds, PriceIncreaseType increaseType,
      List<PriceIncreaseRule> increaseRules, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(skuIds)) {
      return;
    }
    List<PriceSkuConfig> configs = priceSkuConfigDao.listBySkuIds(tenant, orgId, skuIds);
    if (CollectionUtils.isEmpty(configs)) {
      return;
    }
    configs.stream().forEach(line -> {
      line.setIncreaseType(increaseType);
      line.setIncreaseRules(increaseRules);
    });
    priceSkuConfigDao.batchUpdate(tenant, configs, operateInfo);
  }

  @Override
  public List<PriceSku> getPriceSkusByCategoryIds(String tenant, String orgId, List<String> categoryIds) throws BaasException {
    SkuFilter skuFilter = new SkuFilter();
    skuFilter.setCategoryIdIn(categoryIds);
    QueryResult<Sku> list = getSkuQueryResult(tenant, orgId, skuFilter);
    if (CollectionUtils.isEmpty(list.getRecords())) {
      return new ArrayList<>();
    }
    List<PriceSku> results = new ArrayList<>();
    list.getRecords().forEach(i -> {
      results.add(convertToPriceSku(tenant, i));
    });
    return results;
  }

  private Integer queryGroupId(List<SkuGroupCategoryAssoc> allAssoc, List<Category> allCategories, String categoryId) {
    if (CollectionUtils.isEmpty(allAssoc) || CollectionUtils.isEmpty(allCategories)) {
      return null;
    }
    // 关联关系
    List<Category> parent = queryAllParent(allCategories, categoryId);
    if (CollectionUtils.isEmpty(parent)) {
      return null;
    }
    Set<String> categoryIds = parent.stream().map(Category::getId).collect(Collectors.toSet());
    SkuGroupCategoryAssoc assoc = allAssoc.stream()
        .filter(s -> categoryIds.contains(s.getCategoryId()))
        .findFirst()
        .orElse(null);
    return assoc == null ? null : assoc.getSkuGroupId();
  }

  private List<Category> queryAllParent(List<Category> all, String categoryId) {
    if (CollectionUtils.isEmpty(all)) {
      return new ArrayList<>();
    }
    List<Category> parents = new ArrayList<>();
    findCategories(categoryId, all, parents);
    return parents;
  }

  private void findCategories(String categoryId, List<Category> all, List<Category> list) {
    if (all.isEmpty() || StringUtils.isEmpty(categoryId)) {
      return;
    }
    Category category = all.stream().filter(s -> s.getId().equals(categoryId)).findFirst().orElse(null);
    if (category == null) {
      return;
    }
    list.add(category);
    findCategories(category.getUpperId(), all, list);
  }

  private H6SkuBomDefineConfig getH6SkuBomDefineConfig(String tenant) {
    return configClient.getConfig(tenant, H6SkuBomDefineConfig.class);
  }

  private PriceSku convertToPriceSku(String tenant, Sku s) {
    PriceSku sku = new PriceSku();
    sku.setTenant(tenant);
    sku.setCategory(s.getCategory());
    sku.setId(s.getId());
    sku.setCode(s.getCode());
    sku.setGoodsGid(s.getGoodsGid());
    sku.setName(s.getName());
    sku.setQpc(s.getQpc());
    sku.setUnit(s.getUnit());
    sku.setDeleted(s.getDeleted() ? 1 : 0);
    sku.setGoodsType(s.getH6GoodsType());
    return sku;
  }

  private void addFilterCondition(String tenant, String orgId, SkuFilter skuFilter) {
    if (skuFilter == null) {
      return;
    }
    BaasPriceSkuConfig skuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
    List<String> excludeGoodsType = new ArrayList<>();
    if (StringUtils.isNotBlank(skuConfig.getExcludeGoodsTypes())) {
      String[] goodsTypes = skuConfig.getExcludeGoodsTypes().split(",");
      Collections.addAll(excludeGoodsType, goodsTypes);
    }
    // 按商品类型过滤
    if (excludeGoodsType.size() > 0) {
      skuFilter.setGoodsTypeNotIn(excludeGoodsType);
    }
    // 按商品分类过滤
    Set<String> excludeCategories = getExcludeCategories(tenant, orgId);
    if (CollectionUtils.isNotEmpty(excludeCategories)) {
      List<String> list = excludeCategories.stream().collect(Collectors.toList());
      skuFilter.setCategoryIdNotIn(list);
    }
  }

  private Set<String> getExcludeCategories(String tenant, String orgId) {
    Set<String> ret = new HashSet<>();
    BaasPriceSkuConfig skuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
    if (StringUtils.isNotBlank(skuConfig.getExcludeCategoryCodes())) {
      List<String> excludeCategoryList = new ArrayList<>();
      String[] categories = skuConfig.getExcludeCategoryCodes().split(",");
      CollectionUtils.addAll(excludeCategoryList, categories);
      RsCategoryFilter queryFilter = new RsCategoryFilter();
      queryFilter.setCodeIn(excludeCategoryList);
      queryFilter.setFetchParts(RsCategory.PART_CHILDREN);
      //    分类不到组织，默认为 “-”
      queryFilter.setOrgIdEq(OrgConstants.DEFAULT_MAS_ORG_ID);
      RsMasPageResponse<List<RsCategory>> listRsMasPageResponse = rsMasClient.categoryQuery(tenant, queryFilter);
      if (listRsMasPageResponse.isSuccess() && CollectionUtils.isNotEmpty(listRsMasPageResponse.getData())) {
        for (RsCategory category : listRsMasPageResponse.getData()) {
          getAllCategories(category, ret);
        }
      }
    }
    return ret;
  }

  private void getAllCategories(RsCategory category, Set<String> list) {
    if (category == null) {
      return;
    }
    list.add(category.getId());
    if (CollectionUtils.isNotEmpty(category.getChildren())) {
      for (RsCategory child : category.getChildren()) {
        getAllCategories(child, list);
      }
    }
  }
}
