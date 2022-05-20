package com.hd123.baas.sop.service.impl.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hd123.baas.sop.annotation.LogRequestPraras;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.basedata.category.CategoryFilter;
import com.hd123.baas.sop.service.api.basedata.category.CategoryService;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.entity.PUnv;
import com.hd123.baas.sop.service.api.entity.PriceGrade;
import com.hd123.baas.sop.service.api.entity.PriceRange;
import com.hd123.baas.sop.service.api.entity.PriceSkuCategory;
import com.hd123.baas.sop.service.api.entity.SkuGradeConfig;
import com.hd123.baas.sop.service.api.entity.SkuGroup;
import com.hd123.baas.sop.service.api.entity.SkuGroupCategoryAssoc;
import com.hd123.baas.sop.service.api.entity.SkuGroupPositionGradeConfig;
import com.hd123.baas.sop.service.api.entity.SkuGroupRangeGradeConfig;
import com.hd123.baas.sop.service.api.entity.SkuPosition;
import com.hd123.baas.sop.service.api.group.SkuGroupCategory;
import com.hd123.baas.sop.service.api.group.SkuGroupService;
import com.hd123.baas.sop.cache.CacheMap;
import com.hd123.baas.sop.service.dao.grade.PriceGradeDaoBof;
import com.hd123.baas.sop.service.dao.group.PSkuGroup;
import com.hd123.baas.sop.service.dao.group.SkuGroupCategoryAssocDaoBof;
import com.hd123.baas.sop.service.dao.group.SkuGroupDaoBof;
import com.hd123.baas.sop.service.dao.group.SkuGroupPositionGradeConfigDaoBof;
import com.hd123.baas.sop.service.dao.group.SkuGroupRangeGradeConfigDaoBof;
import com.hd123.baas.sop.service.dao.postion.SkuPositionDaoBof;
import com.hd123.baas.sop.service.dao.range.PriceRangeDaoBof;
import com.hd123.baas.sop.service.dao.skugrade.SkuGradeConfigDaoBof;
import com.hd123.baas.sop.utils.CommonUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SkuGroupServiceImpl implements SkuGroupService {
  @Autowired
  private SkuGroupDaoBof skuGroupDao;
  @Autowired
  private SkuGroupCategoryAssocDaoBof skuGroupCategoryAssocDao;
  @Autowired
  private SkuGroupRangeGradeConfigDaoBof skuGroupRangeGradeConfigDao;
  @Autowired
  private PriceRangeDaoBof priceRangeDao;
  @Autowired
  private SkuGroupPositionGradeConfigDaoBof skuGroupPositionGradeConfigDao;
  @Autowired
  private SkuPositionDaoBof skuPositionDao;
  @Autowired
  private SkuGradeConfigDaoBof skuGradeConfigDao;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private SkuService skuService;
  @Autowired
  private PriceGradeDaoBof priceGradeDao;
  @Autowired
  private CacheMap cacheMap;
  @Autowired
  private PriceGradeRateCalculateMgr priceGradeRateCalculateMgr;

  @Override
  @Tx
  @LogRequestPraras
  public void saveNew(String tenant, SkuGroup skuGroup) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuGroup);
    Assert.notNull(skuGroup.getName(), "名称");
    SkuGroup query = skuGroupDao.queryByName(tenant, skuGroup.getOrgId(), skuGroup.getName());
    if (query != null) {
      throw new BaasException("自定义类别已存在");
    }
    skuGroupDao.insert(tenant, skuGroup);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void saveModify(String tenant, SkuGroup skuGroup) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuGroup);
    Assert.notNull(skuGroup.getName(), "名称");
    Assert.notNull(skuGroup.getUuid(), "uuid");
    SkuGroup query = skuGroupDao.query(tenant, skuGroup.getUuid());
    if (query == null) {
      throw new BaasException("自定义类别不存在");
    }
    skuGroupDao.update(tenant, skuGroup);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void batchDelete(String tenant, List<Integer> skuGroupIds) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(skuGroupIds);
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuGroupCategoryAssoc.Queries.SKU_GROUP_ID, Cop.IN, skuGroupIds.toArray());
    QueryResult<SkuGroupCategoryAssoc> query = skuGroupCategoryAssocDao.query(tenant, qd);
    if (query.getRecordCount() > 0) {
      throw new BaasException("该商品定价类别已设置后台类别，请先删除");
    }
    skuGroupDao.delete(tenant, skuGroupIds);
    skuGroupCategoryAssocDao.batchDelete(tenant, skuGroupIds);
  }

  @Override
  @Tx
  public List<SkuGroup> list(String tenant, String orgId) {
    Assert.notNull(tenant, "租戶");
    return skuGroupDao.list(tenant, orgId);
  }

  @Override
  @Tx
  public List<SkuGroup> list(String tenant) {
    Assert.notNull(tenant, "租戶");
    return list(tenant, null);
  }

  @Override
  public QueryResult<SkuGroup> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "租戶");
    Assert.notNull(qd, "qd");
    return skuGroupDao.query(tenant, qd);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void assignCategories(String tenant, String orgId, Integer skuGroupId, List<UCN> categoryList)
      throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuGroupId, "skuGroupId");
    Assert.notEmpty(categoryList);
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PSkuGroup.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PSkuGroup.UUID, Cop.EQUALS, skuGroupId);
    QueryResult<SkuGroup> queryResult = skuGroupDao.query(tenant, qd);
    List<SkuGroup> records = queryResult.getRecords();
    if (CollectionUtils.isEmpty(records)) {
      throw new BaasException("自定义类别不存在");
    }
    List<String> categoryIds = categoryList.stream().map(i -> i.getUuid()).collect(Collectors.toList());
    Map<String, UCN> categoryMap = categoryList.stream().collect(Collectors.toMap(i -> i.getUuid(), i -> i));
    CategoryFilter filter = new CategoryFilter();
    filter.setIdIn(categoryIds);
    QueryResult<Category> query = categoryService.query(tenant, filter);
    List<Category> categories = query.getRecords();
    List<SkuGroupCategoryAssoc> list = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(categories)) {
      for (Category c : categories) {
        if (checkParentAssign(tenant, c.getId()) || checkSonAssign(tenant, c)) {
          continue;
        }
        SkuGroupCategoryAssoc assoc = new SkuGroupCategoryAssoc();
        assoc.setSkuGroupId(skuGroupId);
        assoc.setCategoryId(c.getId());
        assoc.setCategoryCode(c.getCode());
        assoc.setCategoryName(c.getName());
        if (categoryMap.containsKey(c.getId())) {
          UCN ucn = categoryMap.get(c.getId());
          if (ucn != null && StringUtils.isNotEmpty(ucn.getName())) {
            assoc.setCategoryName(ucn.getName());
          }
        }
        assoc.setTenant(tenant);
        assoc.setOrgId(orgId);
        list.add(assoc);
      }
    }
    skuGroupCategoryAssocDao.batchInsert(tenant, list);
  }

  @Override
  @Tx
  @LogRequestPraras
  public void removeCategories(String tenant, Integer skuGroupId, List<String> categoryIds) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuGroupId, "skuGroupId");
    Assert.notEmpty(categoryIds);

    skuGroupCategoryAssocDao.remove(tenant, skuGroupId, categoryIds);
  }

  @Override
