package com.hd123.baas.sop.service.dao.taskgroup;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.taskgroup.TaskGroup;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

public class TaskGroupMapper extends PStandardEntity.RowMapper<TaskGroup> {

  @Override
  public TaskGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
    TaskGroup result = new TaskGroup();
    super.mapFields(rs, rowNum, result);
    result.setName(rs.getString(PTaskGroup.NAME));
    result.setRemindTime(rs.getString(PTaskGroup.REMIND_TIME));
    result.setType(TaskGroupType.valueOf(rs.getString(PTaskGroup.TYPE)));
    result.setDescription(rs.getString(PTaskGroup.DESCRIPTION));
    result.setTenant(rs.getString(PTaskGroup.TENANT));
    result.setCode(rs.getString(PTaskGroup.CODE));
    result.setState(rs.getString(PTaskGroup.STATE));
    return result;
  }
}
