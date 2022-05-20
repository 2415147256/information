package com.hd123.baas.sop.service.api.price.shopprice;

import java.util.Collection;
import java.util.List;

import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceGrade;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/13.
 */
public interface ShopPriceGradeService {

  /**
   * 分页查询
   * 
   * @param tenant
   *          租户
   * @param qd
   *          查询条件
   * @return 门店价格级列表数据
   */
  QueryResult<ShopPriceGrade> query(String tenant, QueryDefinition qd);

  /**
   * 指定门店的类别和定位查询当前价格级
   * 
   * @param tenant
   *          租户
   * @param shop
   *          门店
   * @param skuGroup
   *          类别
   * @param skuPosition
   *          定位
   * @return 价格级
   */
  ShopPriceGrade get(String tenant, String shop, String skuGroup, String skuPosition);

  /**
   * 指定门店的类别和定位查询当前价格级
   *
   * @param tenant
   *          租户
   * @param shop
   *          门店
   * @return 价格级
   */
  List<ShopPriceGrade> listByShop(String tenant, String shop);

  /**
   * 有则更新，无则插入（门店+skuGroup+skuPosition唯一）
   * 
   * @param tenant
   *          租户
   * @param grades
   *          价格级
   */
  void batchSave(String tenant, Collection<ShopPriceGrade> grades) throws BaasException;

}
