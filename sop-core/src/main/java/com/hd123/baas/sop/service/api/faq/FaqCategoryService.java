package com.hd123.baas.sop.service.api.faq;

import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author liuhaoxin
 */
public interface FaqCategoryService {
  /**
   * 新增
   *
   * @param tenant
   *          租户id
   * @param category
   *          分类信息
   * @return
   */
  String saveNew(String tenant, FaqCategory category) throws BaasException;

  /**
   * 删除
   * 
   * @param tenant
   *          租户id
   * @param categoryId
   *          分类id
   */
  void delete(String tenant, String categoryId) throws BaasException;

  /**
   * 编辑
   * 
   * @param tenant
   *          租户标记
   * @param category
   *          分类信息
   */
  void saveModify(String tenant, FaqCategory category) throws BaasException;

  /**
   * 查询分类数据
   * 
   * @param tenant
   *          租户编辑
   * @param qd
   *          自定义查询
   * @param fetchParts
   *          分片
   * @return
   */
  QueryResult<FaqCategory> query(String tenant, QueryDefinition qd, String... fetchParts);
}
