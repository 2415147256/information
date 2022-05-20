package com.hd123.baas.sop.job.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.job.entity.PTimedJob;
import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang
 **/
public class TimedJobMapper extends PEntity.RowMapper<TimedJob> {
  @Override
  public TimedJob mapRow(ResultSet rs, int i) throws SQLException {
    TimedJob entity = new TimedJob();
    super.mapFields(rs, i, entity);
    entity.setCallbackBeanName(rs.getString(PTimedJob.CALLBACK_BEAN_NAME));
    entity.setInterval(rs.getString(PTimedJob.INTERVAL));
    entity.setExpectedRunTime(rs.getTimestamp(PTimedJob.EXPECTED_RUN_TIME));
    entity.setRunTimes(rs.getInt(PTimedJob.RUN_TIMES));
    entity.setParams(rs.getString(PTimedJob.PARAMS));
    entity.setTranId(rs.getString(PTimedJob.TRAN_ID));
    return entity;
  }
}
