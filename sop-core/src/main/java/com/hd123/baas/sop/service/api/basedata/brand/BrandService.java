package com.hd123.baas.sop.service.api.basedata.brand;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface BrandService {

  QueryResult<Brand> query(String tenant, BrandFilter filter) throws BaasException;

}
