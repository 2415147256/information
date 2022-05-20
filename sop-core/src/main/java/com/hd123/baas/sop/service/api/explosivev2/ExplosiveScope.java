package com.hd123.baas.sop.service.api.explosivev2;

import com.hd123.rumba.commons.biz.entity.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * 爆品活动范围
 *
 * @author shenmin
 */
@Getter
@Setter
public class ExplosiveScope extends Entity {
  //租户
  private String tenant;
  //活动ID
  private String owner;
  //活动范围行号
  private Integer lineNo;
  //类型
  private Type optionType;
  //类型对应值的UUID
  private String optionUuid;
  //类型对应值的Code
  private String optionCode;
  //类型对应值的Name
  private String optionName;

  public enum Type {
    //门店
    SHOP,
    //组织
    ORG
  }
}
