package com.hd123.baas.sop.job.timed;

import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang
 */
public interface TimedJobService {

  /**
   * 提交定时任务
   * 
   * @param job
   *          对象
   */
  void submit(TimedJob job) throws BaasException;

  /**
   * 移除定时任务
   * 
   * @param uuid
   *          主键uuid
   */
  void remove(String uuid);

  /**
   * 移除定时任务
   *
   * @param tranId
   *          业务id
   */
  void removeByTranId(String tranId);

  /**
   * 查询记录
   * 
   * @param qd
   */
  QueryResult<TimedJob> query(QueryDefinition qd);

  /**
   * 任务投递到mq异步处理,并删除此任务
   *
   * @param job
   *          对象
   */
  void executeAsync(TimedJob job) throws BaasException;
}
