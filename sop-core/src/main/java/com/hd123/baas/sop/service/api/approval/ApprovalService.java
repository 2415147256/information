package com.hd123.baas.sop.service.api.approval;

/**
 * @author W.J.H.7
 */
public interface ApprovalService<S, T extends Approval> {

  /**
   * 提交/申请审批单
   *
   * @param tenant
   *     租户ID
   * @param source
   *     来源单据对象
   * @return 申请审批单单号
   */
  String submit(String tenant, S source) throws Exception;

  /**
   * 提交/申请审批单
   *
   * @param tenant
   *     租户ID
   * @param spNo
   *     申请审批单单号 来源单据对象
   * @return 申请审批单
   */
  T getByNo(String tenant, String spNo) throws Exception;

  /**
   * 提交/申请审批单
   *
   * @param tenant
   *     租户ID
   * @param token
   *     审批回调token
   * @return 申请审批单
   */
  boolean checkToken(String tenant, String token);
}
