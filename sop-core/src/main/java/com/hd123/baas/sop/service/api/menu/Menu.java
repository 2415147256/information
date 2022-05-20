package com.hd123.baas.sop.service.api.menu;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 * @since 2022-01-23
 */
@Setter
@Getter
public class Menu extends StandardEntity {
  /** 租户id */
  private String tenant;
  /** 编号 */
  private String code;
  /** 前端路径 */
  private String path;
  /** 上级编号 */
  private String upperCode;
  /** 名称 */
  private String title;
  /** 类型 */
  private MenuType type = MenuType.NORMAL;
  /** 图标 */
  private String icon;
  /** 参数 */
  private String parameters;
  /** 顺序 */
  private int sequence;
}
