package com.hd123.baas.sop.service.api.basedata.goods;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author Silent
 **/
public interface GoodsService {

  QueryResult<Goods> query(String tenant, GoodsFilter filter) throws BaasException;

}
