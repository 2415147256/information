package com.hd123.baas.sop.service.dao.h6task;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/23.
 */
public class H6TaskMapper extends PStandardEntity.RowMapper<H6Task> {
  @Override
  public H6Task mapRow(ResultSet rs, int rowNum) throws SQLException {
    H6Task entity = new H6Task();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PH6Task.TENANT));
    entity.setOrgId(rs.getString(PH6Task.ORG_ID));
    entity.setFileUrl(rs.getString(PH6Task.FILE_URL));
    entity.setExecuteDate(rs.getTimestamp(PH6Task.EXECUTE_DATE));
    entity.setOccurredTime(rs.getTimestamp(PH6Task.OCCURRED_TIME));
    entity.setType(H6TaskType.valueOf(rs.getString(PH6Task.TYPE)));
    entity.setState(H6TaskState.valueOf(rs.getString(PH6Task.STATE)));

    entity.setFlowNo(rs.getString(PH6Task.FLOW_NO));
    entity.setErrMsg(SopUtils.convert(rs.getBlob(PH6Task.ERR_MSG)));
    return entity;
  }
}
