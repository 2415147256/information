package com.hd123.baas.sop.service.api.explosivev2;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 爆品活动计划
 */
@Getter
@Setter
public class ExplosiveV2 extends StandardEntity {

  public static final String PART_LINE = "part_line";
  public static final String PART_SCOPE = "part_scope";

  //租户
  private String tenant;
  //组织Id
  private String orgId;
  //活动计划ID
  private String planId;
  //活动编号
  private String flowNo;
  //名称
  private String name;
  //状态
  private State state = State.INIT;
  //额外信息
  private String ext;
  //开始时间
  private Date startDate;
  //结束时间
  private Date endDate;
  //报名开始时间
  private Date signStartDate;
  //报名结束时间
  private Date signEndDate;
  //爆品活动计划行
  private List<ExplosiveV2Line> lines = new ArrayList<>();

  //爆品活动范围
  private List<ExplosiveScope> scopeLines = new ArrayList<>();

  public enum State {
    //未提交
    INIT,
    //待审核
    SUBMITTED,
    //已驳回
    REFUSED,
    //已审核
    AUDITED,
    //已上架
    ACTIVE,
    //已下架
    CANCELED
  }

  @QueryEntity(ExplosiveV2.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = ExplosiveV2.class.getName() + "::";
    @QueryField
    public static final String UUID = PREFIX + "uuid";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String PLAN_ID = PREFIX + "planId";
    @QueryField
    public static final String FLOW_NO = PREFIX + "flowNo";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField
    public static final String EXT = PREFIX + "ext";
    @QueryField
    public static final String START_DATE = PREFIX + "startDate";
    @QueryField
    public static final String END_DATE = PREFIX + "endDate";
    @QueryField
    public static final String SIGN_START_DATE = PREFIX + "signStartDate";
    @QueryField
    public static final String SIGN_END_DATE = PREFIX + "signEndDate";
    @QueryField
    public static final String CREATED = PREFIX + "createInfo.time";
    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX + "keyword like";
    @QueryOperation
    public static final String EFFECTIVE_DATE_BTW = PREFIX + "effectiveDate btw";
    @QueryOperation
    public static final String SIGN_DATE_BTW = PREFIX + "signDate btw";
    @QueryOperation
    public static final String ACTIVE_DATE_BTW = PREFIX + "activeDate btw";
  }

}