package com.hd123.baas.sop.job.timed;

import com.hd123.baas.sop.job.entity.TimedJob;

/**
 * @author zhengzewang
 */
public interface TimedJobExecutor {
  int SUCCESS = 0, RETRY = 1;

  /**
   * 0-回调成功。1-按interval重新提交任务，并更新已执行次数
   * 
   * @param job
   * @return
   */
  int execute(TimedJob job) throws Exception;

}
