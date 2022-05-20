package com.hd123.baas.sop.service.dao.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.task.AssignableShopTaskCount;

/**
 * @author guyahui
 * @date 2021/4/22 19:16
 */
public class AssignableShopTaskCountMapper implements RowMapper<AssignableShopTaskCount> {
  @Override
  public AssignableShopTaskCount mapRow(ResultSet rs, int rowNum) throws SQLException {
    AssignableShopTaskCount assignableShopTaskCount = new AssignableShopTaskCount();
    assignableShopTaskCount.setUuid(rs.getString(PShopTask.OPERATOR_ID));
    assignableShopTaskCount.setCount(rs.getLong("count"));
    return assignableShopTaskCount;
  }
}
