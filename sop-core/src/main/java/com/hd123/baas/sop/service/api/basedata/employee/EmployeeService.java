package com.hd123.baas.sop.service.api.basedata.employee;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author Silent
 **/
public interface EmployeeService {

  QueryResult<Employee> query(String tenant, EmployeeFilter filter) throws BaasException;

}
