package com.hd123.baas.sop.service.api.task;

import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/3.
 */
public interface ShopTaskService {

  String SHOP_TASK_LOG = "shop_task_log";

  /**
   * 获取指定门店任务组下所有门店任务
   */
  List<ShopTask> getByShopTaskGroupId(String tenant, String shopTaskGroupIds, String... sort);

  /**
   * 完成门店任务
   *
   * @param tenant      租户
   * @param uuid        uuid
   * @param finishAppId 完成设备的appId
   * @param feedback    反馈（json）
   * @param finishInfo  完成人信息
   */
  void finish(String tenant, String uuid, String finishAppId, String feedback, OperateInfo finishInfo)
      throws BaasException;

  /**
   * 批量插入门店任务
   */
  void batchInsert(String tenant, List<ShopTask> shopTasks) throws BaasException;

  /**
   * 删除门店任务
   */
  void delete(String tenant, String uuid);

  /**
   * 根据门店任务组ID和任务计划id获取门店任务
   */
  ShopTask getByShopTaskGroupIdAndTaskPlanId(String tenant, String shopTaskGroupId, String taskPlanId);

  /**
   * 新增门店任务计划
   */
  void batchSaveNew(String tenant, ShopTask shopTask) throws BaasException;

  /**
   * 批量设置根据任务计划生成的未完成门店任务状态为已过期
   *
   * @param tenant  租户
   * @param shop
   * @param groupId 任务组id
   */
  void batchCheckLastOne(String tenant, String shop, String groupId, String lastShopTaskGroupId);

  /**
   * 获取指定的门店任务
   *
   * @param tenant
   * @param uuid
   * @return
   */
  ShopTask get(String tenant, String uuid);

  /**
   * 加锁获取任务
   *
   * @param tenant
   * @param uuid
   * @return
   */
  ShopTask getWithLock(String tenant, String uuid);

  /**
   * 查询门店与当前绑定的任务组在指定日期生成的任务快照
   */
  List<ShopTask> list(String tenant, String shop, TaskGroupType type, String planTime) throws ParseException;

  /**
   * 根据门店任务Id查询log
   */
  List<ShopTaskLog> logList(String tenant, String uuid);

  /**
   * 根据门店任务Id查询log,并根据loginId过滤
   */
  List<ShopTaskLog> logListByLoginId(String tenant, String uuid, String loginId);

  /**
   * 根据qd查询门店任务
   *
   * @param tenant
   * @param qd
   * @param fetchParts
   * @return
   */
  QueryResult<ShopTask> query(String tenant, QueryDefinition qd, String... fetchParts);

  /**
   * 根据qd查询门店任务
   *
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<ShopTask> querySummary(String tenant, QueryDefinition qd);

  /**
   * plan planTime shop 所有任务
   */
  List<ShopTask> list(String tenant, String owner, String... fetchParts);

  /**
   * plan planTime shop 所有任务 只查符合登录ID的
   */
  List<ShopTask> listByLoginId(String tenant, String owner, String loginId, String... fetchParts);

  /**
   *
   */
  void saveTaskLog(String tenant, ShopTaskLog shopTaskLog, OperateInfo operateInfo) throws BaasException;

  /**
   *
   */
  void saveAssignableTaskLog(String tenant, ShopTaskLog shopTaskLog, OperateInfo operateInfo) throws BaasException;

  /**
   * @param tenant
   * @param shopTaskLog
   * @param operateInfo
   */
  void finishShopTaskLog(String tenant, ShopTaskLog shopTaskLog, OperateInfo operateInfo) throws BaasException;

