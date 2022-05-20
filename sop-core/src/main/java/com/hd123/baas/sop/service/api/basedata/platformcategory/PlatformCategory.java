/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 *
 * 项目名：	mas-cms-api
 * 文件名：	PlatformCategory.java
  * 模块说明：
 * 修改历史：

 * 2019年9月19日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.platformcategory;

import com.hd123.rumba.commons.biz.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel("平台类目")
public class PlatformCategory extends Entity {
  private static final long serialVersionUID = -8335766689336147715L;

  /** 根路径ID */
  public static final String ROOT_ID = "-";

  /** 平台前台类目 */
  public static final String TYPE_FRONT = "front";

  /** 级联直接子类 */
  public static final String PART_CHILD = "child";
  /** 级联全部子类 */
  public static final String PART_CHILDREN = "children";

  /** 默认平台id */
  public static final String PLATFORM_ID = "-";

  @ApiModelProperty("组织类型")
  private String orgType;
  @ApiModelProperty("组织id")
  private String orgId;
  @ApiModelProperty("平台id")
  private String platformId;
  @ApiModelProperty("类型")
  private String type;
  @ApiModelProperty(value = "id")
  private String id;
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
  private List<PlatformCategory> children = new ArrayList<PlatformCategory>();
  @ApiModelProperty("分类层级")
  private int level;
  @ApiModelProperty(value = "是否启用")
  private Boolean enabled = true;
  @ApiModelProperty(value = "是否显示")
  private Boolean isShow = true;

}
