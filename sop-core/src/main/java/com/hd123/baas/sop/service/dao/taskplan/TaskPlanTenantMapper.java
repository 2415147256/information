package com.hd123.baas.sop.service.dao.taskplan;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.taskplan.TaskPlan;

/**
 * @author guyahui
 * @date 2021/5/11 21:36
 */
public class TaskPlanTenantMapper implements RowMapper<TaskPlan> {
  @Override
  public TaskPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
    TaskPlan result = new TaskPlan();
    result.setTenant(rs.getString(PTaskPlan.TENANT));
    return result;
  }
}
