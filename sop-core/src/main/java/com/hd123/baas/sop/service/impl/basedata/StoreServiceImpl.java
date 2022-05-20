package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.platshopcategory.PlatShopCategory;
import com.hd123.baas.sop.service.api.basedata.pos.Pos;
import com.hd123.baas.sop.service.api.basedata.pos.PosFilter;
import com.hd123.baas.sop.service.api.basedata.pos.PosService;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.service.api.basedata.store.YcShopUpdate;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.remote.rsmas.RsAddress;
import com.hd123.baas.sop.remote.rsmas.RsMasResponse;
import com.hd123.baas.sop.remote.rsmas.follow.RsFollow;
import com.hd123.baas.sop.remote.rsmas.follow.RsFollowFilter;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategory;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategoryFilter;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.baas.sop.remote.rsmas.store.RsStoreFilter;
import com.hd123.baas.sop.utils.RegionUtil;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.spms.commons.bean.Parts;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Silent
 **/
@Service
@Slf4j
public class StoreServiceImpl extends BaseServiceImpl implements StoreService {

  private final double MAX_DISTANCE = 999999999L;
  public static final String POPULAR_SHOP = "popularShop";
  public static final String COLLECTED_SHOP = "collectedShop";
  public static final String DEFAULT_ORG_TYPE = "-";
  public static final String DEFAULT_ORG_ID = "-";
  public static final String DEFAULT_PLATFORM = "-";

  @Autowired
  private PosService posService;

  @Override
  public QueryResult<Store> query(String tenant, StoreFilter filter) throws BaasException {
    Assert.hasText(tenant);
    Assert.notNull(filter);

    RsStoreFilter queryFilter = new RsStoreFilter();
    BeanUtils.copyProperties(filter, queryFilter);
    String orgId = queryFilter.getOrgIdEq();
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    queryFilter.setOrgIdEq(orgId);
    if (!StringUtil.isNullOrBlank(filter.getLocation())) {
      queryFilter.setPageSize(0);
      queryFilter.setPage(0);
    }
    BaasResponse<List<RsStore>> response = covertBaasResponse(getClient().storeQuery(tenant, queryFilter));
    List<Store> storeResult = convertStore(tenant, response.getData());

    if (CollectionUtils.isNotEmpty(storeResult)) {
      //    根据经纬度，按照距离排序
      if (!StringUtil.isNullOrBlank(filter.getLocation())) {
        String longitude = filter.getLocation().split(",")[0];
        String latitude = filter.getLocation().split(",")[1];
        for (Store source : storeResult) {
          convertStoreDistance(source, longitude, latitude);
        }
        if (!StringUtil.isNullOrBlank(longitude) &&
            !StringUtil.isNullOrBlank(latitude)) {
          storeResult.sort((o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));
        }
        if ((filter.getPage() + 1) * filter.getPageSize() > storeResult.size()) {
          storeResult = storeResult.subList(filter.getPage() * filter.getPageSize(), storeResult.size());
        } else {
          storeResult = storeResult.subList(filter.getPage() * filter.getPageSize(), (filter.getPage() + 1) * filter.getPageSize());
        }
      }

      List<String> storeIds = new ArrayList<>();
      for (Store datum : storeResult) {
        storeIds.add(datum.getId());
      }
      Parts parts = new Parts(filter.getFetchParts());
      if (parts.contains(Store.PART_DEFAULT_POS)) {
        fetchDefaultPos(tenant, storeResult);
      }
      if (parts.contains(Store.PART_HAS_PLAT_SHOP_CATEGORY)) {
        fetchHasPlatShopCategory(tenant, storeResult);
      }
      Set<String> populars = null;
      Set<String> collecteds = null;
      if (!StringUtil.isNullOrBlank(filter.getUserIdEq())) {
        populars = buildFollowMap(tenant, POPULAR_SHOP, storeIds, filter.getUserIdEq());
        collecteds = buildFollowMap(tenant, COLLECTED_SHOP, storeIds, filter.getUserIdEq());
      }
      for (Store source : storeResult) {
        convertStoreFollow(source, filter.getUserIdEq(), populars, collecteds);
      }
    }

    QueryResult<Store> result = new QueryResult<Store>();
    result.setRecords(storeResult);
    result.setPage(filter.getPage());
    result.setPageSize(filter.getPageSize());
    result.setPageCount(filter.getPageSize() == 0 ? 0 : (int) response.getTotal() / filter.getPageSize());
    result.setRecordCount(response.getTotal());
    result.setMore(response.getTotal() > filter.getPageSize() * (filter.getPage() + 1));
    return result;
  }

