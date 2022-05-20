package com.hd123.baas.sop.jmzs.workflowrequest.dao;

import com.hd123.baas.sop.jmzs.workflowrequest.api.WorkflowRequest;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.lang.Assert;

import java.util.HashMap;
import java.util.Map;

public class PWorkFlowRequest extends PStandardEntity {

  public static final String TABLE_NAME = "workflow_request";
  public static final String TABLE_ALIAS = "_workflow_request";


  public static final String ORG_ID="orgId";
  public static final String CODE="code";

  public static final String TENANT = "tenant";
  public static final String STATE = "state";
  public static final String TYPE = "type";
  public static final String TARGET = "target";
  public static final String TARGET_ID = "targetId";
  public static final String TARGET_CODE = "targetCode";
  public static final String TARGET_NAME = "targetName";
  public static final String HOLDER = "holder";
  public static final String HOLDER_ID = "holderId";
  public static final String HOLDER_CODE = "holderCode";
  public static final String HOLDER_NAME = "holderName";
  public static final String TITLE = "title";
  public static final String CONTENT = "content";
  public static final String ATTACHMENT = "attachment";


  public static Map<String, Object> toFieldValues(WorkflowRequest entity) {
    Assert.assertArgumentNotNull(entity, "entity");
    Map<String, Object> fvm = new HashMap<String, Object>(PStandardEntity.toFieldValues(entity));
    putFieldValue(fvm, CODE, entity.getCode());
    putFieldValue(fvm, TENANT, entity.getTenant());
    putFieldValue(fvm, ORG_ID, entity.getOrgId());
    putFieldValue(fvm, STATE, entity.getState().name());
    putFieldValue(fvm, TYPE, entity.getType().name());

    putFieldValue(fvm, TARGET, entity.getTarget().name());
    putFieldValue(fvm, TARGET_NAME, entity.getTargetName());
    putFieldValue(fvm, TARGET_ID, entity.getTargetId());
    putFieldValue(fvm, TARGET_CODE, entity.getTargetCode());
    
    putFieldValue(fvm, HOLDER, entity.getHolder().name());
    putFieldValue(fvm, HOLDER_NAME, entity.getHolderName());
    putFieldValue(fvm, HOLDER_ID, entity.getHolderId());
    putFieldValue(fvm, HOLDER_CODE, entity.getHolderCode());
    
    putFieldValue(fvm, TITLE, entity.getTitle());
    putFieldValue(fvm, CONTENT, entity.getContent());
    putFieldValue(fvm, ATTACHMENT, JsonUtil.objectToJson(entity.getAttachment()));
    return fvm;
  }


  }
