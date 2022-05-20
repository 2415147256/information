package com.hd123.baas.sop.service.api.basedata.platformcategory;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import java.util.List;

/**
 * @author Silent
 **/
public interface PlatformCategoryService {

  QueryResult<PlatformCategory> query(String tenant, PlatformCategoryFilter filter)
      throws BaasException;

  PlatformCategory get(String tenant, String orgType, String orgId, String id) throws BaasException;

  BaasResponse update(String tenant, String orgType, String orgId, String id, PlatformCategoryUpdate update, String operator)
      throws BaasException;

  BaasResponse batchUpdate(String tenant, String orgType, String orgId, List<PlatformCategoryUpdates> updateList, String operator)
      throws BaasException;

  BaasResponse batchShow(String tenant, String orgType, String orgId, PlatformCategoryIdList idList, String operator)
      throws BaasException;

  BaasResponse batchHide(String tenant, String orgType, String orgId, PlatformCategoryIdList idList, String operator)
      throws BaasException;

  BaasResponse<String> create(String tenant, String orgType, String orgId, PlatformCategoryCreation creation, String operator)
      throws BaasException;

  BaasResponse remove(String tenant, String orgType, String orgId, String id) throws BaasException;

  QueryResult<PlatformCategorySku> platformCategorySkuQuery(String tenant,
      PlatformCategorySkuFilter filter) throws BaasException;

  BaasResponse platformCategorySkuAdd(String tenant, String orgType, String orgId,
      PlatformCategorySkuList platformCategorySkuList, String operator) throws BaasException;

  BaasResponse platformCategorySkuRemove(String tenant, String orgType, String orgId,
      PlatformCategorySkuList platformCategorySkuList, String operator) throws BaasException;
}