  private void fetchHasPlatShopCategory(String tenant, List<Store> storeResult) {
    if (CollectionUtils.isEmpty(storeResult)) {
      return;
    }
    List<String> storeIds = storeResult.stream().map(Store::getId)
        .collect(Collectors.toList());

    RsPlatShopCategoryFilter rsPlatShopCategoryFilter = new RsPlatShopCategoryFilter();
    rsPlatShopCategoryFilter.setShopIdIn(storeIds);
    rsPlatShopCategoryFilter.setPlatformIdEq(DEFAULT_PLATFORM);
    rsPlatShopCategoryFilter.setDeletedEq(Boolean.FALSE);
    rsPlatShopCategoryFilter.setIdNotIn(Arrays.asList(PlatShopCategory.DEFAULT));
    BaasResponse<List<RsPlatShopCategory>> queryResult = covertBaasResponse(getClient()
        .platShopCategoryQuery(tenant, rsPlatShopCategoryFilter));
    if (CollectionUtils.isEmpty(queryResult.getData())) {
      return;
    }

    Set<String> orgAndShopIds = new HashSet<>();
    for (RsPlatShopCategory datum : queryResult.getData()) {
      orgAndShopIds.add(datum.getOrgType() + "," + datum.getOrgId() + "," + datum.getShopId());
    }
    for (Store store : storeResult) {
      if (orgAndShopIds.contains(store.getOrgType() + "," + store.getOrgId() + "," + store.getId())) {
        store.setHasPlatShopCategory(true);
      }
    }
  }

  private void convertStoreDistance(Store source, String longitude, String latitude) {
    if (source == null) {
      return;
    }
    if (!StringUtil.isNullOrBlank(longitude) &&
        !StringUtil.isNullOrBlank(latitude)) {
      double distance = MAX_DISTANCE;
      if (!StringUtil.isNullOrBlank(source.getLongitude()) &&
          !StringUtil.isNullOrBlank(source.getLatitude())) {
        distance = RegionUtil.calcDistance(// ,
            Double.parseDouble(longitude), //
            Double.parseDouble(latitude), //
            Double.parseDouble(source.getLongitude()),
            Double.parseDouble(source.getLatitude()));
      }
      source.setDistance(new BigDecimal(distance));
    }
  }

  private void convertStoreFollow(Store source, String userIdEq, Set<String> populars, Set<String> collecteds) {
    if (source == null || StringUtil.isNullOrBlank(userIdEq)) {
      return;
    }
    if (populars != null && populars.contains(source.getId())) {
      source.setPopular(Boolean.TRUE);
    } else {
      source.setPopular(Boolean.FALSE);
    }
    if (collecteds != null && collecteds.contains(source.getId())) {
      source.setCollected(Boolean.TRUE);
    } else {
      source.setCollected(Boolean.FALSE);
    }
  }


  private void fetchDefaultPos(String tenant, List<Store> storeResult) throws BaasException {
    if (CollectionUtils.isEmpty(storeResult)) {
      return;
    }

    List<String> storeIds = storeResult.stream().map(Store::getId)
        .collect(Collectors.toList());
    PosFilter posFilter = new PosFilter();
    posFilter.setIsDefaultEq(true);
    posFilter.setStoreIdIn(storeIds);
    List<Pos> posList = posService.query(tenant, posFilter).getRecords();
    if (CollectionUtils.isEmpty(posList)) {
      log.info("平台门店根据门店ID,查询不到任何默认pos机");
      return;
    }
    Map<String, Pos> storeIdKeyPosMap = new HashMap<>();
    for (Pos pos : posList) {
      if (pos.getStore() != null) {
        storeIdKeyPosMap.put(pos.getOrgType() + "," + pos.getOrgId() + "," + pos.getStore().getUuid(), pos);
      }
    }

    for (Store store : storeResult) {
      if (storeIdKeyPosMap.containsKey(store.getOrgType() + "," + store.getOrgId() + "," + store.getId())) {
        Pos pos = storeIdKeyPosMap.get(store.getOrgType() + "," + store.getOrgId() + "," + store.getId());
        store.setDefaultPos(new UCN(pos.getId(), pos.getCode(), pos.getName()));
      }
    }
  }

