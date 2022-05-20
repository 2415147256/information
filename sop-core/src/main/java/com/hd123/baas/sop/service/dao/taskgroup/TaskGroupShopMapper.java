package com.hd123.baas.sop.service.dao.taskgroup;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.taskgroup.TaskGroupShop;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class TaskGroupShopMapper extends PEntity.RowMapper<TaskGroupShop> {
  @Override
  public TaskGroupShop mapRow(ResultSet rs, int rowNum) throws SQLException {
    TaskGroupShop result = new TaskGroupShop();
    super.mapFields(rs, rowNum, result);
    result.setTaskGroup(rs.getString(PTaskGroupShop.TASK_GROUP));
    result.setShop(rs.getString(PTaskGroupShop.SHOP));
    result.setTenant(rs.getString(PTaskGroupShop.TENANT));
    result.setUuid(rs.getString(PTaskGroupShop.UUID));
    result.setTenant(rs.getString(PTaskGroupShop.TENANT));
    return result;
  }
}
