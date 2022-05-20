package com.hd123.baas.sop.service.dao.taskpoints;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.taskpoints.TaskPointsScoreboard;

public class TaskPointsRankMapper implements RowMapper<TaskPointsScoreboard> {
  @Override
  public TaskPointsScoreboard mapRow(ResultSet rs, int rowNum) throws SQLException {
    TaskPointsScoreboard object = new TaskPointsScoreboard();
    object.setRank(rs.getLong(PTaskPointsRank.RANK));
    return object;
  }
}
