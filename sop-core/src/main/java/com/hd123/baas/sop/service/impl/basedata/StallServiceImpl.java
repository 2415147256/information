/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-parent 文件名： StallServiceImpl.java 模块说明： 修改历史： 2021年01月04日 - XLT - 创建。
 */
package
    com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.Sort;
import com.hd123.baas.sop.service.api.basedata.stall.PosToStallBind;
import com.hd123.baas.sop.service.api.basedata.stall.Stall;
import com.hd123.baas.sop.service.api.basedata.stall.StallCreation;
import com.hd123.baas.sop.service.api.basedata.stall.StallFilter;
import com.hd123.baas.sop.service.api.basedata.stall.StallModification;
import com.hd123.baas.sop.service.api.basedata.stall.StallService;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.remote.rsmas.RsSort;
import com.hd123.baas.sop.remote.rsmas.stall.RsPosToStallBind;
import com.hd123.baas.sop.remote.rsmas.stall.RsStall;
import com.hd123.baas.sop.remote.rsmas.stall.RsStallCreation;
import com.hd123.baas.sop.remote.rsmas.stall.RsStallFilter;
import com.hd123.baas.sop.remote.rsmas.stall.RsStallModification;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.baas.sop.remote.rsmas.store.RsStoreFilter;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StallServiceImpl extends BaseServiceImpl implements StallService {

  @Override
  public QueryResult<Stall> query(String tenant, StallFilter filter) throws BaasException {
    List<Stall> stalls = new ArrayList<>();

    RsStallFilter queryFilter = new RsStallFilter();
    BeanUtils.copyProperties(filter, queryFilter);
    queryFilter.setKeywordLike(filter.getKeyword());
    queryFilter.setEnabledEq(filter.getEnabledEq());
    String orgId = queryFilter.getOrgIdEq();
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    queryFilter.setOrgIdEq(orgId);
    queryFilter.setOrgTypeEq(filter.getOrgTypeEq());
    if (!CollectionUtils.isEmpty(filter.getSorts())) {
      List<RsSort> sorts = new ArrayList<>();
      for (Sort sort : filter.getSorts()) {
        RsSort rsSort = new RsSort(sort.getSortKey(), sort.isDesc());
        sorts.add(rsSort);
      }
      queryFilter.setSorts(sorts);
    }
    BaasResponse<List<RsStall>> response = covertBaasResponse(getClient().stallQuery(tenant, queryFilter));
    // fetch门店信息
    Map<String, UCN> stallIdStoreUcnMap = fetchUncShop(tenant, filter.getOrgTypeEq(), filter.getOrgIdEq(), response.getData());
    if (CollectionUtils.isNotEmpty(response.getData())) {
      for (RsStall source : response.getData()) {
        String key = source.getId() + "," + source.getOrgId() + "," + source.getOrgType();
        Stall target = new Stall();
        BeanUtils.copyProperties(source, target);
        target.setStore(stallIdStoreUcnMap.getOrDefault(key, new UCN()));
        stalls.add(target);
      }
    }

    QueryResult<Stall> result = new QueryResult<Stall>();
    result.setRecords(stalls);
    result.setPage(filter.getPage());
    result.setPageSize(filter.getPageSize());
    result.setPageCount(
        filter.getPageSize() == 0 ? 0 : (int) response.getTotal() / filter.getPageSize());
    result.setRecordCount(response.getTotal());
    result.setMore(response.getTotal() > filter.getPageSize() * (filter.getPage() + 1));
    return result;
  }

  private Map<String, UCN> fetchUncShop(String tenant, String orgType, String orgId, List<RsStall> response) {
    Map<String, UCN> stallIdStoreUcnMap = new HashMap<>();
    if (CollectionUtils.isEmpty(response)) {
      return stallIdStoreUcnMap;
    }

    List<String> storeIds = new ArrayList<>();
    Map<String, String> stallIdStoreIdMap = new HashMap<>();
    for (RsStall rsStall : response) {
      if (!StringUtil.isNullOrBlank(rsStall.getStoreId())) {
        storeIds.add(rsStall.getStoreId());
        String key = "," + rsStall.getOrgId() + "," + rsStall.getOrgType();
        stallIdStoreIdMap.put(rsStall.getId() + key, rsStall.getStoreId() + key);
      }
    }

    RsStoreFilter filter = new RsStoreFilter();
    filter.setIdIn(storeIds);
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    filter.setOrgIdEq(orgId);
    filter.setOrgTypeEq(orgType);
    BaasResponse<List<RsStore>> storeQuery = covertBaasResponse(getClient().storeQuery(tenant, filter));
    Map<String, UCN> storeMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(storeQuery.getData())) {
      for (RsStore rsStore : storeQuery.getData()) {
        String key = rsStore.getId() + "," + rsStore.getOrgId() + "," + rsStore.getOrgType();
        UCN store = new UCN();
        store.setCode(rsStore.getCode());
        store.setName(rsStore.getName());
        store.setUuid(rsStore.getId());
        storeMap.put(key, store);
      }
    }

    for (String stallId : stallIdStoreIdMap.keySet()) {
      UCN storeUnc = storeMap.getOrDefault(stallIdStoreIdMap.get(stallId), null);
      if (storeUnc != null) {
        stallIdStoreUcnMap.put(stallId, storeUnc);
      }
    }

    return stallIdStoreUcnMap;
  }

  @Override
  public BaasResponse<Void> create(String tenant, String orgType, String orgId, StallCreation creation, String operator)
      throws BaasException {
    Assert.hasText(tenant);
    Assert.hasText(operator);
    Assert.notNull(creation);

    RsStallCreation rsStallCreation = new RsStallCreation();
    BeanUtils.copyProperties(creation, rsStallCreation);
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    BaasResponse response = covertBaasResponse(getClient()
        .stallCreate(tenant, orgType, orgId, rsStallCreation, operator));
    if (!response.success) {
      BaasResponse baasResponse = new BaasResponse();
      baasResponse.setSuccess(false);
      baasResponse.setCode(50001);
      baasResponse.setMsg("新增出品部门异常:" + response.getMsg());
      return baasResponse;
    }
    return response;
  }

  @Override
  public BaasResponse<Void> modify(String tenant, String orgType, String orgId, String id, StallModification modification,
      String operator)
      throws BaasException {
    Assert.hasText(tenant);
    Assert.hasText(id);
    Assert.hasText(operator);
    Assert.notNull(modification);
    RsStallModification rsStallModification = new RsStallModification();
    BeanUtils.copyProperties(modification, rsStallModification);
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    BaasResponse response = covertBaasResponse(getClient()
        .stallUpdate(tenant, orgType, orgId, id, rsStallModification, operator));
    if (!response.success) {
      BaasResponse baasResponse = new BaasResponse();
      baasResponse.setSuccess(false);
      baasResponse.setCode(50001);
      baasResponse.setMsg("更新出品部门异常:" + response.getMsg());
      return baasResponse;
    }
    return BaasResponse.success();
  }

  @Override
  public BaasResponse<Void> bindPosToStall(String tenant, String orgType, String orgId, PosToStallBind posToStallBind)
      throws BaasException {
    Assert.hasText(tenant);
    Assert.notNull(posToStallBind);
    RsPosToStallBind rsPosToStallBind = new RsPosToStallBind();
    BeanUtils.copyProperties(posToStallBind, rsPosToStallBind);
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    BaasResponse response = covertBaasResponse(getClient().bindPosToStall(tenant, orgType, orgId, rsPosToStallBind));
    if (!response.success) {
      BaasResponse baasResponse = new BaasResponse();
      baasResponse.setSuccess(false);
      baasResponse.setCode(50001);
      baasResponse.setMsg("绑定收银机出品部门:" + response.getMsg());
      return baasResponse;
    }
    return BaasResponse.success();
  }

  @Override
  public BaasResponse enable(String tenant, String orgType, String orgId, String id, String operator)
      throws BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    Assert.assertArgumentNotNull(id, "id");
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    return covertBaasResponse(getClient().stallEnable(tenant, orgType, orgId, id, operator));
  }

  @Override
  public BaasResponse disable(String tenant, String orgType, String orgId, String id, String operator)
      throws BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    Assert.assertArgumentNotNull(id, "id");
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    return covertBaasResponse(getClient().stallDisable(tenant, orgType, orgId, id, operator));
  }

}