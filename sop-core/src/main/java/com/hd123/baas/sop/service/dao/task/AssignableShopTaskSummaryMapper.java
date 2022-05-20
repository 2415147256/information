package com.hd123.baas.sop.service.dao.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.task.AssignableShopTaskSummary;

/**
 * @author guyahui
 * @date 2021/4/22 19:16
 */
public class AssignableShopTaskSummaryMapper implements RowMapper<AssignableShopTaskSummary> {
  @Override
  public AssignableShopTaskSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
    AssignableShopTaskSummary assignableShopTaskSummary = new AssignableShopTaskSummary();
    assignableShopTaskSummary.setShop(rs.getString("shop"));
    assignableShopTaskSummary.setShopCode(rs.getString("shop_code"));
    assignableShopTaskSummary.setShopName(rs.getString("shop_name"));
    assignableShopTaskSummary.setFinished(rs.getLong("finished"));
    assignableShopTaskSummary.setTotal(rs.getLong("total"));
    assignableShopTaskSummary.setScore(rs.getBigDecimal("score"));
    assignableShopTaskSummary.setPoint(rs.getBigDecimal("point"));
    assignableShopTaskSummary.setRate(rs.getBigDecimal("rate"));
    assignableShopTaskSummary.setRank(rs.getLong("rank"));
    return assignableShopTaskSummary;
  }
}
