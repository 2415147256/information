package com.hd123.baas.sop.service.api.h6task;

import java.util.Date;
import java.util.List;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/23.
 */
public interface H6TaskService {

  /**
   * 新增一个任务
   *
   * @param tenant
   *          租户
   * @param h6Task
   *          任务
   * @param operateInfo
   *          操作人信息
   * @return 空任务
   */
  String init(String tenant, H6Task h6Task, OperateInfo operateInfo) throws BaasException;

  /**
   * 新增一个任务
   *
   * @param tenant
   *          租户
   * @param h6Task
   *          任务
   * @param operateInfo
   *          操作人信息
   * @return 空任务
   */
  String saveNew(String tenant, H6Task h6Task, OperateInfo operateInfo) throws BaasException;

  /**
   * 查询任务
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          任务id
   * @return 任务详情
   */
  H6Task get(String tenant, String uuid);

  /**
   * 查询任务（加锁）
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          任务id
   * @return 任务
   */
  H6Task getWithLock(String tenant, String uuid);

  /**
   * 根据时间-状态是未完成的
   * 
   * @param tenant
   *          租户
   * @param type
   *          类型
   * @param executeDate
   *          执行时间
   * @return 任务
   */
  List<H6Task> getByDate(String tenant,String orgId, H6TaskType type, Date executeDate);

  /**
   * 生成链接
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          任务id
   * @param url
   *          链接
   * @param operateInfo
   *          操作人
   */
  void fixUrl(String tenant, String uuid, String url, OperateInfo operateInfo) throws BaasException;

  /**
   * 更新状态
   */
  void updateState(String tenant, String uuid, H6TaskState state, OperateInfo operateInfo) throws BaasException;

  /**
   * 自定义查询
   */
  QueryResult<H6Task> query(String tenant, QueryDefinition qd);

  /**
   * 更新状态
   */
  void logError(String tenant, String uuid, String title, Exception ex, OperateInfo operateInfo) throws BaasException;
}
