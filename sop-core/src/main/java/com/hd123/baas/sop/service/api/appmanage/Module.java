package com.hd123.baas.sop.service.api.appmanage;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Module {
  /**
   * 租户
   */
  private String tenant;
  /**
   * uuid
   */
  private String uuid;
  /**
   * 分类
   */
  private String groupName;
  /**
   * 名称
   */
  private String name;

  @QueryEntity(Module.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = Module.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String GROUP_NAME = PREFIX + "groupName";
  }

}
