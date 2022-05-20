package com.hd123.baas.sop.remote.rsmas.category;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RsCategory extends RsMasEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 根路径ID
   */
  public static final String ROOT_ID = "-";
  /**
   * 级联直接子类
   */
  public static final String PART_CHILD = "child";
  /**
   * 级联全部子类
   */
  public static final String PART_CHILDREN = "children";

  @ApiModelProperty("组织类型")
  private String orgType;
  @ApiModelProperty("组织id")
  private String orgId;
  @ApiModelProperty("分类代码")
  private String code;
  @ApiModelProperty("分类名称")
  private String name;
  @ApiModelProperty("上级分类")
  private String upper;
  @ApiModelProperty("分类路径")
  private String path;
  @ApiModelProperty("分类层级")
  private int level;
  @ApiModelProperty("是否启用")
  private Boolean enabled = true;
  @ApiModelProperty("子分类")
  private List<RsCategory> children = new ArrayList<RsCategory>();
}
