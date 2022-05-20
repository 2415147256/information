package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.department.DepartmentConfig;
import com.hd123.baas.sop.service.api.basedata.Sort;
import com.hd123.baas.sop.service.api.basedata.department.Department;
import com.hd123.baas.sop.service.api.basedata.department.DepartmentFilter;
import com.hd123.baas.sop.service.api.basedata.department.DepartmentService;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.remote.rsmas.RsSort;
import com.hd123.baas.sop.remote.rsmas.department.RsDepartment;
import com.hd123.baas.sop.remote.rsmas.department.RsDepartmentFilter;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import com.qianfan123.baas.config.api.entity.ConfigItem;
import com.qianfan123.baas.config.client.remote.ConfigClient;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Silent
 **/
@Service
public class DepartmentServiceImpl extends BaseServiceImpl implements DepartmentService {

  @Autowired
  private ConfigClient client;
  @Value("${sop-service.appId}")
  private String appId;

  @Override
  public QueryResult<Department> query(String tenant, DepartmentFilter filter) throws BaasException {
    Assert.hasText(tenant);
    Assert.notNull(filter);
    List<Department> departments = new ArrayList<>();
    RsDepartmentFilter queryFilter = new RsDepartmentFilter();
    if (filter.getIdIn() != null && filter.getIdIn().size() > 0) {
      queryFilter.setIdIn(filter.getIdIn());
    }
    if (!StringUtil.isNullOrBlank(filter.getOrgIdEq())) {
      String orgId = queryFilter.getOrgIdEq();
      orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
      queryFilter.setOrgIdEq(orgId);
      queryFilter.setOrgIdEq(filter.getOrgIdEq());
    }
    if (!StringUtil.isNullOrBlank(filter.getOrgTypeEq())) {
      queryFilter.setOrgTypeEq(filter.getOrgTypeEq());
    }
    if (!StringUtil.isNullOrBlank(filter.getKeyword())) {
      queryFilter.setKeyword(filter.getKeyword());
    }
    if (!StringUtil.isNullOrBlank(filter.getNameEq())) {
      queryFilter.setNameEq(filter.getNameEq());
    }
    queryFilter.setPage(filter.getPage());
    queryFilter.setPageSize(filter.getPageSize());
    if (CollectionUtils.isNotEmpty(filter.getSorts())) {
      List<RsSort> sorts = new ArrayList<>();
      for (Sort sort : filter.getSorts()) {
        RsSort rsSort = new RsSort();
        rsSort.setSortKey(sort.getSortKey());
        rsSort.setDesc(sort.isDesc());
        sorts.add(rsSort);
      }
      queryFilter.setSorts(sorts);
    }
    // 资料查询部门数据
    BaasResponse<List<RsDepartment>> response = covertBaasResponse(getClient().departmentQuery(tenant, queryFilter));
    if (CollectionUtils.isNotEmpty(response.getData())) {
      for (RsDepartment source : response.getData()) {
        Department target = new Department();
        BeanUtils.copyProperties(source, target);
        departments.add(target);
      }
    }

    QueryResult<Department> result = new QueryResult<Department>();
    result.setRecords(departments);
    result.setPage(filter.getPage());
    result.setPageSize(filter.getPageSize());
    result.setPageCount(filter.getPageSize() == 0 ? 0 : (int) response.getTotal() / filter.getPageSize());
    result.setRecordCount(response.getTotal());
    result.setMore(response.getTotal() > filter.getPageSize() * (filter.getPage() + 1));
    return result;
  }

  @Override
  public String save(String tenant, String orgId, List<Department> departments) throws BaasException {
    Assert.notNull(departments, "部门信息");
    Assert.notNull(orgId,"orgId");
    String retCode = "";
    List<ConfigItem> assignList = new ArrayList<>();
    DepartmentConfig vo = new DepartmentConfig();
    List<String> deptList = new ArrayList<>();
    if (departments.size() > 0) {
      for (Department department : departments) {
        deptList.add(department.getId());
      }
      ConfigItem item = new ConfigItem();
      item.setTenant(tenant);
      item.setAppId(appId);
      String key = buildKey(orgId, departments.get(0).getScope());
      item.setKey(key);
      vo.setScope(departments.get(0).getScope());
      vo.setDepts(deptList);
      item.setValue(JsonUtil.objectToJson(vo));
      assignList.add(item);

      try {
        BaasResponse<Void> result = client.save(assignList);
        retCode = String.valueOf(result.getCode());
      } catch (Exception e) {
        throw new BaasException("调用配置中心，保存信息失败");
      }
    }
    return retCode;
  }
}
