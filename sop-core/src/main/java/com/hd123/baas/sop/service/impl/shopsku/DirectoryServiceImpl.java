package com.hd123.baas.sop.service.impl.shopsku;

import com.alibaba.fastjson.JSON;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.skumgr.Directory;
import com.hd123.baas.sop.service.api.skumgr.DirectoryService;
import com.hd123.baas.sop.service.api.skumgr.DirectoryShop;
import com.hd123.baas.sop.service.api.skumgr.DirectorySku;
import com.hd123.baas.sop.service.api.skumgr.DirectorySkuFilter;
import com.hd123.baas.sop.service.api.skumgr.SkuProperty;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasPageResponse;
import com.hd123.baas.sop.remote.rsmas.cat.Cat;
import com.hd123.baas.sop.remote.rsmas.cat.CatCreation;
import com.hd123.baas.sop.remote.rsmas.cat.CatFilter;
import com.hd123.baas.sop.remote.rsmas.cat.CatSKUCreation;
import com.hd123.baas.sop.remote.rsmas.cat.CatSKUMod;
import com.hd123.baas.sop.remote.rsmas.cat.CatSKURelation;
import com.hd123.baas.sop.remote.rsmas.cat.CatSKURelationFilter;
import com.hd123.baas.sop.remote.rsmas.cat.CatShop;
import com.hd123.baas.sop.remote.rsmas.cat.CatShopFilter;
import com.hd123.baas.sop.remote.rsmas.cat.MasPageResponse;
import com.hd123.baas.sop.remote.rsmas.cat.MasRequest;
import com.hd123.baas.sop.remote.rsmas.cat.MasResponse;
import com.hd123.baas.sop.remote.rsmas.cat.RsMasCatClient;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.baas.sop.remote.rsmas.store.RsStoreFilter;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hd123.baas.sop.common.OrgConstants.DEFAULT_ORG_TYPE;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Service
public class DirectoryServiceImpl implements DirectoryService {
  @Autowired
  private RsMasCatClient skuCatClient;
  @Autowired
  private SkuService skuService;
  @Autowired
  private RsMasClient rsMasClient;

