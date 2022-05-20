package com.hd123.baas.sop.service.api.faq;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author liuhaoxin
 */
public interface FaqArticleService {
  /**
   * 新增
   * 
   * @param tenant
   *          租户id
   * @param faqArticle
   *          文章信息
   * @return
   */
  String saveNew(String tenant, FaqArticle faqArticle, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除
   *
   * @param tenant
   *          租户id
   * @param articleId
   *          文章id
   */
  void delete(String tenant, String articleId) throws BaasException;

  /**
   * 编辑
   *
   * @param tenant
   *          租户标记
   * @param faqArticle
   *          文章信息
   */
  void saveModify(String tenant, FaqArticle faqArticle, OperateInfo operateInfo) throws BaasException;

  /**
   * 查询文章信息
   *
   * @param tenant
   *          租户编辑
   * @param qd
   *          自定义查询
   * @param fetchParts
   *          分片
   * @return
   */
  QueryResult<FaqArticle> query(String tenant, QueryDefinition qd, OperateInfo operateInfo, String... fetchParts);

  /**
   * 同意文章
   *
   * @param tenant
   *          租户
   * @param articleId
   *          文章id
   * @param operateInfo
   *          操作信息
   */

  void helpful(String tenant, String articleId, OperateInfo operateInfo) throws BaasException;

  /**
   * 同意文章
   *
   * @param tenant
   *          租户
   * @param articleId
   *          文章id
   * @param operateInfo
   *          操作信息
   */
  void unhelpful(String tenant, String articleId, OperateInfo operateInfo) throws BaasException;

  /**
   * 取消点赞
   * 
   * @param tenant
   *          租户
   * @param articleId
   *          文章
   * @param operateInfo
   *          操作人信息
   */
  void cancel(String tenant, String articleId, OperateInfo operateInfo) throws BaasException;

  /**
   * 查询文章详情
   * 
   * @param tenant
   *          租户id
   * @param articleId
   *          文章唯一id
   * @return
   */
  FaqArticle get(String tenant, String articleId,OperateInfo operateInfo) throws BaasException;
}
