package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.basedata.category.CategoryFilter;
import com.hd123.baas.sop.service.api.basedata.sku.DSku;
import com.hd123.baas.sop.service.api.basedata.sku.DSkuBom;
import com.hd123.baas.sop.service.api.basedata.sku.DSkuTag;
import com.hd123.baas.sop.service.dao.basedata.SkuBomDaoBof;
import com.hd123.baas.sop.service.dao.basedata.SkuDaoBof;
import com.hd123.baas.sop.service.dao.basedata.SkuTagDaoBof;
import com.hd123.baas.sop.service.api.basedata.sku.GoodsIdList;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuBomCreation;
import com.hd123.baas.sop.service.api.basedata.sku.SkuBomCreationList;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.basedata.sku.Tag;
import com.hd123.baas.sop.service.api.price.SkuDefine;
import com.hd123.baas.sop.service.impl.price.config.PriceSkuBom;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateContext;
import com.hd123.rumba.commons.biz.query.*;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.spms.commons.bean.Parts;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Silent
 **/
@Service
public class SkuServiceImpl extends AbstractService implements SkuService {

  @Autowired
  private SkuDaoBof skuDao;
  @Autowired
  private SkuBomDaoBof skuBomDao;
  @Autowired
  private SkuTagDaoBof skuTagDao;
  @Autowired
  private CategoryServiceImpl categoryService;

  public final static String DEFAULT_ORG_ID = "-";
  public final static String DEFAULT_ORG_TYPE = "-";

  @Override
  @Tx
  public String insert(String tenant,String orgType, String orgId, Sku sku, String operator) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(sku, "sku");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");
    Assert.hasText(sku.getId(), "skuId");
    Assert.hasText(sku.getGoodsGid(), "GoodsId");

    DSku result = skuDao.get(tenant,orgType,orgId, sku.getId());
    if (result != null && Boolean.FALSE.equals(result.getDeleted())) {
      return "skuId为" + sku.getId() + "的销售商品已经存在";
    }

    DSku dSku = new DSku();
    BeanUtils.copyProperties(sku, dSku);
    dSku.setCategoryId(sku.getCategory() == null ? null : sku.getCategory().getId());
    dSku.setTenant(tenant);
    dSku.setOrgId(orgId);
    dSku.setOrgType(orgType);
    if (result != null && Boolean.TRUE.equals(result.getDeleted())) {
      dSku.setUuid(result.getUuid());
      dSku.setCreateInfo(result.getCreateInfo());
      onUpdate(dSku, newContext(operator));
    } else {
      dSku.setUuid(null);
      onCreate(dSku, newContext(operator));
    }

    List<DSkuBom> dSkuBomList = buildSkuBomList(tenant, orgType,orgId, sku);
    List<DSkuTag> dSkuTagList = buildSkuTagList(tenant, orgType,orgId,Arrays.asList(sku));

