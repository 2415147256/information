package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.brand.Brand;
import com.hd123.baas.sop.service.api.basedata.brand.BrandFilter;
import com.hd123.baas.sop.service.api.basedata.brand.BrandService;
import com.hd123.baas.sop.remote.rsmas.brand.RsBrand;
import com.hd123.baas.sop.remote.rsmas.brand.RsBrandFilter;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
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
public class BrandServiceImpl extends BaseServiceImpl implements BrandService {

  protected static final String DEFAULT_ORG_ID = "-";

  @Override
  public QueryResult<Brand> query(String tenant, BrandFilter filter) throws BaasException {
    Assert.hasText(tenant);
    Assert.notNull(filter);

    List<Brand> brandList = new ArrayList<>();

    RsBrandFilter queryFilter = new RsBrandFilter();
    BeanUtils.copyProperties(filter, queryFilter);
//    String orgId = queryFilter.getOrgIdEq();
//    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    //   品牌不到组织，默认为 “-”
    queryFilter.setOrgIdEq(DEFAULT_ORG_ID);
    BaasResponse<List<RsBrand>> response = covertBaasResponse(getClient().brandQuery(tenant, queryFilter));
    if (CollectionUtils.isNotEmpty(response.getData())) {
      for (RsBrand source : response.getData()) {
        Brand target = new Brand();
        BeanUtils.copyProperties(source, target);
        brandList.add(target);
      }
    }

    QueryResult<Brand> result = new QueryResult<Brand>();
    result.setRecords(brandList);
    result.setPage(filter.getPage());
    result.setPageSize(filter.getPageSize());
    result.setPageCount(
        filter.getPageSize() == 0 ? 0 : (int) response.getTotal() / filter.getPageSize());
    result.setRecordCount(response.getTotal());
    result.setMore(response.getTotal() > filter.getPageSize() * (filter.getPage()+1));
    return result;

  }

}