  private Set<String> buildFollowMap(String tenant, String type, List<String> storeIds, String userId) throws BaasException {
    if (CollectionUtils.isEmpty(storeIds) || StringUtil.isNullOrBlank(userId)) {
      return null;
    }

    RsFollowFilter rsFollowFilter = new RsFollowFilter();
    rsFollowFilter.setFollowObjIn(storeIds);
    rsFollowFilter.setFollowTypeEq(type);
    rsFollowFilter.setTenantEq(tenant);
    rsFollowFilter.setUserIdEq(userId);
    BaasResponse<List<RsFollow>> followQuery = covertBaasResponse(
        getClient().followQuery(tenant, rsFollowFilter));
    if (!followQuery.success) {
      log.error("查找{}门店失败,返回信息为{}", rsFollowFilter.getFollowTypeEq(), followQuery.getMsg());
      throw new BaasException(
          "查找收藏类型为" + rsFollowFilter.getFollowTypeEq() + "失败," + followQuery.getMsg());
    }
    if (CollectionUtils.isEmpty(followQuery.getData())) {
      return null;
    }

    Set<String> follows = new HashSet<>();
    for (RsFollow datum : followQuery.getData()) {
      if (datum.getFollowObj() != null) {
        follows.add(datum.getFollowObj().toString());
      }
    }
    return follows;
  }

  @Override
  public Store get(String tenant, String orgType, String orgId, String id, String userId) throws BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    Assert.assertArgumentNotNull(id, "id");

    BaasResponse<RsStore> response = covertBaasResponse(
        getClient().storeGet(tenant, orgType, orgId, id));

    if (!response.isSuccess() || response.getData() == null) {
      return null;
    }

    List<Store> storeResult = convertStore(tenant, Collections.singletonList(response.getData()));
    Store store = storeResult.get(0);
    Set<String> populars = null;
    Set<String> collecteds = null;
    if (!StringUtil.isNullOrBlank(userId)) {
      String storeId = store.getId();
      populars = buildFollowMap(tenant, POPULAR_SHOP, Arrays.asList(storeId), userId);
      collecteds = buildFollowMap(tenant, COLLECTED_SHOP, Arrays.asList(storeId), userId);
    }
    convertStoreFollow(store, userId, populars, collecteds);
    fetchDefaultPos(tenant, Arrays.asList(store));
    return store;
  }

  @Override
  public String update(String tenant, String orgType, String orgId, String id, YcShopUpdate update, String operator) throws BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    Assert.assertArgumentNotNull(id, "id");
    Assert.assertArgumentNotNull(update, "update");

    BaasResponse<RsStore> storeResponse = covertBaasResponse(
        getClient().storeGet(tenant, orgType, orgId, id));
    if (!storeResponse.isSuccess() || storeResponse.getData() == null) {
      throw new BaasException("查询不到id为" + id + "对应的门店，或者对应的门店为空," + storeResponse.getMsg());
    }
    RsStore rsStore = storeResponse.getData();
    RsAddress address = rsStore.getAddress();
    address.setLatitude(update.getLatitude());
    address.setLongitude(update.getLongitude());
    rsStore.setAddress(address);
    rsStore.setBusinessHours(update.getBusinessHours());
    RsMasResponse masResponse = getClient()
        .storeUpdate(tenant, orgType, orgId, id, rsStore, buildOperator());
    if (!masResponse.success) {
      throw new BaasException(masResponse.getEchoMessage());
    }
    return id;
  }
}