//  @LogRequestPraras
  public List<PriceSkuCategory> categoryList(String tenant, String skuGroupId) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuGroupId, "skuGroupId");
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuGroupCategoryAssoc.Queries.SKU_GROUP_ID, Cop.EQUALS, skuGroupId);
    QueryResult<SkuGroupCategoryAssoc> query = skuGroupCategoryAssocDao.query(tenant, qd);
    List<SkuGroupCategoryAssoc> records = query.getRecords();
    List<PriceSkuCategory> priceSkuCategories = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(records)) {
      records.stream().forEach(record -> {
        PriceSkuCategory category = new PriceSkuCategory();
        category.setCode(record.getCategoryCode());
        category.setName(record.getCategoryName());
        category.setUuid(record.getCategoryId());
        priceSkuCategories.add(category);
      });
    }
    return priceSkuCategories;
  }

  @Override
//  @LogRequestPraras
  public List<PriceRange> gradeByAllRangeList(String tenant, String skuGroupId) {
    return gradeByAllRangeList(tenant, null, skuGroupId);
  }

  @Override
//  @LogRequestPraras
  public List<PriceRange> gradeByAllRangeList(String tenant, String orgId, String skuGroupId) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuGroupId, "skuGroupId");
    QueryDefinition qd = new QueryDefinition();
    if (StringUtils.isNotBlank(orgId)) {
      qd.addByField(SkuGroupRangeGradeConfig.Queries.ORG_ID, Cop.EQUALS, orgId);
    }
    qd.addByField(SkuGroupRangeGradeConfig.Queries.SKU_GROUP_ID, Cop.EQUALS, skuGroupId);
    QueryResult<SkuGroupRangeGradeConfig> result = skuGroupRangeGradeConfigDao.query(tenant, qd);
    List<SkuGroupRangeGradeConfig> records = result.getRecords();
    List<PriceRange> priceRanges = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(records)) {
      for (SkuGroupRangeGradeConfig config : records) {
        PriceRange range = priceRangeDao.queryByUuid(tenant, config.getPriceRangeId());
        if (range == null) {
          continue;
        }
        String priceGradeJson = config.getPriceGradeJson();
        if (StringUtils.isNoneBlank(priceGradeJson)) {
          List<PUnv> priceGrades = JSON.parseArray(priceGradeJson, PUnv.class);
          range.setPriceGrades(priceGrades);
        }
        priceRanges.add(range);
      }
      priceRanges = priceRanges.stream()
          .sorted(Comparator.comparingInt(o -> Integer.parseInt(o.getName())))
          .collect(Collectors.toList());
    }
    return priceRanges;
  }

  @Override
  @Tx
  @LogRequestPraras
  public void saveGradeListByAllRange(String tenant, String skuGroupId, List<PriceRange> priceRanges)
      throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuGroupId, "skuGroupId");
    if (CollectionUtils.isEmpty(priceRanges)) {
      return;
    }
    SkuGroup skuGroup = skuGroupDao.query(tenant, Integer.valueOf(skuGroupId));
    if (skuGroup == null) {
      throw new BaasException("自定义类别不存在");
    }
    List<Integer> rangeIds = priceRanges.stream().map(PriceRange::getUuid).collect(Collectors.toList());
    if (CollectionUtils.isEmpty(rangeIds)) {
      throw new BaasException("价格带UUID为空");
    }
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceRange.Queries.UUID, Cop.IN, rangeIds.toArray());
    List<PriceRange> ranges = priceRangeDao.query(tenant, qd).getRecords();
    if (CollectionUtils.isEmpty(ranges) || rangeIds.size() != ranges.size()) {
      throw new BaasException("价格带不存在");
    }
    List<SkuGroupRangeGradeConfig> newList = new ArrayList<>();
    priceRanges.forEach(sp -> {
      SkuGroupRangeGradeConfig config = new SkuGroupRangeGradeConfig();
      config.setTenant(tenant);
      config.setOrgId(sp.getOrgId());
      config.setSkuGroupId(Integer.valueOf(skuGroupId));
      config.setPriceRangeId(sp.getUuid());
      List<PUnv> priceGrades = new ArrayList<>();
      if (CollectionUtils.isNotEmpty(sp.getPriceGrades())) {
        priceGrades = sp.getPriceGrades();
      }
      config.setPriceGradeJson(JsonUtil.objectToJson(priceGrades));
      newList.add(config);
    });
    if (CollectionUtils.isNotEmpty(newList)) {
      skuGroupRangeGradeConfigDao.delete(tenant, skuGroupId);
      skuGroupRangeGradeConfigDao.batchInsert(tenant, newList);
    }
  }

  @Override
  @Tx
  public void batchGradeListByAllRange(String tenant, List<SkuGroupRangeGradeConfig> configs) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(configs);
    // key: groupId value:priceRangeId
    Map<Integer, Set<Integer>> map = new HashMap<>();
    Set<String> gradeNames = new HashSet<>();

    for (SkuGroupRangeGradeConfig config : configs) {
      Set<Integer> rangeIds = map.get(config.getSkuGroupId());
      if (map.get(config.getSkuGroupId()) == null) {
        rangeIds = new HashSet<>();
        map.put(config.getSkuGroupId(), rangeIds);
      }
      if (rangeIds.contains(config.getPriceRangeId())) {
        throw new BaasException("批量新增信息存在重复，groupIdL:{0},priceRangeId:{1}", config.getSkuGroupId(),
            config.getPriceRangeId());
      }
      rangeIds.add(config.getPriceRangeId());
      String priceGradeJson = config.getPriceGradeJson();
      if (StringUtils.isEmpty(priceGradeJson)) {
        throw new BaasException("批量新增信息有误，groupIdL:{0},priceRangeId:{1},priceGrades:{2}", config.getSkuGroupId(),
            config.getPriceRangeId(), config.getPriceGradeJson());
      }
      try {
        List<PUnv> grades = JsonUtil.jsonToList(priceGradeJson, PUnv.class);
        Set<String> punvNames = grades.stream().map(PUnv::getName).collect(Collectors.toSet());
        gradeNames.addAll(punvNames);
      } catch (Exception e) {
        throw new BaasException("导入信息有误，groupIdL:{0},priceRangeId:{1},priceGrades:{2}", config.getSkuGroupId(),
            config.getPriceRangeId(), config.getPriceGradeJson());
      }
    }
    // 校验价格级是否存在
    List<PriceGrade> priceGrades = priceGradeDao.query(tenant, configs.get(0).getOrgId(), gradeNames);
    if (priceGrades.isEmpty() || priceGrades.size() != gradeNames.size()) {
      throw new BaasException("批量新增价格级查询不存在");
    }
    skuGroupRangeGradeConfigDao.batchDelete(tenant, map.keySet());
    skuGroupRangeGradeConfigDao.batchInsert(tenant, configs);
  }

  @Override
  public void batchSaveGradeListByAllPosition(String tenant, List<SkuGroupPositionGradeConfig> configs)
      throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(configs);
    // key: groupId value:positionId
    Map<Integer, Set<Integer>> map = new HashMap<>();
    Set<String> gradeNames = new HashSet<>();
    for (SkuGroupPositionGradeConfig config : configs) {
      Set<Integer> positions = map.get(config.getSkuGroupId());
      if (map.get(config.getSkuGroupId()) == null) {
        positions = new HashSet<>();
        map.put(config.getSkuGroupId(), positions);
      }
      if (positions.contains(config.getPricePositionId())) {
        log.info("批量新增信息存在重复，groupIdL:{0},positionId:{1}", config.getSkuGroupId(), config.getPricePositionId());
        throw new BaasException("批量新增信息存在重复");
      }
      positions.add(config.getPricePositionId());
      String priceGradeJson = config.getPriceGradeJson();
      if (StringUtils.isEmpty(priceGradeJson)) {
        log.info("批量新增信息有误，groupIdL:{0},positionId:{1},priceGrades:{2}", config.getSkuGroupId(),
            config.getPricePositionId(), config.getPriceGradeJson());
        throw new BaasException("批量新增信息有误");
      }
      try {
        List<PUnv> grades = JsonUtil.jsonToList(priceGradeJson, PUnv.class);
        Set<String> punvNames = grades.stream().map(PUnv::getName).collect(Collectors.toSet());
        gradeNames.addAll(punvNames);
      } catch (Exception e) {
        log.info("导入信息有误，groupIdL:{0},positionId:{1},priceGrades:{2}", config.getSkuGroupId(),
            config.getPricePositionId(), config.getPriceGradeJson());
        throw new BaasException("导入信息有误");
      }
    }
    List<PriceGrade> priceGrades = priceGradeDao.query(tenant, configs.get(0).getOrgId(), gradeNames);
    if (priceGrades.isEmpty() || priceGrades.size() != gradeNames.size()) {
      throw new BaasException("批量新增价格级查询不存在");
    }
    skuGroupPositionGradeConfigDao.batchDelete(tenant, map.keySet());
    skuGroupPositionGradeConfigDao.batchInsert(tenant, configs);
  }

  @Override
