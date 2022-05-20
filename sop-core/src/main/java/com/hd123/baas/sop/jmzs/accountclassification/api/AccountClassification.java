package com.hd123.baas.sop.jmzs.accountclassification.api;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountClassification extends TenantStandardEntity {

  /**
   * 组织Id
   */
  private String orgId;
  /**
   * 编号
   */
  private int code;
  /**
   * 名称
   */
  private String name;
  /**
   * 类型
   */
  private AccountClassificationType type = AccountClassificationType.INCOMINGS;
  /**
   * 状态
   */
  private AccountClassificationState state = AccountClassificationState.DISABLE;

  @QueryEntity(AccountClassification.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(AccountClassification.class);
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String NAME = PREFIX.nameOf("name");
    @QueryField
    public static final String TYPE = PREFIX.nameOf("type");
    @QueryField
    public static final String STATE = PREFIX.nameOf("state");
    @QueryField
    public static final String CODE = PREFIX.nameOf("code");

    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX.nameOf("keyword like");

  }

}
