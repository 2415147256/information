package com.hd123.baas.sop.service.api.appmanage;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserModule {
  /**
   * 租户
   */
  private String tenant;
  /**
   * uuid
   */
  private String uuid;
  /**
   * 用户id
   */
  private String userId;
  /**
   * 应用id
   */
  private String application;

  /**
   * 排序
   */
  private int sort;

  /**
   * 创建时间
   */
  private Date created;

  /**
   * 创建命名空间
   */
  private String creatorNS;

  /**
   * 创建人id
   */
  private String creatorId;

  /**
   * 创建人
   */
  private String creatorName;

  @QueryEntity(UserModule.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = UserModule.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String USER_ID = PREFIX + "userId";
    @QueryField
    public static final String APPLICATION = PREFIX + "application";
  }

}
