package com.hd123.baas.sop.service.api.basedata.category;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author Silent
 **/
public interface CategoryService {

  QueryResult<Category> query(String tenant, CategoryFilter filter) throws BaasException;

}
