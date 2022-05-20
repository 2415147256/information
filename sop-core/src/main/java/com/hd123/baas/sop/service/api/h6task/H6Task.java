package com.hd123.baas.sop.service.api.h6task;

import java.util.Date;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Getter
@Setter
public class H6Task extends TenantStandardEntity {

  // 组织ID
  private String orgId;
  // 单号
  private String flowNo;
  // 下发文件地址
  private String fileUrl;
  // 计算日期
  private Date executeDate;
  // 文件生成时间
  private Date occurredTime;
  // 类型
  private H6TaskType type = H6TaskType.PRICE;
  // 状态
  private H6TaskState state = H6TaskState.INIT;
  // 错误内容
  private String errMsg;

  @QueryEntity(H6Task.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = H6Task.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField
    public static final String TYPE = PREFIX + "type";
    @QueryField
    public static final String OCCURRED_TIME = PREFIX + "occurredTime";

  }

}
