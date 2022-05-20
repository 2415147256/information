package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.basedata.goods.Goods;
import com.hd123.baas.sop.service.api.basedata.goods.GoodsFilter;
import com.hd123.baas.sop.service.api.basedata.goods.GoodsService;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.remote.rsmas.goods.RsGoods;
import com.hd123.baas.sop.remote.rsmas.goods.RsGoodsFilter;
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
 * @author lina
 */
@Service
public class GoodsServiceImpl extends BaseServiceImpl implements GoodsService {

  @Override
  public QueryResult<Goods> query(String tenant, GoodsFilter filter) throws BaasException {
    Assert.hasText(tenant);
    Assert.notNull(filter);

    List<Goods> goodsList = new ArrayList<>();

    RsGoodsFilter queryFilter = new RsGoodsFilter();
    BeanUtils.copyProperties(filter, queryFilter);
    String orgId = queryFilter.getOrgIdEq();
    orgId = DefaultOrgIdConvert.toMasDefOrgId(orgId);
    queryFilter.setOrgIdEq(orgId);
    BaasResponse<List<RsGoods>> response = covertBaasResponse(getClient().goodsQuery(tenant, queryFilter));
    if (CollectionUtils.isNotEmpty(response.getData())) {
      for (RsGoods source : response.getData()) {
        Goods target = new Goods();
        BeanUtils.copyProperties(source, target);
        target.setGoodsGid(source.getId());
        if (source.getCategory() != null) {
          Category category = new Category();
          BeanUtils.copyProperties(source.getCategory(), category);
          category.setUpperId(source.getCategory().getUpper());
          target.setCategory(category);
        }
        goodsList.add(target);
      }
    }

    QueryResult<Goods> result = new QueryResult<Goods>();
    result.setRecords(goodsList);
    result.setPage(filter.getPage());
    result.setPageSize(filter.getPageSize());
    result.setPageCount(
        filter.getPageSize() == 0 ? 0 : (int) response.getTotal() / filter.getPageSize());
    result.setRecordCount(response.getTotal());
    result.setMore(response.getTotal() > filter.getPageSize() * (filter.getPage() + 1));
    return result;
  }

}
