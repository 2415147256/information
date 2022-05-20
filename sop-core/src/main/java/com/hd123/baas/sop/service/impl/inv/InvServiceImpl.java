package com.hd123.baas.sop.service.impl.inv;

import com.hd123.baas.sop.service.api.Inv.InvService;
import com.hd123.baas.sop.service.api.Inv.StoreType;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.inv.AvailableInv;
import com.hd123.baas.sop.remote.rsh6sop.inv.AvailableInvFilter;
import com.hd123.baas.sop.remote.rsh6sop.store.SimpleStore;
import com.hd123.baas.sop.remote.rsh6sop.store.StoreFilter;
import com.hd123.baas.sop.remote.rsh6sop.wrh.SimpleWarehouse;
import com.hd123.baas.sop.remote.rsh6sop.wrh.WarehouseFilter;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author liuhaoxin
 */
@Slf4j
@Service
public class InvServiceImpl implements InvService {

  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  public QueryResult<AvailableInv> invQuery(String tenant, String orgId, QueryRequest req) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(req, "req");

    AvailableInvFilter queryFilter = new AvailableInvFilter();
    if (CollectionUtils.isNotEmpty(req.getFilters())) {
      for (FilterParam filter : req.getFilters()) {
        String property = filter.getProperty();
        Object value = filter.getValue();
        if (AvailableInvFilter.KEYWORD_LIKE.equals(property)) {
          queryFilter.setKeywordLikes(value.toString());
          continue;
        }
        if (AvailableInvFilter.WRH_GID_EQ.equals(property)) {
          queryFilter.setWrhGidEquals(Integer.valueOf(value.toString()));
          continue;
        }
//        if (AvailableInvFilter.ORG_GID_EQ.equals(property)) {
//          String h6OrgId = DefaultOrgIdConvert.toH6DefOrgId(value.toString(), false);
//          if (Objects.nonNull(h6OrgId)) {
//            queryFilter.setOrgGidEquals(Integer.valueOf(h6OrgId));
//          }
//          continue;
//        }
        if (AvailableInvFilter.QTY_NO_EQ.equals(property)) {
          queryFilter.setExcludeZeroTotalCount(Boolean.TRUE.toString().equals(value.toString()));
        }
      }
    }

    queryFilter.setPage(getPage(req.getStart(), req.getLimit()));
    queryFilter.setPageSize(req.getLimit());

    BaasResponse<List<AvailableInv>> response = getClient(tenant).invQuery(tenant, queryFilter);
    if (!response.isSuccess()) {
      log.error("调用h6库存查询接口失败！response={}", JsonUtil.objectToJson(response));
      throw new BaasException("调用h6库存查询接口失败！");
    }
    QueryResult<AvailableInv> result = new QueryResult<>();
    result.setRecords(response.getData());
    result.setMore(response.getMore());
    result.setRecordCount(response.getTotal());
    return result;
  }

  private Integer getPage(Integer start, Integer limit) {
    return new Double(Math.floor(new Double(start) / new Double(limit))).intValue();
  }

  @Override
  public QueryResult<SimpleWarehouse> wrhQuery(String tenant, String orgId, QueryRequest req) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(req, "req");

    WarehouseFilter queryFilter = new WarehouseFilter();
    String h6OrgId = DefaultOrgIdConvert.toH6DefOrgId(orgId, false);
    if (Objects.nonNull(h6OrgId)) {
      queryFilter.setOrgGidEquals(Integer.valueOf(h6OrgId));
    }
    if (CollectionUtils.isNotEmpty(req.getFilters())) {
      for (FilterParam filter : req.getFilters()) {
        String property = filter.getProperty();
        Object value = filter.getValue();
        if (WarehouseFilter.ORG_GID_EQ.equals(property)) {
          h6OrgId = DefaultOrgIdConvert.toH6DefOrgId(value.toString(), false);
          if (Objects.nonNull(h6OrgId)) {
            queryFilter.setOrgGidEquals(Integer.valueOf(h6OrgId));
          }
        }
      }
    }
    BaasResponse<List<SimpleWarehouse>> response = getClient(tenant).wrhQuery(tenant, queryFilter);
    if (!response.isSuccess()) {
      log.error("调用h6仓位查询接口失败！response={}", JsonUtil.objectToJson(response));
      throw new BaasException("调用h6仓位查询接口失败！");
    }
    QueryResult<SimpleWarehouse> result = new QueryResult<>();
    result.setRecords(response.getData());
    result.setMore(response.getMore());
    result.setRecordCount(response.getTotal());
    return result;
  }

  @Override
  public QueryResult<SimpleStore> storeQuery(String tenant, String orgId, StoreType type)
      throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(type, "type");

    StoreFilter queryFilter = new StoreFilter();
    if (StoreType.ORG.equals(type)) {
      queryFilter.setTypeEquals(1);
    } else {
      queryFilter.setTypeEquals(0);
    }
    String h6OrgId = DefaultOrgIdConvert.toH6DefOrgId(orgId, false);
    if (Objects.nonNull(h6OrgId)) {
      queryFilter.setOrgGidEquals(Integer.valueOf(orgId));
    }
    BaasResponse<List<SimpleStore>> response = getClient(tenant).storeQuery(tenant, queryFilter);
    if (!response.isSuccess()) {
      log.error("调用h6组织/门店查询接口失败！response={}", JsonUtil.objectToJson(response));
      throw new BaasException("调用h6组织/门店查询接口失败！");
    }
    QueryResult<SimpleStore> result = new QueryResult<>();
    result.setRecords(response.getData());
    result.setMore(response.getMore());
    result.setRecordCount(response.getTotal());
    return result;
  }

  private RsH6SOPClient getClient(String tenant) throws BaasException {
    return feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
  }

}
