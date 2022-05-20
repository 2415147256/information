package com.hd123.baas.sop.job.dao;

/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * <p>
 * 项目名： alphamo-service
 * <p>
 * 文件名： PurchaseSkuDayMapper.java
 * <p>
 * 模块说明：
 * <p>
 * 修改历史：
 * <p>
 * 2018年04月08日 - yanghaixiao - 创建。
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.job.entity.*;

public class JobInstanceMapper implements RowMapper<PJobInstance> {

  @Override
  public PJobInstance mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
    PJobInstance target = new PJobInstance();
    target.setInstanceId(rs.getString(PJobInstance.INSTANCE_ID));
    target.setFinishedAt(rs.getTimestamp(PJobInstance.FINISHED_AT));
    target.setStartedAt(rs.getTimestamp(PJobInstance.STARTED_AT));
    PJobDetailInfo detailInfo = new PJobDetailInfo();
    detailInfo.setGroup(rs.getString(PJobInstance.JOB_GROUP));
    detailInfo.setName(rs.getString(PJobInstance.JOB_NAME));
    detailInfo.setJobClassName(rs.getString(PJobInstance.JOB_CLASS_NAME));
    detailInfo.setDataMap(rs.getString(PJobInstance.JOB_DATA_MAP));
    detailInfo.setDesciption(rs.getString(PJobInstance.JOB_DESCRIPTION));
    target.setDetail(detailInfo);
    PSchedulerInfo schedulerInfo = new PSchedulerInfo();
    schedulerInfo.setInstanceId(rs.getString(PJobInstance.SCHEDULER_INSTANCE_ID));
    schedulerInfo.setName(rs.getString(PJobInstance.SCHEDULER_NAME));
    target.setScheduler(schedulerInfo);
    PTriggerInfo triggerInfo = new PTriggerInfo();
    triggerInfo.setGroup(rs.getString(PJobInstance.TRIGGER_GROUP));
    triggerInfo.setName(rs.getString(PJobInstance.TRIGGER_NAME));
    triggerInfo.setTriggerClassName(rs.getString(PJobInstance.TRIGGER_CLASS_NAME));
    triggerInfo.setCronExpression(rs.getString(PJobInstance.CRON_EXPRESSION));
    triggerInfo.setDataMap(rs.getString(PJobInstance.TRIGGER_DATA_MAP));
    triggerInfo.setStartTime(rs.getTimestamp(PJobInstance.TRIGGER_START_TIME));
    triggerInfo.setEndTime(rs.getTimestamp(PJobInstance.TRIGGER_END_TIME));
    triggerInfo.setDescription(rs.getString(PJobInstance.TRIGGER_DESCRIPTION));
    target.setTrigger(triggerInfo);
    target.setState(JobInstanceState.valueOf(rs.getString(PJobInstance.FSTATE)));
    Blob json = rs.getBlob(PJobInstance.DATA_MAP);
    String dataMap = null;
    try {
      dataMap = new BufferedReader(new InputStreamReader(json.getBinaryStream())).lines()
          .parallel()
          .collect(Collectors.joining(System.lineSeparator()));
    } catch (Exception e) {
      e.printStackTrace();
      dataMap = "{}";
    }
    target.setDataMap(dataMap);
    if (StringUtils.isNotBlank(rs.getString(PJobInstance.FRESULT))) {
      target.setResult(JobInstanceResult.valueOf(rs.getString(PJobInstance.FRESULT)));
    }
    return target;
  }
}
