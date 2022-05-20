/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobInstanceDaoImpl.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.job.entity.JobInstanceState;
import com.hd123.baas.sop.job.entity.PJobInstance;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author huzexiong
 *
 */
@Repository
public class JobInstanceDaoImpl implements JobInstanceDao {
  public static final String BEAN_ID = "alphamo.jobInstanceDaoImpl";
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void saveModify(String tenant, PJobInstance jobInstance) throws IllegalArgumentException, BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    SelectBuilder select = new SelectBuilder().from(PJobInstance.TABLE_NAME, null)
        .where(Predicates.equals(PJobInstance.INSTANCE_ID, jobInstance.getInstanceId()));
    List<PJobInstance> list = jdbcTemplate.query(select.build(), new RowMapper<PJobInstance>() {
      @Override
      public PJobInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
        PJobInstance instance = new PJobInstance();
        instance.setInstanceId(rs.getString(PJobInstance.INSTANCE_ID));
        instance.setState(JobInstanceState.valueOf(rs.getString(PJobInstance.FSTATE)));
        return instance;
      }
    });
    if (null == list || list.isEmpty()) {
      jdbcTemplate.update(new InsertBuilder().addValues(new String[] {
          PJobInstance.INSTANCE_ID, //
          PJobInstance.FSTATE, //
          PJobInstance.SCHEDULER_NAME, //
          PJobInstance.SCHEDULER_INSTANCE_ID, //
          PJobInstance.JOB_GROUP, //
          PJobInstance.JOB_NAME, //
          PJobInstance.JOB_CLASS_NAME, //
          PJobInstance.JOB_DATA_MAP, //
          PJobInstance.JOB_DESCRIPTION, //
          PJobInstance.TRIGGER_GROUP, //
          PJobInstance.TRIGGER_NAME, //
          PJobInstance.TRIGGER_CLASS_NAME, //
          PJobInstance.CRON_EXPRESSION, //
          PJobInstance.TRIGGER_DATA_MAP, //
          PJobInstance.TRIGGER_START_TIME, //
          PJobInstance.TRIGGER_END_TIME, //
          PJobInstance.TRIGGER_DESCRIPTION, //
          PJobInstance.DATA_MAP, //
          PJobInstance.STARTED_AT //
      }, new Object[] {
          jobInstance.getInstanceId(), //
          jobInstance.getState().name(), //
          jobInstance.getScheduler().getName(), //
          jobInstance.getScheduler().getInstanceId(), //
          jobInstance.getDetail().getGroup(), //
          jobInstance.getDetail().getName(), //
          jobInstance.getDetail().getJobClassName(), //
          jobInstance.getDetail().getDataMap(), //
          jobInstance.getDetail().getDesciption(), //
          jobInstance.getTrigger().getGroup(), //
          jobInstance.getTrigger().getName(), //
          jobInstance.getTrigger().getTriggerClassName(), //
          jobInstance.getTrigger().getCronExpression(), //
          jobInstance.getTrigger().getDataMap(), //
          jobInstance.getTrigger().getStartTime(), //
          jobInstance.getTrigger().getEndTime(), //
          jobInstance.getTrigger().getDescription(), //
          jobInstance.getDataMap(), //
          new Date()//
      }).table(PJobInstance.TABLE_NAME).build());
    } else {
      jdbcTemplate.update(new UpdateBuilder().setValue(PJobInstance.DATA_MAP, jobInstance.getDataMap())//
          .setValue(PJobInstance.FINISHED_AT, jobInstance.getFinishedAt())//
          .setValue(PJobInstance.FSTATE, jobInstance.getState().name())//
          .setValue(PJobInstance.FRESULT, jobInstance.getResult().name())//
          .setValue(PJobInstance.TRIGGER_END_TIME, jobInstance.getTrigger().getEndTime())//
          .table(PJobInstance.TABLE_NAME, null)//
          .where(Predicates.equals(PJobInstance.INSTANCE_ID, jobInstance.getInstanceId()))//
          .build());
    }
  }

  @Override
  public PJobInstance get(String tenant, String excelUuid) throws BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    Assert.assertArgumentNotNull(excelUuid, "excelUuid");

    SelectBuilder select = new SelectBuilder().from(PJobInstance.TABLE_NAME)
        .where(Predicates.equals(PJobInstance.JOB_NAME, excelUuid))
        .orderBy(PJobInstance.STARTED_AT, false);
    List<PJobInstance> list = jdbcTemplate.query(select.build(), new JobInstanceMapper());
    if (CollectionUtils.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public PJobInstance getByName(String tenant, String jobName) throws BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    Assert.assertArgumentNotNull(jobName, "jobName");

    SelectBuilder select = new SelectBuilder().from(PJobInstance.TABLE_NAME)
        .where(Predicates.equals(PJobInstance.JOB_NAME, jobName))
        .orderBy(PJobInstance.STARTED_AT, false);
    List<PJobInstance> list = jdbcTemplate.query(select.build(), new JobInstanceMapper());
    if (CollectionUtils.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public PJobInstance getByInstanceId(String tenant, String instanceId) throws BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    Assert.assertArgumentNotNull(instanceId, "instanceId");

    SelectBuilder select = new SelectBuilder().from(PJobInstance.TABLE_NAME)
        .where(Predicates.equals(PJobInstance.INSTANCE_ID, instanceId));
    List<PJobInstance> list = jdbcTemplate.query(select.build(), new JobInstanceMapper());
    if (CollectionUtils.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public void updateDataMap(String tenant, String instanceId, String dataMap) throws BaasException {
    Assert.assertArgumentNotNull(tenant, "tenant");
    jdbcTemplate.update(new UpdateBuilder()//
        .table(PJobInstance.TABLE_NAME)//
        .setValue(PJobInstance.DATA_MAP, dataMap)//
        .where(Predicates.equals(PJobInstance.INSTANCE_ID, instanceId))//
        .build());
  }

}
