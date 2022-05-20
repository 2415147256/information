package com.hd123.baas.sop.jmzs.franchise.api;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 加盟商
 */
@Setter
@Getter
public class Franchise extends TenantStandardEntity {

  /**
   * 组织Id
   */
  private String orgId;
  /**
   * 编号
   */
  private String code;
  /**
   * 编号
   */
  private String id;
  /**
   * 名称
   */
  private String name;
  /**
   * 职务
   */
  private String position;
  /**
   * 手机号
   */
  private String mobile;
  /**
   * 创建时间
   */
  private Date createDate;
  /**
   * 状态
   */
  private String status;
  /**
   * 合同信息
   */
  private List<String> contractImages = new ArrayList<>();

  /**
   * 下辖门店
   */
  private List<UCN> shops = new ArrayList<>();
  /**
   * 删除标记
   */
  private Boolean deleted = Boolean.FALSE;
  /**
   * 额外属性
   */
  private String ext;

  @QueryEntity(Franchise.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(Franchise.class);
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String NAME = PREFIX.nameOf("name");
    @QueryField
    public static final String CODE = PREFIX.nameOf("code");
    @QueryField
    public static final String ID = PREFIX.nameOf("id");
    @QueryField
    public static final String DELETED = PREFIX.nameOf("deleted");

    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX.nameOf("keyword like");

  }

}
