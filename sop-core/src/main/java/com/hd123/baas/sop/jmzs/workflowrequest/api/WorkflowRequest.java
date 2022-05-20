package com.hd123.baas.sop.jmzs.workflowrequest.api;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请
 */
@Setter
@Getter
public class WorkflowRequest extends TenantStandardEntity {

  /**
   * 组织Id
   */
  private String orgId;
  /**
   * 编号
   */
  private int code;
  /**
   * 标题
   */
  private String title;

  /**
   * 内容
   */
  private String content;
  /**
   * 附件
   */
  private List<String> attachment = new ArrayList<>();
  /**
   * 类型
   */
  private WorkFlowRequestType type = WorkFlowRequestType.MEMBER;
  /**
   * 状态
   */
  private WorkFlowRequestState state = WorkFlowRequestState.INIT;
  /**
   * 目标
   */
  private WorkflowRequestTarget target = WorkflowRequestTarget.SHOP;
  /**
   * 目标ID
   */
  private String targetId;
  /**
   * 目标code
   */
  private String targetCode;
  /**
   * 目标名称
   */
  private String targetName;
  /**
   * 持有人类型
   */
  private WorkFlowRequestHolder holder = WorkFlowRequestHolder.JM;

  /**
   * 持有人Id
   */
  private String holderId;

  /**
   * 持有人code
   */
  private String holderCode;

  /**
   * 持有人名称
   */
  private String holderName;

  @QueryEntity(WorkflowRequest.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(WorkflowRequest.class);
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String TITLE = PREFIX.nameOf("title");
    @QueryField
    public static final String TYPE = PREFIX.nameOf("type");
    @QueryField
    public static final String STATE = PREFIX.nameOf("state");
    @QueryField
    public static final String CODE = PREFIX.nameOf("code");
    @QueryField
    public static final String HOLDER = PREFIX.nameOf("holder");
    @QueryField
    public static final String HOLDER_ID = PREFIX.nameOf("holderId");
    @QueryField
    public static final String HOLDER_CODE = PREFIX.nameOf("holderCode");
    @QueryField
    public static final String HOLDER_NAME = PREFIX.nameOf("holderName");
    @QueryField
    public static final String TARGET = PREFIX.nameOf("target");
    @QueryField
    public static final String TARGET_ID = PREFIX.nameOf("targetId");
    @QueryField
    public static final String TARGET_CODE = PREFIX.nameOf("targetCode");
    @QueryField
    public static final String TARGET_NAME = PREFIX.nameOf("targetName");

  }

}
