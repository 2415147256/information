package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.employee.Employee;
import com.hd123.baas.sop.service.api.basedata.employee.EmployeeFilter;
import com.hd123.baas.sop.service.api.basedata.employee.EmployeeService;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.remote.rsmas.employee.RsEmployee;
import com.hd123.baas.sop.remote.rsmas.employee.RsEmployeeFilter;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silent
 **/
@Service
public class EmployeeServiceImpl extends BaseServiceImpl implements EmployeeService {

  @Override
  public QueryResult<Employee> query(String tenant, EmployeeFilter filter) throws BaasException {
    Assert.hasText(tenant);
    Assert.notNull(filter);

    List<Employee> employees = new ArrayList<>();

    RsEmployeeFilter queryFilter = new RsEmployeeFilter();
    BeanUtils.copyProperties(filter, queryFilter);
    String orgId = queryFilter.getOrgIdEq();
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    queryFilter.setOrgIdEq(orgId);
    BaasResponse<List<RsEmployee>> response = covertBaasResponse(getClient().employeeQuery(tenant, queryFilter));
    if (!CollectionUtils.isEmpty(response.getData())) {
      for (RsEmployee source : response.getData()) {
        Employee target = new Employee();
        BeanUtils.copyProperties(source, target);
        employees.add(target);
      }
    }

    QueryResult<Employee> result = new QueryResult<Employee>();
    result.setRecords(employees);
    result.setPage(filter.getPage());
    result.setPageSize(filter.getPageSize());
    result.setPageCount(
        filter.getPageSize() == 0 ? 0 : (int) response.getTotal() / filter.getPageSize());
    result.setRecordCount(response.getTotal());
    result.setMore(response.getTotal() > filter.getPageSize() * (filter.getPage() + 1));
    return result;
  }
}
