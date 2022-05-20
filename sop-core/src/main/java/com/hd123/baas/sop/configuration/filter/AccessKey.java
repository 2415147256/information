package com.hd123.baas.sop.configuration.filter;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.util.TagUtils.SCOPE_REQUEST;

/**
 * @author zhengzewang on 2020/11/9.
 */
@Component(AccessKey.BEAN_NAME)
@Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
@Getter
@Setter
public class AccessKey {

  public static final String BEAN_NAME = "sop-service.accesskey";

  /** 租户 */
  private String tenant;
  /** 组织 */
  private String orgId;
  /** 门店uuid */
  private String shop;
  /** 应用ID，由tlsp分配 */
  private String appId;
  /** 登录用户id */
  private String userId;
  /** 登录账号 */
  private String loginId;
  /** 登录用户名 */
  private String userName;

  /**加盟商ID*/
  private String franchiseeId;
  /**加盟商CODE*/
  private String franchiseeCode;

  public OperateInfo getOperateInfo() {
    return new OperateInfo(new Operator(loginId, userName));
  }
}
