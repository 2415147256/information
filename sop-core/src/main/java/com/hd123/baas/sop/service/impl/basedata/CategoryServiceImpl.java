package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.basedata.category.CategoryFilter;
import com.hd123.baas.sop.service.api.basedata.category.CategoryService;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.remote.rsmas.category.RsCategory;
import com.hd123.baas.sop.remote.rsmas.category.RsCategoryFilter;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends BaseServiceImpl implements CategoryService {

  protected static final String DEFAULT_ORG_ID = "-";

  @Override
  public QueryResult<Category> query(String tenant, CategoryFilter filter) throws BaasException {
    Assert.hasText(tenant);
    Assert.notNull(filter);

    RsCategoryFilter queryFilter = new RsCategoryFilter();
    BeanUtils.copyProperties(filter, queryFilter);
    queryFilter.setFetchParts(filter.getFetchParts());
//    String orgId = queryFilter.getOrgIdEq();
//    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    //    分类不到组织，默认为 “-”
    queryFilter.setOrgIdEq(DEFAULT_ORG_ID);
    List<String> orgIdIn = queryFilter.getOrgIdIn();
    if (CollectionUtils.isNotEmpty(orgIdIn)) {
      List<String> collect = orgIdIn.stream().map(DefaultOrgIdConvert::toMasDefOrgId).collect(Collectors.toList());
      queryFilter.setOrgIdIn(collect);
    }
    BaasResponse<List<RsCategory>> response = covertBaasResponse(getClient().categoryQuery(tenant, queryFilter));

    List<Category> categories = convert(response.getData());
    decodeFriendlyStr(categories, new HashMap<String, Category>());
    QueryResult<Category> result = new QueryResult<Category>();
    result.setRecords(categories);
    result.setPage(filter.getPage());
    result.setPageSize(filter.getPageSize());
    result.setPageCount(filter.getPageSize() == 0 ? 0 : (int) response.getTotal() / filter.getPageSize());
    result.setRecordCount(response.getTotal());
    result.setMore(response.getTotal() > filter.getPageSize() * (filter.getPage() + 1));
    return result;
  }

  private List<Category> convert(List<RsCategory> rsCategoryList) {
    List<Category> categoryList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(rsCategoryList)) {
      for (RsCategory rsCategory : rsCategoryList) {
        Category category = new Category();
        BeanUtils.copyProperties(rsCategory, category);
        category.setUpperId(rsCategory.getUpper());
        category.setCategories(convert(rsCategory.getChildren()));
        categoryList.add(category);
      }
    }
    return categoryList;
  }

  private void decodeFriendlyStr(List<Category> categories, HashMap<String, Category> map) {
    for (Category category : categories) {
      map.put(category.getId(), category);
      String friendlyStr = MessageFormat.format("{0}[{1}]{2}", category.getName(), category.getCode(),
          decodeFriendlyStr(category.getUpperId(), map));
      category.setFriendlyStr(friendlyStr);
      decodeFriendlyStr(category.getCategories(), map);
    }
  }

  private String decodeFriendlyStr(String id, HashMap<String, Category> map) {
    Category category = map.get(id);
    if (category == null) {
      return "";
    }

    return "/" + category.getName() + decodeFriendlyStr(category.getUpperId(), map);
  }
}