    skuDao.insert(dSku);
    skuBomDao.remove(tenant,orgType,orgId, dSku.getId());
    if (CollectionUtils.isNotEmpty(dSkuBomList)) {
      skuBomDao.insert(dSkuBomList);
    }
    skuTagDao.remove(tenant,orgType,orgId, dSku.getId());
    if (CollectionUtils.isNotEmpty(dSkuTagList)) {
      skuTagDao.insert(dSkuTagList);
    }
    return dSku.getId();
  }

  @Override
  @Tx
  public List<String> batchInit(String tenant,String orgType, String orgId, List<Sku> skuList, String operator) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");
    Assert.notEmpty(skuList, "skuList");

    Set<String> skuIdSet = skuList.stream().map(Sku::getId).collect(Collectors.toSet());
    SkuFilter skuFilter = new SkuFilter();
    skuFilter.setIdIn(new ArrayList<>(skuIdSet));
    skuFilter.setOrgIdEq(orgId);
    skuFilter.setOrgTypeEq(orgType);
    QueryDefinition qd = buildSkuQd(tenant, skuFilter);
    List<DSku> records = skuDao.query(qd).getRecords();
    Map<String, DSku> currentSkuMap = records.stream()
        .collect(Collectors.toMap(DSku::getId, Function.identity(), (existing, replacement) -> existing));
    List<DSku> dSkuList = buildSkuList(tenant,orgType,orgId, currentSkuMap, skuList, newContext(operator));
    List<DSkuTag> dSkuTagList = buildSkuTagList(tenant, orgType,orgId,skuList);

    skuDao.batchInit(dSkuList);
    skuTagDao.remove(tenant, orgType,orgId, new ArrayList<>(skuIdSet));
    if (CollectionUtils.isNotEmpty(dSkuTagList)) {
      skuTagDao.insert(dSkuTagList);
    }
    return new ArrayList<>(skuIdSet);
  }

  @Override
  @Tx
  public void batchInitBom(String tenant,String orgType, String orgId, SkuBomCreationList creationList, String operator) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(creationList, "creationList");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");

    List<DSkuBom> dSkuBomList = new ArrayList<>();
    Set<String> goodsIdSet = new HashSet<>();
    for (SkuBomCreation creation : creationList.getItems()) {
      DSkuBom dSkuBom = new DSkuBom();
      dSkuBom.setBom(creation.getBom());
      dSkuBom.setTenant(tenant);
      dSkuBom.setOrgId(orgId);
      dSkuBom.setOrgType(orgType);
      dSkuBom.setSkuId(creation.getSkuId());
      dSkuBom.setGoodsGid(creation.getGoodsGid());
      dSkuBomList.add(dSkuBom);

      goodsIdSet.add(creation.getGoodsGid());
    }
    skuBomDao.removeByGoodsIdList(tenant,orgType,orgId, new ArrayList<>(goodsIdSet));
    skuBomDao.insert(dSkuBomList);
  }

  private List<DSku> buildSkuList(String tenant,String orgType, String orgId, Map<String, DSku> currentSkuMap, List<Sku> skuList,
      OperateContext operatorContext) {
    List<DSku> dSkuList = new ArrayList<>();
    Set<String> uniqueIdSet = new HashSet<>();
    for (Sku target : skuList) {
      if (StringUtil.isNullOrBlank(target.getId()) || StringUtil.isNullOrBlank(target.getGoodsGid())
          || uniqueIdSet.contains(target.getId())) {
        continue;
      }
      uniqueIdSet.add(target.getId());

      DSku dSku = new DSku();
      BeanUtils.copyProperties(target, dSku);
      dSku.setCategoryId(target.getCategory() == null ? null : target.getCategory().getId());
      dSku.setTenant(tenant);
      dSku.setOrgId(orgId);
      dSku.setOrgType(orgType);
      dSku.setQpcDesc(target.getQpcDesc());
      if (currentSkuMap.containsKey(target.getId())) {
        dSku.setUuid(currentSkuMap.get(target.getId()).getUuid());
        dSku.setCreateInfo(currentSkuMap.get(target.getId()).getCreateInfo());
        dSku.setVersion(currentSkuMap.get(target.getId()).getVersion());
        dSku.setGoodsGid(currentSkuMap.get(target.getId()).getGoodsGid());
        onUpdate(dSku, operatorContext);
      } else {
        onCreate(dSku, operatorContext);
      }
      dSkuList.add(dSku);
    }
    return dSkuList;
  }

  private List<DSkuBom> buildSkuBomList(String tenant,String orgType, String orgId, Sku sku) {
    List<DSkuBom> dSkuBomList = new ArrayList<>();
    Set<String> uniqueBomSet = new HashSet<>();
    if (!CollectionUtils.isEmpty(sku.getBom())) {
      for (String bom : sku.getBom()) {
        if (StringUtil.isNullOrBlank(bom) || uniqueBomSet.contains(bom)) {
          continue;
        }
        uniqueBomSet.add(bom);
        DSkuBom dSkuBom = new DSkuBom();
        dSkuBom.setBom(bom);
        dSkuBom.setTenant(tenant);
        dSkuBom.setOrgId(orgId);
        dSkuBom.setOrgType(orgType);
        dSkuBom.setSkuId(sku.getId());
        dSkuBom.setGoodsGid(sku.getGoodsGid());
        dSkuBomList.add(dSkuBom);
      }
    }
    return dSkuBomList;
  }

  private List<DSkuTag> buildSkuTagList(String tenant,String orgType, String orgId, List<Sku> skuList) {
    List<DSkuTag> dSkuTags = new ArrayList<>();
    Set<String> uniqueTagSet = new HashSet<>();
    for (Sku sku : skuList) {
      if (!CollectionUtils.isEmpty(sku.getTags())) {
        for (Tag tag : sku.getTags()) {
          if (uniqueTagSet.contains(sku.getId() + tag.getCode())) {
            continue;
          }
          uniqueTagSet.add(sku.getId() + tag.getCode());
          DSkuTag dSkuTag = new DSkuTag();
          dSkuTag.setCode(tag.getCode());
          dSkuTag.setName(tag.getName());
          dSkuTag.setTenant(tenant);
          dSkuTag.setOrgId(orgId);
          dSkuTag.setOrgType(orgType);
          dSkuTag.setSkuId(sku.getId());
          dSkuTag.setGoodsGid(sku.getGoodsGid());
          dSkuTags.add(dSkuTag);
        }
      }
    }
    return dSkuTags;
  }

  @Override
  @Tx
  public String update(String tenant,String orgType, String orgId, Sku sku, String operator) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(sku, "sku");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");

    DSku result = skuDao.get(tenant, orgType,orgId, sku.getId());
    if (result == null || Boolean.TRUE.equals(result.getDeleted())) {
      return "未找到该skuId为" + sku.getId() + "的商品，无法更改";
    }

    DSku dSku = new DSku();
    BeanUtils.copyProperties(sku, dSku);
    dSku.setUuid(result.getUuid());
    dSku.setOrgId(orgId);
    dSku.setOrgType(orgType);
    dSku.setGoodsGid(result.getGoodsGid());
    dSku.setCategoryId(sku.getCategory() == null ? null : sku.getCategory().getId());
    dSku.setTenant(tenant);
    dSku.setCreateInfo(result.getCreateInfo());
    dSku.setInputCode(result.getInputCode());
    dSku.setRequired(result.getRequired());
    dSku.setPlu(result.getPlu());
    dSku.setVersion(result.getVersion());
    sku.setGoodsGid(result.getGoodsGid()); // GoodsGid不允许修改
    onUpdate(dSku, newContext(operator));

    List<DSkuBom> dSkuBomList = buildSkuBomList(tenant,orgType,orgId, sku);
    List<DSkuTag> dSkuTags = buildSkuTagList(tenant,orgType,orgId, Arrays.asList(sku));

    skuDao.insert(dSku);
    skuBomDao.remove(tenant,orgType,orgId, dSku.getId());
    if (CollectionUtils.isNotEmpty(dSkuBomList)) {
      skuBomDao.insert(dSkuBomList);
    }
    skuTagDao.remove(tenant,orgType,orgId, dSku.getId());
    if (CollectionUtils.isNotEmpty(dSkuTags)) {
      skuTagDao.insert(dSkuTags);
    }
    return dSku.getId();
  }

  @Override
  @Tx
  public void delete(String tenant,String orgType, String orgId, String Id, String operator) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(Id, "Id");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");

    skuDao.remove(tenant,orgType,orgId, Id);
    skuBomDao.remove(tenant,orgType,orgId, Id);
    skuTagDao.remove(tenant,orgType,orgId, Id);
  }

  @Override
  @Tx
  public void deleteByGoodsIdList(String tenant,String orgType, String orgId, GoodsIdList goodsIdList, String operator) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(goodsIdList, "goodsIdList");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");

    skuDao.removeByGoodsIdList(tenant,orgType,orgId, goodsIdList.getGoodsIds());
    skuBomDao.removeByGoodsIdList(tenant,orgType,orgId, goodsIdList.getGoodsIds());
    skuTagDao.removeByGoodsIdList(tenant,orgType,orgId, goodsIdList.getGoodsIds());
  }

  @Override
  @Tx
  public void deleteByIds(String tenant,String orgType, String orgId, List<String> ids, String operator) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(ids, "ids");

    skuDao.removeByIds(tenant,orgType,orgId, ids);
    skuBomDao.remove(tenant,orgType,orgId, ids);
    skuTagDao.remove(tenant,orgType,orgId, ids);
  }

  @Override
  public Sku get(String tenant,String orgType, String orgId, String id) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(id, "id");
    Assert.assertArgumentNotNull(orgType, "orgType");
    Assert.assertArgumentNotNull(orgId, "orgId");

    DSku dSku = skuDao.get(tenant,orgType,orgId, id);
    if (dSku == null) {
      return new Sku();
    }
    Sku sku = new Sku();
    BeanUtils.copyProperties(dSku, sku);
    if (!StringUtil.isNullOrBlank(dSku.getCategoryId())) {
      CategoryFilter categoryFilter = new CategoryFilter();
      categoryFilter.setIdIn(Arrays.asList(dSku.getCategoryId()));
      categoryFilter.setOrgIdEq(orgId);
      categoryFilter.setOrgTypeEq(orgType);
      QueryResult<Category> categoryList = categoryService.query(tenant, categoryFilter);
      sku.setCategory(CollectionUtils.isNotEmpty(categoryList.getRecords()) ? categoryList.getRecords().get(0) : null);
    }

    QueryDefinition qd = buildSkuBomQd(tenant,Collections.singleton(orgType),Collections.singleton(orgId), Arrays.asList(id));
    List<DSkuBom> dSkuBomList = skuBomDao.query(qd).getRecords();
    if (CollectionUtils.isNotEmpty(dSkuBomList)) {
      List<String> boms = dSkuBomList.stream().map(DSkuBom::getBom).collect(Collectors.toList());
      sku.setBom(boms);
    }

    QueryDefinition qdTag = buildSkuTagQd(tenant,Collections.singleton(orgType),Collections.singleton(orgId),  Arrays.asList(id));
    List<DSkuTag> dSkuTags = skuTagDao.query(qdTag).getRecords();
    if (CollectionUtils.isNotEmpty(dSkuTags)) {
      List<Tag> tags = new ArrayList<>();
      for (DSkuTag dSkuTag : dSkuTags) {
        Tag tag = new Tag();
        tag.setCode(dSkuTag.getCode());
        tag.setName(dSkuTag.getName());
        tags.add(tag);
      }
      sku.setTags(tags);
    }
    return sku;
  }

  private QueryDefinition buildSkuQd(String tenantId, SkuFilter filter) throws BaasException {
    QueryDefinition qd = new QueryDefinition(filter.getPage(), filter.getPageSize());
    AndCondition andCondition = new AndCondition();
    andCondition.addByField(DSku.Queries.TENANT, Cop.EQUALS, tenantId);
    if (!StringUtil.isNullOrBlank(filter.getKeywordLike())) {
      OrCondition orCondition = new OrCondition();
      orCondition.addByField(DSku.Queries.CODE, Cop.LIKES, filter.getKeywordLike());
      orCondition.addByField(DSku.Queries.NAME, Cop.LIKES, filter.getKeywordLike());
      orCondition.addByField(DSku.Queries.INPUT_CODE, Cop.LIKES, filter.getKeywordLike());
      andCondition.addConditions(orCondition);
    }
    if (CollectionUtils.isNotEmpty(filter.getOrgIdIn())){
      List<String> orgList = filter.getOrgIdIn().stream().map(DefaultOrgIdConvert::toMasDefOrgId).collect(Collectors.toList());
      andCondition.addByField(DSku.Queries.ORG_ID, Cop.IN, orgList.toArray());
    }
    if (!StringUtil.isNullOrBlank(filter.getOrgIdEq())) {
      String orgId = DefaultOrgIdConvert.toMasDefOrgId(filter.getOrgIdEq());
      andCondition.addByField(DSku.Queries.ORG_ID, Cop.EQUALS, orgId);
    }
    if (!StringUtil.isNullOrBlank(filter.getOrgTypeEq())) {
      andCondition.addByField(DSku.Queries.ORG_TYPE, Cop.EQUALS, filter.getOrgTypeEq());
    }
    if (filter.getDeletedEq() != null) {
      andCondition.addByField(DSku.Queries.DELETED, Cop.EQUALS, filter.getDeletedEq());
    }
    if (filter.getRequiredEq() != null) {
      andCondition.addByField(DSku.Queries.REQUIRED, Cop.EQUALS, filter.getRequiredEq());
    }
    if (CollectionUtils.isNotEmpty(filter.getGoodsTypeIn())) {
      andCondition.addByField(DSku.Queries.H6_GOODS_TYPE, Cop.IN, filter.getGoodsTypeIn().toArray());
    }
    if (CollectionUtils.isNotEmpty(filter.getGoodsTypeNotIn())) {
      OrCondition orCondition = new OrCondition();
      orCondition.addByField(DSku.Queries.H6_GOODS_TYPE, Cop.not(Cop.IN), filter.getGoodsTypeNotIn().toArray());
      orCondition.addByField(DSku.Queries.H6_GOODS_TYPE, Cop.IS_NULL);
      andCondition.addConditions(orCondition);
    }
    if (filter.getPluIsNull() != null) {
      if (filter.getPluIsNull()) {
        andCondition.addByField(DSku.Queries.PLU, Cop.IS_NULL);
      } else {
        andCondition.addByField(DSku.Queries.PLU, Cop.NOT + Cop.IS_NULL);
      }
    }
    if (filter.getRequiredEq() != null) {
      andCondition.addByField(DSku.Queries.REQUIRED, Cop.EQUALS, filter.getRequiredEq());
    }
    if (filter.getInputCodeEq() != null) {
      andCondition.addByField(DSku.Queries.INPUT_CODE, Cop.EQUALS, filter.getInputCodeEq());
    }
    if (filter.getInputCodeIsNull() != null) {
      if (filter.getInputCodeIsNull()) {
        andCondition.addByField(DSku.Queries.INPUT_CODE, Cop.IS_NULL);
      } else {
        andCondition.addByField(DSku.Queries.INPUT_CODE, Cop.NOT + Cop.IS_NULL);
        andCondition.addByField(DSku.Queries.INPUT_CODE, Cop.NOT + Cop.EQUALS, "");
      }
    }
    if (filter.getQpcEq() != null) {
      andCondition.addByField(DSku.Queries.QPC, Cop.EQUALS, filter.getQpcEq());
    }
    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(filter.getCodeIn())) {
      andCondition.addByField(DSku.Queries.CODE, Cop.IN, filter.getCodeIn().toArray());
    }
    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(filter.getGoodsGidIn())) {
      andCondition.addByField(DSku.Queries.GOODS_GID, Cop.IN, filter.getGoodsGidIn().toArray());
    }
    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(filter.getGoodsGidNotIn())) {
      andCondition.addByField(DSku.Queries.GOODS_GID, Cop.not(Cop.IN), filter.getGoodsGidNotIn().toArray());
    }
    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(filter.getCategoryIdIn())) {
      andCondition.addByField(DSku.Queries.CATEGORY_ID, Cop.IN, filter.getCategoryIdIn().toArray());
    }
    if (CollectionUtils.isNotEmpty(filter.getCategoryIdNotIn())) {
      andCondition.addByField(DSku.Queries.CATEGORY_ID, Cop.not(Cop.IN), filter.getCategoryIdNotIn().toArray());
    }
    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(filter.getIdIn())) {
      andCondition.addByField(DSku.Queries.ID, Cop.IN, filter.getIdIn().toArray());
    }
    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(filter.getInputCodeIn())) {
      andCondition.addByField(DSku.Queries.INPUT_CODE, Cop.IN, filter.getInputCodeIn().toArray());
    }
    if (filter.getSkuDuEq() != null) {
      andCondition.addByField(DSku.Queries.SKU_DU, Cop.EQUALS, filter.getSkuDuEq());
    }
    if (filter.getSorts() != null && !filter.getSorts().isEmpty()) {
      buildQdOrder(filter, qd);
    }
    qd.setCondition(andCondition);
    return qd;
  }

  private QueryDefinition buildSkuBomQd(String tenantId,Set<String> orgTypes, Set<String> orgIds, List<String> skuIds) {
    QueryDefinition qd = new QueryDefinition();
    AndCondition andCondition = new AndCondition();
    andCondition.addByField(DSkuBom.Queries.ORG_TYPE, Cop.IN, orgTypes.toArray());
    andCondition.addByField(DSkuBom.Queries.ORG_ID, Cop.IN, orgIds.toArray());
    andCondition.addByField(DSkuBom.Queries.TENANT, Cop.EQUALS, tenantId);
    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(skuIds)) {
      andCondition.addByField(DSkuBom.Queries.SKU_ID, Cop.IN, skuIds.toArray());
    }
    qd.setCondition(andCondition);
    return qd;
  }

  private QueryDefinition buildSkuTagQd(String tenantId,Set<String> orgTypes, Set<String> orgIds, List<String> skuIds) {
    QueryDefinition qd = new QueryDefinition();
    AndCondition andCondition = new AndCondition();
    andCondition.addByField(DSkuTag.Queries.TENANT, Cop.EQUALS, tenantId);
    andCondition.addByField(DSkuTag.Queries.ORG_TYPE, Cop.IN, orgTypes.toArray());
    andCondition.addByField(DSkuTag.Queries.ORG_ID, Cop.IN, orgIds.toArray());
    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(skuIds)) {
      andCondition.addByField(DSkuTag.Queries.SKU_ID, Cop.IN, skuIds.toArray());
    }
    qd.setCondition(andCondition);
    return qd;
  }

  @Override
  public QueryResult<Sku> query(String tenant, SkuFilter filter) throws BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    buildCategoryFilter(tenant, filter);
    buildSkuDefineFilter(tenant, filter);
    QueryDefinition qd = buildSkuQd(tenant, filter);
    QueryResult<DSku> skuResult = skuDao.query(qd);
    List<DSku> records = skuResult.getRecords();

    List<Sku> target = new ArrayList<>();
    Map<String, String> targetMap = new HashMap<>();
    Set<String> orgTypes = new HashSet<>();
    Set<String> orgIds = new HashSet<>();
    Set<String> categoryIds = new HashSet<>();
    for (DSku dSku : records) {
      Sku sku = new Sku();
      BeanUtils.copyProperties(dSku, sku);
      target.add(sku);
      orgTypes.add(sku.getOrgType());
      orgIds.add(sku.getOrgId());
      categoryIds.add(dSku.getCategoryId());
      String key = dSku.getCategoryId();
      String skuKey = dSku.getId() + "," + dSku.getOrgId() + "," + dSku.getOrgType();
      targetMap.put(skuKey, key);
    }
    fetchParts(tenant,orgTypes, orgIds,categoryIds,targetMap, target, filter);
    QueryResult<Sku> result = new QueryResult<Sku>();
    result.setRecords(target);
    result.setPage(filter.getPage());
    result.setPageSize(filter.getPageSize());
    result.setPageCount(skuResult.getPageCount());
    result.setRecordCount(skuResult.getRecordCount());
    result.setMore(skuResult.getRecordCount() > filter.getPageSize() * (filter.getPage() + 1));
    return result;
  }

  private void buildCategoryFilter(String tenantId, SkuFilter filter) throws BaasException {
    if (StringUtil.isNullOrBlank(filter.getCategoryUpperIdEq())) {
      return;
    }
    CategoryFilter categoryFilter = new CategoryFilter();
    categoryFilter.setIdIn(Arrays.asList(filter.getCategoryUpperIdEq()));
    categoryFilter.setFetchParts(Category.PART_CHILDREN);
    categoryFilter.setOrgIdEq(filter.getOrgIdEq());
    categoryFilter.setOrgTypeEq(filter.getOrgTypeEq());
    QueryResult<Category> categoryList = categoryService.query(tenantId, categoryFilter);

    Set<String> categoryUpperIds = new HashSet<>();
    fetchCategoryIds(categoryList.getRecords(), categoryUpperIds);
    if (CollectionUtils.isEmpty(filter.getCategoryIdIn())) {
      filter.setCategoryIdIn(new ArrayList<>());
    }
    if (CollectionUtils.isNotEmpty(categoryUpperIds)) {
      filter.getCategoryIdIn().addAll(categoryUpperIds);
    }
  }

  private void buildSkuDefineFilter(String tenantId, SkuFilter filter) throws BaasException {
    if (StringUtil.isNullOrBlank(filter.getSkuDefineEq())) {
      return;
    }
    // 仅处理原料品
    if (!SkuDefine.SPLITBYPART_RAW.name().equalsIgnoreCase(filter.getSkuDefineEq())) {
      return;
    }
    List<DSkuBom> list = skuBomDao.list(tenantId);
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    List<String> gids = new ArrayList<>();
    for (DSkuBom i : list) {
      PriceSkuBom priceSkuBom = JsonUtil.jsonToObject(i.getBom(), PriceSkuBom.class);
      if (PriceSkuBom.PriceSkuBomType.splitByPart.name().equals(priceSkuBom.getType())) {
        // 原料品
        if (CollectionUtils.isNotEmpty(priceSkuBom.getRaw())) {
          gids.addAll(priceSkuBom.getRaw().stream().map(PriceSkuBom.BomRawLine::getGdGid).collect(Collectors.toList()));
        }
      }
    }
    if (CollectionUtils.isNotEmpty(gids)) {
      if (CollectionUtils.isNotEmpty(filter.getGoodsGidIn())) {
        filter.getGoodsGidIn().retainAll(gids);
      } else {
        filter.setGoodsGidIn(gids);
      }
    }

  }

  private void fetchCategoryIds(List<Category> records, Set<String> categoryUpperIds) {
    if (CollectionUtils.isEmpty(records)) {
      return;
    }
    for (Category record : records) {
      categoryUpperIds.add(record.getId());
      fetchCategoryIds(record.getCategories(), categoryUpperIds);
    }
  }

  private void fetchParts(String tenant, Set<String> orgTypes, Set<String> orgIds, Set<String> categoryIds,Map<String, String> targetMap, List<Sku> records, SkuFilter filter)
      throws BaasException {
    if (CollectionUtils.isEmpty(records)) {
      return;
    }
    Parts parts = new Parts(filter.getFetchParts());
    if (parts.contains(Sku.PART_CATEGORY)) {
      CategoryFilter categoryFilter = new CategoryFilter();
      categoryFilter.setIdIn(new ArrayList<>(categoryIds));
//      categoryFilter.setOrgIdIn(new ArrayList<>(orgIds));
//      categoryFilter.setOrgTypeIn(new ArrayList<>(orgTypes));
      List<Category> categoryResult = categoryService.query(tenant, categoryFilter).getRecords();
      Map<String, Category> categoryMap = new HashMap<>();
      if (CollectionUtils.isNotEmpty(categoryResult)) {
        categoryMap = categoryResult.stream()
            .collect(Collectors.toMap(category-> category.getId(), Function.identity(), (existing, replacement) -> existing));
      }
      for (Sku record : records) {
        String skuKey = record.getId() + "," + record.getOrgId() + "," + record.getOrgType();
        String categoryKey = targetMap.containsKey(skuKey) ? targetMap.get(skuKey) : null;
        record.setCategory(categoryMap.containsKey(categoryKey) ? categoryMap.get(categoryKey) : null);
      }
    }
    if (parts.contains(Sku.PART_BOM)) {
      Set<String> skuIdSet = records.stream().map(Sku::getId).collect(Collectors.toSet());
      QueryDefinition qd = buildSkuBomQd(tenant,orgTypes,orgIds, new ArrayList<>(skuIdSet));
      List<DSkuBom> dSkuBomList = skuBomDao.query(qd).getRecords();
      if (CollectionUtils.isEmpty(dSkuBomList)) {
        return;
      }
      Map<String, List<String>> dSkuBomMap = new HashMap<>();
      for (DSkuBom dSkuBom : dSkuBomList) {
        String key = dSkuBom.getSkuId() + "," + dSkuBom.getOrgId() + "," + dSkuBom.getOrgType();
        if (!dSkuBomMap.containsKey(key)) {
          dSkuBomMap.put(key, new ArrayList<>());
        }
        dSkuBomMap.get(key).add(dSkuBom.getBom());
      }
      for (Sku record : records) {
        String key = record.getId() + "," + record.getOrgId() + "," + record.getOrgType();
        if (!dSkuBomMap.containsKey(key)) {
          continue;
        }
        record.setBom(dSkuBomMap.get(key));
      }
    }

    if (parts.contains(Sku.PART_TAG)) {
      Set<String> skuIdSet = records.stream().map(Sku::getId).collect(Collectors.toSet());
      QueryDefinition qd = buildSkuTagQd(tenant, orgTypes,orgIds,new ArrayList<>(skuIdSet));
      List<DSkuTag> dSkuTags = skuTagDao.query(qd).getRecords();
      if (CollectionUtils.isEmpty(dSkuTags)) {
        return;
      }
      Map<String, List<Tag>> dSkuTagMap = new HashMap<>();
      for (DSkuTag dSkuTag : dSkuTags) {
        String key = dSkuTag.getSkuId() + "," + dSkuTag.getOrgId() + "," + dSkuTag.getOrgType();
        if (!dSkuTagMap.containsKey(key)) {
          dSkuTagMap.put(key, new ArrayList<>());
        }
        Tag tag = new Tag();
        tag.setName(dSkuTag.getName());
        tag.setCode(dSkuTag.getCode());
        dSkuTagMap.get(key).add(tag);
      }
      for (Sku record : records) {
        String key = record.getId() + "," + record.getOrgId() + "," + record.getOrgType();
        if (!dSkuTagMap.containsKey(key)) {
          continue;
        }
        record.setTags(dSkuTagMap.get(key));
      }
    }
  }
}
