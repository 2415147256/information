/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	InvXFApplyService.java
 * 模块说明：
 * 修改历史：
 * 2020/11/3 - Leo - 创建。
 */

package com.hd123.baas.sop.service.api.invxfapply;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.QueryRequest;

import java.text.ParseException;
import java.util.List;

/**
 * @author Leo
 */
public interface InvXFApplyService {

  /**
   * 查询
   *
   * @param tenant
   * @param request
   * @return
   */
  QueryResult<InvXFApply> query(String tenant, List<String> orgIds, QueryRequest request)
    throws ParseException, BaasException;

  /**
   * 获取指定单据
   *
   * @param tenant
   * @param num
   * @return
   */
  InvXFApply get(String tenant, String num) throws BaasException;

  /**
   * 拒绝
   *
   * @param tenant
   * @param rejection
   */
  void reject(String tenant, InvXFApplyRejection rejection, OperateInfo operateInfo)
    throws BaasException;

  /**
   * 同意
   *
   * @param tenant
   * @param auditor
   */
  void audit(String tenant, InvXFApplyAuditor auditor, OperateInfo operateInfo)
    throws BaasException;
}