//  @LogRequestPraras
  public List<SkuPosition> gradeByAllPositionList(String tenant, String skuGroupId) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuGroupId, "skuGroupId");
    List<SkuGroupPositionGradeConfig> skuGroupRangeGradeConfigs = skuGroupPositionGradeConfigDao
        .queryBySkuGroupId(tenant, skuGroupId);
    List<SkuPosition> skuPositions = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(skuGroupRangeGradeConfigs)) {
      skuGroupRangeGradeConfigs.stream().forEach(config -> {
        SkuPosition skuPosition = skuPositionDao.queryById(tenant, config.getPricePositionId());
        List<PUnv> priceGrades = JSON.parseArray(config.getPriceGradeJson(), PUnv.class);
        if (skuPosition != null) {
          skuPosition.setPriceGrades(priceGrades);
          skuPositions.add(skuPosition);
        }
      });
    }
    return skuPositions;
  }

  @Override
  public List<SkuGroupPositionGradeConfig> groupAllPositionList(String tenant, List<Integer> groupIds) {
    Assert.notNull(tenant, "租戶");
    return skuGroupPositionGradeConfigDao.queryBySkuGroupIds(tenant, groupIds);
  }

  @Override
  public List<SkuGroupRangeGradeConfig> groupAllRangeList(String tenant, List<Integer> groupIds) {
    Assert.notNull(tenant, "租戶");
    return skuGroupRangeGradeConfigDao.query(tenant, new QueryDefinition()).getRecords();
  }

  @Override
  @Tx
  @LogRequestPraras
  public void saveGradeListByAllPosition(String tenant, String skuGroupId, List<SkuPosition> skuPositions) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuGroupId, "skuGroupId");
    if (CollectionUtils.isEmpty(skuPositions)) {
      return;
    }
    skuGroupPositionGradeConfigDao.delete(tenant, skuGroupId);
    Integer skuGroupIdInt = Integer.valueOf(skuGroupId);
    List<SkuGroupPositionGradeConfig> newList = new ArrayList<>();
    skuPositions.forEach(sp -> {
      SkuGroupPositionGradeConfig config = new SkuGroupPositionGradeConfig();
      config.setTenant(tenant);
      config.setSkuGroupId(skuGroupIdInt);
      config.setPricePositionId(sp.getUuid());
      List<PUnv> priceGrades = new ArrayList<>();
      if (CollectionUtils.isNotEmpty(sp.getPriceGrades())) {
        priceGrades = sp.getPriceGrades();
      }
      config.setPriceGradeJson(JsonUtil.objectToJson(priceGrades));
      newList.add(config);
    });
    skuGroupPositionGradeConfigDao.batchInsert(tenant, newList);
  }

  @Override
  public List<SkuGroup> listBySkuIds(String tenant, List<String> skuIds) throws BaasException {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(skuIds, "skuIds");
    SkuFilter filter = new SkuFilter();
    filter.setIdIn(skuIds);
    QueryResult<Sku> query = skuService.query(tenant, filter);
    List<Sku> records = query.getRecords();
    if (CollectionUtils.isEmpty(records)) {
      return null;
    }
    List<String> categories = records.stream().map(Sku::getCategory).map(Category::getId).collect(Collectors.toList());
    List<SkuGroupCategoryAssoc> list = skuGroupCategoryAssocDao.queryByCategoryIds(tenant, categories);
    if (CollectionUtils.isEmpty(list)) {
      return null;
    }
    List<Integer> groupIds = list.stream().map(SkuGroupCategoryAssoc::getSkuGroupId).collect(Collectors.toList());
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuGroup.Queries.UUID, Cop.IN, groupIds);
    QueryResult<SkuGroup> skuGroups = skuGroupDao.query(tenant, qd);
    return skuGroups.getRecords();
  }

  @Override
  public List<SkuGroupCategoryAssoc> queryAllAssoc(String tenant, String orgId) {
    Assert.notNull(tenant, "租戶");

    return skuGroupCategoryAssocDao.queryAll(tenant, orgId);
  }

  @Override
  public SkuGroup get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    return skuGroupDao.get(tenant, uuid);
  }

  @Override
  public List<SkuGroupCategory> listByCategoryId(String tenant, String categoryId) throws BaasException {
    QueryDefinition assocQd = new QueryDefinition();
    List<Category> parentList = new ArrayList<>();
    queryParents(tenant, categoryId, parentList);
    assocQd.addByField(SkuGroupCategoryAssoc.Queries.CATEGORY_ID, Cop.IN, parentList);
    List<SkuGroupCategoryAssoc> assocs = skuGroupCategoryAssocDao.query(tenant, assocQd).getRecords();
    return listByAssoc(tenant, assocs);
  }

  @Override
  public List<SkuGroupCategory> listByGroupId(String tenant, String groupId) throws BaasException {
    QueryDefinition assocQd = new QueryDefinition();
    assocQd.addByField(SkuGroupCategoryAssoc.Queries.SKU_GROUP_ID, Cop.EQUALS, groupId);
    List<SkuGroupCategoryAssoc> assocs = skuGroupCategoryAssocDao.query(tenant, assocQd).getRecords();
    return listByAssoc(tenant, assocs);
  }

  @Tx
  @Override
  public void saveGradeListBySkuCal(String tenant, String orgId, List<PriceGradeBySkuCal> gradeBySkuCals)
      throws BaasException {
    List<PriceGrade> priceGrades = priceGradeDao.list(tenant, orgId);

    List<SkuGradeConfig> configs = new ArrayList<>();
    for (PriceGradeBySkuCal gradeBySkuCal : gradeBySkuCals) {
      List<PUnv> pUnvs = compute(priceGrades, gradeBySkuCal.getFirstPriceGrade(), gradeBySkuCal.getSecondPriceGrade());
      SkuGradeConfig config = new SkuGradeConfig();
      config.setSkuId(gradeBySkuCal.getSkuId());
      config.setPriceGradeJson(JSON.toJSONString(pUnvs));
      config.setOrgId(orgId);
      configs.add(config);
    }
    skuGradeConfigDao.updateByUniqueKey(tenant, configs);
  }

  @Tx
  @Override
  public void saveGradeListByRangeCal(String tenant, String orgId, int groupId, List<PriceGradeByRangeCal> rangeCals)
      throws BaasException {
    List<PriceGrade> priceGrades = priceGradeDao.list(tenant, orgId);

    List<SkuGroupRangeGradeConfig> configs = new ArrayList<>();
    for (PriceGradeByRangeCal rangeCal : rangeCals) {
      List<PUnv> pUnvs = compute(priceGrades, rangeCal.getFirstPriceGrade(), rangeCal.getSecondPriceGrade());

      SkuGroupRangeGradeConfig config = new SkuGroupRangeGradeConfig();
      config.setSkuGroupId(groupId);
      config.setPriceRangeId(rangeCal.getPriceRange().getUuid());
      config.setPriceGradeJson(JSON.toJSONString(pUnvs));
      configs.add(config);
    }
    skuGroupRangeGradeConfigDao.updateByUniqueKey(tenant, configs);
  }

  @Override
  public List<PUnv> computeGradeList(String tenant, String orgId, PUnv firstPriceGrade, PUnv secondPriceGrade)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(firstPriceGrade, "firstPriceGrade");
    Assert.notNull(secondPriceGrade, "secondPriceGrade");

    List<PriceGrade> priceGrades = priceGradeDao.list(tenant,orgId);
    return compute(priceGrades, firstPriceGrade, secondPriceGrade);
  }

  @Override
  public List<PUnv> computeGradeList(String tenant, PUnv firstPriceGrade, PUnv secondPriceGrade) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(firstPriceGrade, "firstPriceGrade");
    Assert.notNull(secondPriceGrade, "secondPriceGrade");

    List<PriceGrade> priceGrades = priceGradeDao.list(tenant, null);
    return compute(priceGrades, firstPriceGrade, secondPriceGrade);
  }

  @Override
  public QueryResult<SkuGradeConfig> querySkuGradeConfig(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    return skuGradeConfigDao.query(tenant, qd);
  }

  @Tx
  @Override
  public void removeSkuGradeConfig(String tenant, Integer uuid) {
    Assert.hasText(tenant, "tenant");

    skuGradeConfigDao.remove(tenant, uuid);
  }

  @Override
  public SkuGradeConfig getSkuGradeBySkuId(String tenant, String orgId, String skuId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(skuId, "skuId");
    return skuGradeConfigDao.get(tenant, orgId, skuId);
  }

  @Override
  public List<SkuGradeConfig> skuGradeConfigList(String tenant, String orgId) {
    Assert.hasText(tenant, "tenant");
    return skuGradeConfigDao.list(tenant, orgId);
  }

  private List<SkuGroupCategory> listByAssoc(String tenant, List<SkuGroupCategoryAssoc> assocs) throws BaasException {
    List<SkuGroup> groups = skuGroupDao.list(tenant);

    // 获取所有的商品分类 避免多次查询
    List<Category> categories = queryAllCategories(tenant);
    List<SkuGroupCategory> groupCategories = new ArrayList<>();
    for (SkuGroupCategoryAssoc assoc : assocs) {
      Category category = getCategoryById(assoc.getCategoryId(), categories);
      if (category == null) {
        continue;
      }
      List<Category> list = new ArrayList<>();
      getCategories(category, list);
      SkuGroup group = groups.stream().filter(s -> s.getUuid() == assoc.getSkuGroupId()).findFirst().orElse(null);
      if (group == null) {
        log.info("自定义类别{0}不存在", assoc.getSkuGroupId());
        continue;
      }
      for (Category c : list) {
        SkuGroupCategory skuGroupCategory = new SkuGroupCategory();
        skuGroupCategory.setSkuGroup(group);
        skuGroupCategory.setCategory(c);
        groupCategories.add(skuGroupCategory);
      }
    }
    log.info("查询的商品分类与前台分类：{}", JsonUtil.objectToJson(groupCategories));
    return groupCategories;
  }

  private Category getCategoryById(String id, List<Category> list) {
    if (CollectionUtils.isEmpty(list)) {
      return null;
    }
    Category result = null;
    for (Category c : list) {
      if (c.getId().equals(id)) {
        result = c;
        break;
      }
      result = getCategoryById(id, c.getCategories());
      if (result != null) {
        break;
      }
    }
    return result;
  }

  private List<Category> queryAllCategories(String tenant) throws BaasException {
    CategoryFilter categoryFilter = new CategoryFilter();
    categoryFilter.setPage(0);
    categoryFilter.setPageSize(Integer.MAX_VALUE);
    categoryFilter.setFetchParts(Category.PART_CHILDREN);
    categoryFilter.setUpperEq("-");
    String key = JsonUtil.objectToJson(categoryFilter);
    Object value = cacheMap.get(key);
    log.info("缓存数据：{}", JsonUtil.objectToJson(value));
    QueryResult<Category> queryResult;
    if (value == null) {
      queryResult = categoryService.query(tenant, categoryFilter);
      log.info("全量商品分类{}", JsonUtil.objectToJson(queryResult.getRecords()));
      cacheMap.put(key, queryResult);
    } else {
      queryResult = (QueryResult<Category>) value;
    }
    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(queryResult.getRecords())) {
      return queryResult.getRecords();
    }
    return new ArrayList<>();
  }

  /**
   * true 存在指派关系 false 不存在指派关系 验证父目录是否被指派自定义类别 包含商品目录本身
   *
   */
  private boolean checkParentAssign(String tenant, String categoryId) throws BaasException {
    List<Category> parents = new ArrayList<>();
    queryParents(tenant, categoryId, parents);
    if (CollectionUtils.isNotEmpty(parents)) {
      List<String> parentIds = parents.stream().map(Category::getId).collect(Collectors.toList());
      List<SkuGroupCategoryAssoc> list = skuGroupCategoryAssocDao.queryByCategoryIds(tenant, parentIds);
      if (CollectionUtils.isNotEmpty(list)) {
        return true;
      }
    }
    return false;
  }

  private void queryParents(String tenent, String categoryId, List<Category> parentList) throws BaasException {
    CategoryFilter filter = new CategoryFilter();
    filter.setIdIn(Arrays.asList(categoryId));
    QueryResult<Category> query = categoryService.query(tenent, filter);
    List<Category> records = query.getRecords();
    if (CollectionUtils.isNotEmpty(records)) {
      Category parent = records.get(0);
      if (Category.ROOT_ID.equalsIgnoreCase(parent.getUpperId())) {
        return;
      }
      parentList.add(parent);
      queryParents(tenent, parent.getUpperId(), parentList);
    }
  }

  /**
   * true 存在指派关系 false 不存在指派关系 验证目录的子目录是否存在指派
   *
   * @param tenant
   * @param category
   * @return
   */
  private boolean checkSonAssign(String tenant, Category category) {
    List<Category> allSon = new ArrayList<>();
    getCategories(category, allSon);
    if (CollectionUtils.isEmpty(allSon)) {
      return false;
    }
    List<String> sonIds = allSon.stream().map(Category::getId).collect(Collectors.toList());
    List<SkuGroupCategoryAssoc> list = skuGroupCategoryAssocDao.queryByCategoryIds(tenant, sonIds);
    // 存在子类与自定义目录
    if (CollectionUtils.isNotEmpty(list)) {
      return true;
    }
    return false;
  }

  private void getCategories(Category category, List<Category> list) {
    if (CollectionUtils.isNotEmpty(category.getCategories())) {
      list.addAll(category.getCategories());
      for (Category c : category.getCategories()) {
        getCategories(c, list);
      }
    }
  }

  private List<PUnv> compute(List<PriceGrade> priceGrades, PUnv firstPriceGrade, PUnv secondPriceGrade)
      throws BaasException {
    PriceGradeRate firstPriceGradeRate = convert(priceGrades, firstPriceGrade);
    PriceGradeRate secondPriceGradeRate = convert(priceGrades, secondPriceGrade);

    List<PriceGradeRate> priceGradeRates = priceGradeRateCalculateMgr.calAndGetPriceGradeRate(firstPriceGradeRate,
        secondPriceGradeRate, priceGrades.size());

    List<PUnv> pUnvs = new ArrayList<>();
    for (PriceGradeRate priceGradeRate : priceGradeRates) {
      PriceGrade priceGrade = priceGrades.get(priceGradeRate.getSeq());
      PUnv pUnv = new PUnv();
      pUnv.setUuid(String.valueOf(priceGrade.getUuid()));
      pUnv.setName(priceGrade.getName());
      pUnv.setValue(priceGradeRate.getIncreaseRate());
      pUnvs.add(pUnv);
    }
    return pUnvs;
  }

  private PriceGradeRate convert(List<PriceGrade> priceGrades, PUnv pUnv) throws BaasException {
    Optional<PriceGrade> first = CommonUtils.findFirst(priceGrades,
        priceGrade -> Objects.equals(String.valueOf(priceGrade.getUuid()), pUnv.getUuid()));
    if (!first.isPresent()) {
      throw new BaasException("价格级不存在");
    }

    PriceGradeRate priceGradeRate = new PriceGradeRate();
    priceGradeRate.setSeq(priceGrades.indexOf(first.get()));
    priceGradeRate.setIncreaseRate(pUnv.getValue());
    return priceGradeRate;
  }
}
