/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsPlatformCategory.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月6日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platformcategory;

import java.util.ArrayList;
import java.util.List;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@ApiModel("平台类目")
public class RsPlatformCategory extends RsMasEntity {
  private static final long serialVersionUID = -4132173278822394099L;

  /** 根路径ID */
  public static final String ROOT_ID = "-";

  /** 平台前台类目 */
  public static final String TYPE_FRONT = "front";
  /** 平台后台类目 */
  public static final String TYPE_BACK = "back";

  /** 级联直接子类 */
  public static final String PART_CHILD = "child";
  /** 级联全部子类 */
  public static final String PART_CHILDREN = "children";

  @ApiModelProperty("组织类型")
  private String orgType;
  @ApiModelProperty("组织id")
  private String orgId;
  @ApiModelProperty("平台id")
  private String platformId;
  @ApiModelProperty("类型")
  private String type;
  @ApiModelProperty("代码")
  private String code;
  @ApiModelProperty("名称")
  private String name;
  @ApiModelProperty("上级")
  private String upperId = ROOT_ID;
  @ApiModelProperty("路径")
  private String path;
  @ApiModelProperty("排序值")
  private Integer sort;
  @ApiModelProperty("图片")
  private String image;
  @ApiModelProperty("子类目")
  private List<RsPlatformCategory> children = new ArrayList<RsPlatformCategory>();
  @ApiModelProperty("分类层级")
  private int level;
  @ApiModelProperty(value = "是否启用")
  private Boolean enabled = true;
  @ApiModelProperty(value = "是否显示")
  private Boolean isShow = true;
}
