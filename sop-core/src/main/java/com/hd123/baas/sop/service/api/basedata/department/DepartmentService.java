package com.hd123.baas.sop.service.api.basedata.department;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @author Silent
 **/
public interface DepartmentService {

  QueryResult<Department> query(String tenant, DepartmentFilter filter) throws BaasException;

  String save(String tenant,String orgId, List<Department> departments) throws BaasException;

}
