package com.hd123.baas.sop.service.api.feedback;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * 质量反馈服务接口
 *
 * @author yu lilin
 * @since 1.0
 */
public interface FeedbackService {
  public static final String BEAN_ID = "sop-api.FeedbackService";

  /**
   * 反馈原因选项名
   */
  public static final String OPT_REASON = "feedbackReason";

  /**
   * 质量反馈模块选项名
   */
  public static final String OPT_FEEDBACK = "feedback";

  /**
   * 导入反馈单的appId
   */
  public static final String IMPORT_APPID = "sop";

  /**
   * 导入反馈单的申请原因
   */
  public static final String IMPORT_APPLY_REASON = "其他";

  /**
   * 导入反馈单的申请备注
   */
  public static final String IMPORT_APPLY_NOTE = "总部反馈";

  public static final String SPLITER = "@_@";
  public static final String DATETIME_FORMAT = "yyyy-MM-dd";

  public static final String OPERATE_TIME = "operateTime";
  public static final String OPERATOR_ID = "operatorId";
  public static final String OPERATOR_NAME = "operatorName";

  /**
   * 查询质量反馈单
   */
  QueryResult<Feedback> query(String tenant, FeedbackFilter filter);

  /**
   * 查询可质量反馈的商品
   */
  QueryResult<FeedbackGdSearchResult> search(String shop, String tenant, FeedbackFilter filter) throws BaasException;

  /**
   * 获取质量反馈单
   *
   * @param uuid
   *     反馈单uuid
   * @return 反馈单
   * @throws BaasException
   *     不存在
   */
  Feedback get(String uuid, String... fetchParts) throws BaasException;

  /**
   * 保存质量反馈单
   */
  String save(Feedback feedback) throws BaasException;

  /**
   * 新建并提交质量申请单
   */
  String createAndSubmit(String tenant, FeedbackCreation creation, OperateInfo operateInfo);

  /**
   * 拒绝质量反馈单
   */
  void reject(String tenant, String appId, FeedbackRejection rejection, OperateInfo operateInfo) throws BaasException;

  /**
   * 同意质量反馈单
   */
  void audit(String tenant, String appId, FeedbackApproval approval, OperateInfo operateInfo) throws BaasException;

  /**
   * 导入异常质量反馈
   */
  String importFeedbacks(String tenant, String orgId, String appId, String path, OperateInfo operateInfo) throws Exception;

  /**
   * 获取原因列表
   */
  List<String> listReasons(String tenant, String orgId, FeedbackReasonType type);

  /**
   * 保存审批原因
   */
  void saveAuditReason(String tenant, String orgId, List<String> reasons, FeedbackReasonType type, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 保存申请原因
   */
  void saveApplyReason(String tenant, String orgId, List<String> reasons, OperateInfo operateInfo);

  /**
   * 批量更新质量反馈单的状态
   */
  void batchUpdateState(String tenant, List<String> billIds, FeedbackState state, OperateInfo operateInfo);

  /**
   * 判断质量反馈单是否存在
   */
  String isExists(String id);

  /**
   * 按分类保存质量反馈
   */
  void saveReasons(String tenant, FeedbackReasonSaver feedbackReasonSaver, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 查询
   */
  List<FeedbackReasonSaver> queryReasons(String tenant, String orgId, String type, String sortCode) throws BaasException;

  /**
   * 确认接口
   */
  void check(String tenant, String billId, OperateInfo operateInfo) throws BaasException;

  /**
   * 更新审批单号spNo
   */
  void addSpNo(String tenant, String billId, String spNo);

  /**
   * 保存ext扩展字段
   */
  void saveExt(String tenant, String billId, FeedbackExt ext);
}
