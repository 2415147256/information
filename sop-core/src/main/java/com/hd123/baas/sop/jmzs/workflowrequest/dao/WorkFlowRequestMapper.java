package com.hd123.baas.sop.jmzs.workflowrequest.dao;

import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkFlowRequestHolder;
import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkFlowRequestState;
import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkFlowRequestType;
import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkflowRequest;
import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkflowRequestTarget;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.baas.sop.utils.SopUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkFlowRequestMapper extends PWorkFlowRequest.RowMapper<WorkflowRequest> {

  @Override
  public WorkflowRequest mapRow(ResultSet rs, int i) throws SQLException {
    WorkflowRequest target = new WorkflowRequest();
    mapFields(rs, i, target);
    target.setTenant(rs.getString(PWorkFlowRequest.TENANT));
    target.setOrgId(rs.getString(PWorkFlowRequest.ORG_ID));
    target.setCode(rs.getInt(PWorkFlowRequest.CODE));
    target.setState(WorkFlowRequestState.valueOf(rs.getString(PWorkFlowRequest.STATE)));
    target.setType(WorkFlowRequestType.valueOf(rs.getString(PWorkFlowRequest.TYPE)));
    target.setTarget(WorkflowRequestTarget.valueOf(rs.getString(PWorkFlowRequest.TARGET)));
    target.setTargetId(rs.getString(PWorkFlowRequest.TARGET_ID));
    target.setTargetCode(rs.getString(PWorkFlowRequest.TARGET_CODE));
    target.setTargetName(rs.getString(PWorkFlowRequest.TARGET_NAME));

    target.setHolder(WorkFlowRequestHolder.valueOf(rs.getString(PWorkFlowRequest.HOLDER)));
    target.setHolderId(rs.getString(PWorkFlowRequest.HOLDER_ID));
    target.setHolderCode(rs.getString(PWorkFlowRequest.HOLDER_CODE));
    target.setHolderName(rs.getString(PWorkFlowRequest.HOLDER_NAME));
    
    target.setAttachment(JsonUtil.jsonToList(SopUtils.convert(rs.getBlob(PWorkFlowRequest.ATTACHMENT)), String.class));
    target.setTitle(rs.getString(PWorkFlowRequest.TITLE));
    target.setContent(rs.getString(PWorkFlowRequest.CONTENT));

    return target;

  }
}