  /**
   * @param tenant
   * @param shopTaskLog
   * @param operateInfo
   */
  void finishAssignableShopTaskLog(String tenant, ShopTaskLog shopTaskLog, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 按计划与门店 创建门店任务
   *
   * @param tenant
   * @param shop
   * @param tasks
   */
  void batchSaveNew(String tenant, String plan, String shop, List<ShopTask> tasks, OperateInfo operateInfo)
      throws BaasException;

  ShopTaskLog getLog(String tenant, String logId);

  /**
   * 门店任务交接创建
   *
   * @param tenant           租户
   * @param shopTaskTransfer 交接信息
   * @return uuid
   */
  String transfer(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException;

  /**
   * 门店任务批量交接创建
   *
   * @param tenant           租户
   * @param shopTaskTransfer 交接信息
   * @return uuid
   */
  String batchTransfer(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException;

  /**
   * 门店任务交接接受
   *
   * @param tenant                租户
   * @param buildShopTaskTransfer 交接信息
   * @return uuid
   */
  void accept(String tenant, ShopTaskTransfer buildShopTaskTransfer, OperateInfo operateInfo) throws BaasException;

  /**
   * 门店任务批量交接接受
   *
   * @param tenant 租户
   * @param uuid   交接记录ID
   */
  void batchAccept(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 门店任务交接拒绝
   *
   * @param tenant           租户
   * @param shopTaskTransfer 交接信息
   * @return uuid
   */
  void refuse(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException;

  /**
   * 门店任务批量交接拒绝
   *
   * @param tenant 租户
   * @param uuid   交接记录ID
   */
  void batchRefuse(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException;

  /**
   * 获取交接详情、门店任务详情
   *
   * @param tenant                     租户
   * @param shopTaskTransferDetailReq 查询条件
   * @return 交接详情、门店任务详情
   */
  ShopTaskTransferDetail getShopTaskTransferDetail(String tenant,
                                                    ShopTaskTransferDetailReq shopTaskTransferDetailReq) throws BaasException;

  /**
   * 获取交接详情、门店任务详情,批量交接时使用
   *
   * @param tenant       租户
   * @param shopTaskId   任务ID
   * @param transferFrom 交接人
   * @param transferTo   交接对象
   * @param type         查询类型（详情有两种方式，交接人查看和交接对象查看，不同的查看对应的数据范围不同）
   * @return 交接详情、门店任务详情
   */
  ShopTaskTransferDetail getShopTaskBatchTransferDetail(String tenant, String shopTaskId, String transferFrom, String transferTo, String type) throws BaasException;

  /**
   * 根据shopTaskLogId 集合查询交接记录列表
   *
   * @param tenant
   * @param shopTaskLogIds
   * @return
   */
  List<ShopTaskTransfer> listByShopTaskLogsIdList(String tenant, List<String> shopTaskLogIds);

  /**
   * 根据提醒时间获取任务列表
   *
   * @param tenant
   * @param remindTime 提醒时间 分钟级别
   * @return
   */
  List<ShopTask> listByRemindTime(String tenant, Date remindTime);

  /**
   * 根据提醒时间获取任务列表
   *
   * @param tenant
   * @param remindTime 提醒时间 分钟级别
   * @return
   */
  List<ShopTask> listByStartTime(String tenant, Date remindTime);

  /**
   * 根据提醒时间获取普通任务列表
   *
   * @param tenant
   * @param remindTime 提醒时间 分钟级别
   * @return
   */
  List<ShopTask> listAssignableByRemindTime(String tenant, Date remindTime);

  /**
   * 取消交接任务
   *
   * @param tenant 租户
   * @param uuid   交接记录ID
   */
  void cancel(String tenant, String uuid) throws BaasException;

  /**
   * 取消过期任务的交接状态
   *
   * @param tenant
   * @param shopTaskIds
   */
  void cancelByShopTaskId(String tenant, List<String> shopTaskIds);

  /**
   * 通过联合主键查询巡检任务列表
   *
   * @param tenant     租户
   * @param plan       计划ID
   * @param planPeriod 计划周期
   * @param shop       门店ID
   * @param operatorId 执行人
   * @return ShopTasks
   */
  List<ShopTask> listByUK(String tenant, String plan, String planPeriod, String shop, String operatorId);

  /**
   * 根据logId取消相关交接记录
   *
   * @param tenant 租户
   * @param logId  logId
   */
  void cancelByLogId(String tenant, String logId);

  /**
   * 批量保存任务计划信息
   *
   * @param tenant         租户
   * @param plan           计划ID
   * @param tasks          任务列表
   * @param sysOperateInfo 操作人信息
   */
  void batchSaveNewAssign(String tenant, String plan, List<ShopTask> tasks, OperateInfo sysOperateInfo);

  /**
   * 根据uuid获取交接记录
   *
   * @param tenant
   * @param uuid
   * @return
   */
  ShopTaskTransfer getByUuid(String tenant, String uuid);

  /**
   * 根据owners获取log记录列表
   *
   * @param tenant 租户
   * @param owners shopTaskIdList
   * @return logList
   */
  List<ShopTaskLog> listByOwners(String tenant, List<String> owners);

  /**
   * 创建基于shopTask的任务交接，普通任务
   *
   * @param tenant                租户
   * @param buildShopTaskTransfer 交接记录
   * @param operateInfo           操作人信息
   * @return
   */
  String transferAssignableShopTask(String tenant, ShopTaskTransfer buildShopTaskTransfer, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 门店任务交接接受,普通任务
   *
   * @param tenant                租户
   * @param buildShopTaskTransfer 交接信息
   * @return uuid
   */
  void acceptAssignableShopTask(String tenant, ShopTaskTransfer buildShopTaskTransfer, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 门店任务交接拒绝
   *
   * @param tenant           租户
   * @param shopTaskTransfer 交接信息
   * @return uuid
   */
  void refuseAssignableShopTask(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 取消交接任务
   *
   * @param tenant 租户
   * @param uuid   交接记录ID
   */
  void cancelAssignableShopTask(String tenant, String uuid) throws BaasException;

  /**
   * 终止指定普通任务
   *
   * @param tenant 租户
   * @param uuid   uuid
   */
  void terminate(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 获取门店列表，用于前端数据筛选
   *
   * @param tenant
   * @param loginId
   * @return
   */
  List<ShopTask> listShop(String tenant, String loginId);

  /**
   * shopTask终止
   *
   * @param tenant      租户
   * @param score       得分
   * @param operateInfo 操作人信息
   */
  void changeShopTaskState(String tenant, String uuid, BigDecimal score, ShopTaskState state, OperateInfo operateInfo);

  /**
   * shopTask终止
   *
   * @param tenant      租户
   * @param operateInfo 操作人信息
   */
  void changeShopTaskState(String tenant, String uuid, ShopTaskState state, OperateInfo operateInfo);

  /**
   * 执行人反馈信息
   *
   * @param tenant      租户
   * @param log      反馈信息
   * @param operateInfo 操作人
   */
  void reply(String tenant, AssignableShopTaskLog log, OperateInfo operateInfo) throws BaasException;

  /**
   * 根据shopIds查询数据
   *
   * @param tenant      租户
   * @param shopIdLists shopID集合
   * @return
   */
  List<ShopTask> listByShopIds(String tenant, ShopTaskState state, List<String> shopIdLists);

  /**
   * 根据shopTaskIds集合获取交接记录集合
   *
   * @param tenant
   * @param shopTaskIds
   * @return
   */
  List<ShopTaskTransfer> listTransferByShopTaskIdList(String tenant, List<String> shopTaskIds);

  /**
   * 根据shopTaskIds集合获取交接记录集合
   *
   * @param tenant
   * @param shopTaskIds
   * @return
   */
  List<ShopTaskTransfer> listBatchTransferByShopTaskIdList(String tenant, List<String> shopTaskIds);

  List<AssignableShopTaskCount> getCountByState(String tenant, List<String> operators, ShopTaskState state);

  /**
   * 普通任务的评价
   *  @param tenant      租户
   * @param log      请求信息
   * @param operateInfo 操作人信息
   */
  void audit(String tenant, AssignableShopTaskLog log, OperateInfo operateInfo) throws BaasException;

  /**
   * @param tenant
   * @param shopTask
   */
  void grabOrder(String tenant, ShopTask shopTask, OperateInfo operateInfo) throws BaasException;

  /**
   * 获取过期的普通任务列表
   *
   * @param tenant 租户
   * @return
   */
  void expireShopTask(String tenant);

  /**
   * 实时统计任务列表
   *
   * @param tenant      租户
   * @param startDate   有效期开始时间
   * @param endDate     有效期结束
   * @param shopKeyWord 店铺关键词
   * @param page        页码
   * @param pageSize    分页大小
   * @return
   */
  public List<AssignableShopTaskSummary> querySummary(String tenant, Date startDate, Date endDate, String shopKeyWord,
                                                      Integer page, Integer pageSize);

  /**
   * 实时统计任务总数
   *
   * @param tenant      租户
   * @param startDate   有效期开始时间
   * @param endDate     有效期结束
   * @param shopKeyWord 店铺关键词
   * @return
   */
  public long querySummaryCount(String tenant, Date startDate, Date endDate, String shopKeyWord);

  /**
   * 查询大于指定完成率的任务数量
   *
   * @param tenant    租户
   * @param startDate 开始时间
   * @param endDate   结束时间
   * @param rate      完成率
   * @return
   */
  long queryCountGreaterRate(String tenant, Date startDate, Date endDate, BigDecimal rate);

  /**
   * 支撑门店巡检结果导出页面的导出明细和按店导出
   *
   * @param tenant      租户
   * @param shopKeyword 门店关键词，按店导出压缩包的时候此处值为shopId
   * @param startDate   计划开始时间的下限
   * @param endDate     计划开始时间的上限
   * @param planKeyword 计划代码或计划名称
   * @return 返回shopTaskLine导出对象
   */
  List<ShopTaskLine> query(String tenant, String shopKeyword, Date startDate, Date endDate, String planKeyword);

  /**
   * 当完成最后一个交接的任务时取消该任务（主题的交接）
   *
   * @param tenant      租户
   * @param shopTaskId  任务ID
   * @param operateInfo 操作人信息
   */
  void checkBatchTransfer(String tenant, String shopTaskId, String logId, OperateInfo operateInfo);

  /**
   * 通过shopTaskId列表获取所有此范围内的属于当前登陆人的任务列表
   *
   * @param tenant      租户
   * @param shopTaskIds 任务ID列表
   * @param operatorId  执行人ID
   * @return
   */
  List<ShopTaskLog> listByShopTaskIdsAndOperatorId(String tenant, List<String> shopTaskIds, String operatorId);

  /**
   * 获取含有交接记录明细的任务列表
   *
   * @param tenant     租户
   * @param plan       计划
   * @param planPeriod 计划周期
   * @param shop       店铺
   * @param operatorId 当前登录人
   * @return
   */
  List<ShopTask> listShopTaskTransferByUK(String tenant, String plan, String planPeriod, String shop, String operatorId);

  /**
   * 获取含有交接记录明细的任务列表
   *
   * @param tenant     租户
   * @param plan       计划
   * @param planPeriod 计划周期
   * @param shop       店铺
   * @param operatorId 当前登录人
   * @return
   */
  List<ShopTaskLog> listShopTaskLogTransferByUK(String tenant, String plan, String planPeriod, String shop, String operatorId);

  /**
   * 根据shopTaskId或shopTaskLogId获取交接记录列表
   *
   * @param id           shopTaskId/shopTaskLogId
   * @param transferType 交接记录类型
   * @return
   */
  List<ShopTaskTransfer> listByShopTaskLogIdOrShopTaskId(String tenant, String id, String operatorId, String transferType);
}
