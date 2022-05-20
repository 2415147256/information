package com.hd123.baas.sop.service.dao.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.task.PlanSummary;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class PlanSummaryMapper extends PEntity.RowMapper<PlanSummary> {
  @Override
  public PlanSummary mapRow(ResultSet rs, int i) throws SQLException {
    PlanSummary planSummary = new PlanSummary();
    planSummary.setTenant(rs.getString(PShopTaskSummary.TENANT));
    planSummary.setPlan(rs.getString(PShopTaskSummary.PLAN));
    planSummary.setCode(rs.getString(PShopTaskSummary.PLAN_CODE));
    planSummary.setName(rs.getString(PShopTaskSummary.PLAN_NAME));
    planSummary.setPeriod(rs.getString(PShopTaskSummary.PLAN_PERIOD));
    planSummary.setPeriodCode(rs.getString(PShopTaskSummary.PLAN_PERIOD_CODE));
    planSummary.setStartTime(rs.getTimestamp(PShopTaskSummary.PLAN_START_TIME));
    planSummary.setEndTime(rs.getTimestamp(PShopTaskSummary.PLAN_END_TIME));
    return planSummary;
  }
}
