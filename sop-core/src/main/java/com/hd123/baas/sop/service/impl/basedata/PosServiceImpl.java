package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.Sort;
import com.hd123.baas.sop.service.api.basedata.pos.Pos;
import com.hd123.baas.sop.service.api.basedata.pos.PosFilter;
import com.hd123.baas.sop.service.api.basedata.pos.PosService;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.remote.rsmas.RsSort;
import com.hd123.baas.sop.remote.rsmas.pos.RsPos;
import com.hd123.baas.sop.remote.rsmas.pos.RsPosFilter;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silent
 **/
@Service
public class PosServiceImpl extends BaseServiceImpl implements PosService {
  @Override
  public QueryResult<Pos> query(String tenant, PosFilter filter) throws
    BaasException {
    List<Pos> pos = new ArrayList<>();

    RsPosFilter queryFilter = new RsPosFilter();
    BeanUtils.copyProperties(filter, queryFilter);
    String orgId = queryFilter.getOrgIdEq();
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    queryFilter.setOrgIdEq(orgId);
    if (!CollectionUtils.isEmpty(filter.getSorts())) {
      List<RsSort> sorts = new ArrayList<>();
      for (Sort sort: filter.getSorts()) {
        RsSort rsSort = new RsSort(sort.getSortKey(), sort.isDesc());
        sorts.add(rsSort);
      }
      queryFilter.setSorts(sorts);
    }
    BaasResponse<List<RsPos>> response = covertBaasResponse(getClient().posQuery(tenant, queryFilter));
    if (CollectionUtils.isNotEmpty(response.getData())) {
      for (RsPos source : response.getData()) {
        Pos target = new Pos();
        BeanUtils.copyProperties(source, target);
        if (source.getWarehouse() != null) {
          UCN warehouse = new UCN(source.getWarehouse().getUuid(), source.getWarehouse().getCode(), source.getWarehouse().getName());
          target.setWarehouse(warehouse);
        }
        pos.add(target);
      }
    }

    QueryResult<Pos> result = new QueryResult<Pos>();
    result.setRecords(pos);
    result.setPage(filter.getPage());
    result.setPageSize(filter.getPageSize());
    result.setPageCount(
        filter.getPageSize() == 0 ? 0 : (int) response.getTotal() / filter.getPageSize());
    result.setRecordCount(response.getTotal());
    result.setMore(response.getTotal() > filter.getPageSize() * (filter.getPage()+1));
    return result;
  }

  @Override
  public Pos get(String tenantId,String orgType, String orgId, String id) throws BaasException {
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    BaasResponse<RsPos> response = covertBaasResponse(getClient().posGet(tenantId, orgType, orgId, id));
    if (response.getData() == null|| !response.isSuccess()) {
      return null;
    }
    Pos pos = new Pos();
    RsPos rsPos = response.getData();
    BeanUtils.copyProperties(rsPos, pos);
    if (rsPos.getWarehouse() != null) {
      UCN warehouse = new UCN(rsPos.getWarehouse().getUuid(), rsPos.getWarehouse().getCode(), rsPos.getWarehouse().getName());
      pos.setWarehouse(warehouse);
    }

    return pos;
  }
}
