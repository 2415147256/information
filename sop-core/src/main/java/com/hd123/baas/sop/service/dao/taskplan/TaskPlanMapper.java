package com.hd123.baas.sop.service.dao.taskplan;

import com.hd123.baas.sop.service.api.taskplan.TaskPlan;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanState;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskPlanMapper extends PStandardEntity.RowMapper<TaskPlan> {
  @Override
  public TaskPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
    TaskPlan result = new TaskPlan();
    super.mapFields(rs, rowNum, result);
    result.setTaskGroup(rs.getString(PTaskPlan.TASK_GROUP));
    result.setTenant(rs.getString(PTaskPlan.TENANT));
    result.setName(rs.getString(PTaskPlan.NAME));
    result.setWordNeeded(rs.getBoolean(PTaskPlan.WORD_NEEDED));
    result.setImageNeeded(rs.getBoolean(PTaskPlan.IMAGE_NEEDED));
    result.setRemindTime(rs.getString(PTaskPlan.REMIND_TIME));
    result.setDescription(rs.getString(PTaskPlan.DESCRIPTION));
    result.setTemplateCls(rs.getString(PTaskPlan.TEMPLATE_CLS));
    result.setPlanTime(rs.getString(PTaskPlan.PLAN_TIME));
    result.setState(TaskPlanState.valueOf(rs.getString(PTaskPlan.STATE)));
    result.setStartDate(rs.getTimestamp(PTaskPlan.START_DATE));
    result.setEndDate(rs.getTimestamp(PTaskPlan.END_DATE));
    result.setSort(rs.getInt(PTaskPlan.SORT));
    result.setCycle(rs.getString(PTaskPlan.CYCLE));
    result.setValidityDays(rs.getInt(PTaskPlan.VALIDITY_DAYS));
    result.setDayOfWeek(rs.getInt(PTaskPlan.DAY_OF_WEEK));
    result.setDayOfMonth(rs.getInt(PTaskPlan.DAY_OF_MONTH));
    result.setDelayDay(rs.getInt(PTaskPlan.DELAY_DAY));
    result.setPublishDate(rs.getString(PTaskPlan.PUBLISH_DATE));
    result.setTaskGroups(rs.getString(PTaskPlan.TASK_GROUPS));
    result.setShops(rs.getString(PTaskPlan.SHOPS));
    result.setCode(rs.getString(PTaskPlan.CODE));
    result.setGenerateMode(rs.getString(PTaskPlan.GENERATE_MODE));
    result.setShopMode(rs.getString(PTaskPlan.SHOP_MODE));
    result.setPublishTaskDate(rs.getDate(PTaskPlan.PUBLISH_TASK_DATE));
    return result;
  }
}
