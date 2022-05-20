package com.hd123.baas.sop.job.bean;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.timedjob.TimeJobEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.timedjob.TimedJobMsg;
import com.hd123.baas.sop.job.dao.TimedJobDao;
import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.baas.sop.job.timed.TimedJobService;
import com.hd123.baas.sop.utils.DateUtil;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

@Service
public class TimedJobServiceImpl implements TimedJobService {

  @Autowired
  private TimedJobDao dao;

  @Autowired
  private EvCallEventPublisher publisher;

  @Tx
  @Override
  public void submit(TimedJob job) throws BaasException {
    dao.delete(job.getUuid());
    if (job.getExpectedRunTime() == null) {
      if (job.getIntervals().length == 0) {
        throw new BaasException("expectedRunTime和interval不能同时为空");
      }
      job.setExpectedRunTime(DateUtil.add(new Date(), Calendar.SECOND, job.getIntervals()[0]));
    }
    dao.insert(job);
  }

  @Tx
  @Override
  public void remove(String uuid) {
    dao.delete(uuid);
  }

  @Tx
  @Override
  public void removeByTranId(String tranId) {
    dao.deleteByTranId(tranId);

  }

  @Override
  public QueryResult<TimedJob> query(QueryDefinition qd) {
    return dao.query(qd);
  }

  @Override
  @Tx
  public void executeAsync(TimedJob job) throws BaasException {
    this.remove(job.getUuid());
    TimedJobMsg msg = new TimedJobMsg();
    msg.setJob(job);
    publisher.publishForNormal(TimeJobEvCallExecutor.TIME_JOB_CREATE_EXECUTOR_ID, msg);
  }
}