  @Override
  public String saveNew(String tenant, String orgId, String name, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(name, "目录名称");
    List<Directory> list = this.list(tenant, orgId);
    MasResponse<String> masResponse;
    boolean existName = list.stream().anyMatch(d -> d.getName().equals(name));
    if (existName) {
      throw new BaasException("目录名称已存在");
    }
    try {
      CatCreation catCreation = new CatCreation();
      catCreation.setName(name);
      catCreation.setOperatorId(operateInfo.getOperator().getId());
      masResponse = skuCatClient.create(tenant, DEFAULT_ORG_TYPE, orgId, catCreation, operateInfo.getOperator().getFullName());
      if (!masResponse.isSuccess()) {
        throw new BaasException("调用失败:{}", masResponse.getEchoMessage());
      }
    } catch (Exception e) {
      throw new BaasException("资料中台调用失败:{}", e.getMessage());
    }

    if (!list.isEmpty()) {
      Directory defDirectory = list.get(0);
      DirectorySkuFilter filter = new DirectorySkuFilter();
      filter.setPage(0);
      filter.setPageSize(Integer.MAX_VALUE);
      QueryResult<DirectorySku> directorySkuQueryResult = queryDirectorySku(tenant, defDirectory.getUuid(), filter);
      if (!directorySkuQueryResult.getRecords().isEmpty()) {
        List<String> skuIds = directorySkuQueryResult.getRecords()
            .stream()
            .filter(DirectorySku::isChannelRequired)
            .map(DirectorySku::getSkuId)
            .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(skuIds)) {
          addSku(tenant, orgId, masResponse.getData(), skuIds, operateInfo);
        }
      }
    }
    return masResponse.getData();
  }

  @Override
  public List<Directory> list(String tenant, String orgId) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    List<Directory> directories = new ArrayList<>();
    CatFilter filter = new CatFilter();
    filter.setOrgIdEq(orgId);
    return queryCat(tenant,filter);
  }

  @Override
  public List<Directory> listByOrgIds(String tenant, List<String> orgIds) {
    Assert.hasText(tenant, "tenant");
    CatFilter filter = new CatFilter();
    if(CollectionUtils.isNotEmpty(orgIds)){
      filter.setOrgIdIn(orgIds);
    }
    return queryCat(tenant,filter);
  }

  @Override
  public Directory get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(tenant, "uuid");
    CatFilter filter = new CatFilter();
    filter.setIdEq(uuid);
    List<Directory> records = queryCat(tenant, filter);
    if(CollectionUtils.isEmpty(records)){
      return null;
    }
    return records.get(0);
  }

  @Override
  public void delete(String tenant, String orgId, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(operateInfo, "operateInfo");
    List<Directory> directories = list(tenant, orgId);
    if (directories.isEmpty()) {
      log.info("目录列表为空");
      return;
    }
    if (directories.get(0).getUuid().equals(uuid)) {
      throw new BaasException("默认目录不能删除");
    }
    // 1.删除目录关联的门店
    List<DirectoryShop> shops = queryShops(tenant, orgId, uuid);
    List<String> shopIds = shops.stream().map(DirectoryShop::getShop).collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(shopIds)) {
      deleteShops(tenant, orgId, uuid, shopIds, operateInfo);
      // 2.将门店关联到默认目录
      relateShops(tenant, orgId, directories.get(0).getUuid(), shopIds, operateInfo);
    }
    // 3.删除目录
    MasResponse<Void> response = skuCatClient.delete(tenant, DEFAULT_ORG_TYPE, orgId, uuid, operateInfo.getOperator().getFullName());
    if (!response.isSuccess()) {
      throw new BaasException("删除目录失败:{0}", response.getEchoMessage());
    }

  }

  @Override
  @Tx
  public void relateShops(String tenant, String orgId, String uuid, List<String> shops, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notEmpty(shops, "shops");
    Assert.notNull(operateInfo, "operateInfo");

    List<CatShop> catShops = new ArrayList<>();
    for (String shop : shops) {
      CatShop catShop = new CatShop();
      catShop.setShopId(shop);
      catShops.add(catShop);
    }
    // 1.删除门店同其他目录的关联关系
    CatFilter filter = new CatFilter();
    filter.setIncludeShopId(shops);
    filter.setFetchParts("shops");
    MasPageResponse<List<Cat>> catListResponse = skuCatClient.query(tenant, filter);
    List<Cat> catList = catListResponse.getData();
    Map<String, List<String>> listMap = new HashMap<>();
    for (String shop : shops) {
      Cat cat = null;
      if (!CollectionUtils.isEmpty(catList)) {
        catList = catList.stream().filter(c -> !c.getId().equals(uuid)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(catList)) {
          cat = catList.stream()
              .filter(c -> !c.getId().equals(uuid) && CollectionUtils.isNotEmpty(c.getShops())
                  && c.getShops().stream().anyMatch(h -> h.getShopId().equals(shop)))
              .findFirst()
              .orElse(null);
        }
      }
      if (cat == null) {
        log.info("过滤门店{}", shop);
        continue;
      }
      List<String> sp = listMap.getOrDefault(cat.getId(), new ArrayList<>());
      sp.add(shop);
      listMap.put(cat.getId(), sp);
    }
    log.info("删除其他目录与门店关联：{}", JSON.toJSON(listMap));
    for (String catId : listMap.keySet()) {
      List<String> deleteShopList = listMap.get(catId);
      deleteShops(tenant, orgId, catId, deleteShopList, operateInfo);
    }

    // 2.删除目录下的门店
    List<DirectoryShop> directoryShops = queryShops(tenant, null, uuid);
    if (CollectionUtils.isNotEmpty(directoryShops)) {
      List<String> deleteShops = directoryShops.stream().map(DirectoryShop::getShop).collect(Collectors.toList());
      deleteShops(tenant, orgId, uuid, deleteShops, operateInfo);
    }
    // 3.保存门店与目录关系
    try {
      MasRequest<List<CatShop>> request = new MasRequest();
      request.setData(catShops);
      MasResponse<Void> response = skuCatClient.saveShops(tenant, DEFAULT_ORG_TYPE, orgId, uuid, request,
          operateInfo.getOperator().getFullName());
      if (!response.isSuccess()) {
        throw new BaasException("关联门店失败:{0}", response.getEchoMessage());
      }
    } catch (Exception e) {
      log.error("资料中台调用失败:{}", e.getMessage());
      throw new BaasException("资料中台调用失败");
    }
  }

  @Override
  public void deleteShops(String tenant, String orgId, String uuid, List<String> shops, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notEmpty(shops, "shops");

    List<DirectoryShop> directoryShops = queryShops(tenant, null, uuid);
    Set<String> shopIdSet = directoryShops.stream().map(DirectoryShop::getShop).collect(Collectors.toSet());
    List<String> diffShop = shops.stream().filter(s -> !shopIdSet.contains(s)).collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(diffShop)) {
      throw new BaasException("门店没有关联目录：{0}", diffShop);
    }
    try {
      MasRequest<List<String>> request = new MasRequest();
      request.setData(shops);
      MasResponse<Void> response = skuCatClient.removeShops(tenant, DEFAULT_ORG_TYPE, orgId, uuid, request,
          operateInfo.getOperator().getFullName());
      // 新增门店到默认目录
      if (!response.isSuccess()) {
        throw new BaasException("删除门店失败:{0}", response.getEchoMessage());
      }
    } catch (Exception e) {
      log.error("资料中台调用失败:{}", e.getMessage());
      throw new BaasException("资料中台调用失败");
    }
  }

  @Override
  public List<DirectoryShop> queryShops(String tenant, String orgId, String uuid) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    List<DirectoryShop> shops = new ArrayList<>();
    CatShopFilter filter = new CatShopFilter();
    try {
      MasPageResponse<List<CatShop>> response = skuCatClient.query(tenant, uuid, filter);
      if (!response.isSuccess()) {
        throw new BaasException("查询门店失败:{0}", response.getEchoMessage());
      }
      List<String> shopIds = response.getData().stream().map(CatShop::getShopId).collect(Collectors.toList());
      if (!shopIds.isEmpty()) {
        RsStoreFilter rsStoreFilter = new RsStoreFilter();
        rsStoreFilter.setIdIn(shopIds);
        orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
        rsStoreFilter.setOrgIdEq(orgId);
        RsMasPageResponse<List<RsStore>> listRsMasPageResponse = rsMasClient.storeQuery(tenant, rsStoreFilter);
        if (listRsMasPageResponse.isSuccess()) {
          for (RsStore datum : listRsMasPageResponse.getData()) {
            DirectoryShop shop = new DirectoryShop();
            shop.setOwner(uuid);
            shop.setShop(datum.getId());
            shop.setShopCode(datum.getCode());
            shop.setShopName(datum.getName());
            shops.add(shop);
          }
        }
      }

    } catch (Exception e) {
      log.error("资料中台调用失败:{}", e.getMessage());
      throw new BaasException("资料中台调用失败");
    }
    return shops;
  }

  @Override
  public QueryResult<DirectorySku> queryDirectorySku(String tenant, String uuid, DirectorySkuFilter filter)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(filter, "filter");

    QueryResult<DirectorySku> result = new QueryResult<>();
    result.setPageSize(filter.getPageSize());
    result.setPage(filter.getPage());
    result.setPageCount(0);
    result.setRecordCount(0);
    CatSKURelationFilter relationFilter = new CatSKURelationFilter();
    relationFilter.setCatIdIn(Collections.singletonList(uuid));
    relationFilter.setPageSize(Integer.MAX_VALUE);
    relationFilter.setPage(0);
    if (filter.getDirectorySelect() != null) {
      relationFilter.setRequired(filter.getDirectorySelect());
    }
    MasPageResponse<List<CatSKURelation>> catSkuRelations = skuCatClient.query(tenant, relationFilter);
    if (CollectionUtils.isEmpty(catSkuRelations.getData())) {
      return result;
    }
    Map<String, Boolean> skuRequiredMap = catSkuRelations.getData()
        .stream()
        .collect(Collectors.toMap(CatSKURelation::getSkuId, CatSKURelation::getRequired));
    List<String> skuIds = catSkuRelations.getData().stream().map(CatSKURelation::getSkuId).collect(Collectors.toList());
    if (CollectionUtils.isEmpty(skuIds)) {
      return result;
    }
    //获取当前目录的orgId;
    Directory directory = get(tenant, uuid);
    if(directory == null){
      throw new BaasException("目录不存在");
    }
    SkuFilter skuFilter = new SkuFilter();
    skuFilter.setIdIn(skuIds);
    skuFilter.setOrgIdEq(directory.getOrgId());
    skuFilter.setPage(filter.getPage());
    skuFilter.setPageSize(filter.getPageSize());
    if (StringUtils.isNotBlank(filter.getKeywordLike())) {
      skuFilter.setKeywordLike(filter.getKeywordLike());
    }
    if (filter.getRequiredEq() != null) {
      skuFilter.setRequiredEq(filter.getRequiredEq());
    }
    if (CollectionUtils.isNotEmpty(filter.getSorts())) {
      skuFilter.setSorts(filter.getSorts());
    }
    QueryResult<Sku> skuQueryResult = skuService.query(tenant, skuFilter);
    List<DirectorySku> directorySkus = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(skuQueryResult.getRecords())) {
      for (Sku sku : skuQueryResult.getRecords()) {
        DirectorySku directorySku = new DirectorySku();
        directorySku.setDirectoryRequired(skuRequiredMap.get(sku.getId()));
        directorySku.setSkuId(sku.getId());
        directorySku.setCode(sku.getCode());
        directorySku.setName(sku.getName());
        directorySku.setQpc(sku.getQpc());
        directorySku.setUnit(sku.getUnit());
        directorySku.setChannelRequired(sku.getRequired());
        directorySku.setGid(sku.getGoodsGid());
        directorySkus.add(directorySku);
      }
      result.setRecords(directorySkus);
      result.setPageCount(skuQueryResult.getPageCount());
      result.setRecordCount(skuQueryResult.getRecordCount());
    }
    return result;
  }

  @Override
  public void addSku(String tenant, String orgId, String uuid, List<String> skuIds, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notEmpty(skuIds, "skuIds");
    Assert.notNull(operateInfo, "operateInfo");

    MasRequest<List<CatSKUCreation>> request = new MasRequest();
    try {
      List<CatSKUCreation> creations = new ArrayList<>();
      for (String skuId : skuIds) {
        CatSKUCreation c = new CatSKUCreation();
        c.setCatId(uuid);
        c.setSkuId(skuId);
        creations.add(c);
        request.setData(creations);
      }
      MasResponse<Void> response = skuCatClient.saveSkus(tenant, DEFAULT_ORG_TYPE, orgId, uuid, request,
          operateInfo.getOperator().getFullName());
      if (!response.isSuccess()) {
        throw new BaasException("添加商品失败：{0}", response.getEchoMessage());
      }
    } catch (Exception e) {
      log.error("添加商品失败:{}", e.getMessage());
      throw new BaasException("资料中台调用失败");
    }
  }

  @Override
  public void deleteSku(String tenant, String orgId, String uuid, List<String> skuIds, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(operateInfo, "operateInfo");
    MasRequest<List<String>> request = new MasRequest();
    request.setData(skuIds);
    try {
      MasResponse<Void> response = skuCatClient.removeSkus(tenant, DEFAULT_ORG_TYPE, orgId, uuid, request,
          operateInfo.getOperator().getFullName());
      if (!response.isSuccess()) {
        throw new BaasException("删除商品失败:{0}", response.getEchoMessage());
      }
    } catch (Exception e) {
      log.error("删除商品失败:{}", e.getMessage());
      throw new BaasException("资料中台调用失败");

    }
  }

  // 设置渠道必定属性
  @Tx
  @Override
  public void selectDirectoryRequired(String tenant, String orgId, String uuid, List<SkuProperty> skuProperties,
      OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(skuProperties, "skuProperties");
    Assert.notNull(operateInfo, "operateInfo");
    for (SkuProperty skuProperty : skuProperties) {
      CatSKUMod catSKUMod = new CatSKUMod();
      catSKUMod.setRequired(skuProperty.isSelect());
      try {
        MasResponse masResponse = skuCatClient.modSKU(tenant, DEFAULT_ORG_TYPE, orgId, uuid, skuProperty.getSkuId(), catSKUMod);
        if (!masResponse.isSuccess()) {
          throw new BaasException("修改目录{0}下商品{1}失败：{2}", uuid, skuProperty.getSkuId(), masResponse.getEchoMessage());
        }
      } catch (Exception e) {
        throw new BaasException("资料中台调用失败:{}", e.getMessage());
      }
    }
  }
  private List<Directory>  queryCat(String tenant, CatFilter filter){
    List<Directory> directories = new ArrayList<>();
    MasPageResponse<List<Cat>> query = skuCatClient.query(tenant, filter);
    if (query.isSuccess() && CollectionUtils.isNotEmpty(query.getData())) {
      for (int i = 0; i < query.getData().size(); i++) {
        Cat cat = query.getData().get(i);
        Directory directory = new Directory();
        directory.setUuid(cat.getId());
        directory.setName(cat.getName());
        directory.setOrgId(cat.getOrgId());
        if (i == 0) {
          directory.setDef(true);
        }
        directories.add(directory);
      }
    }
    return directories;
  }
}
