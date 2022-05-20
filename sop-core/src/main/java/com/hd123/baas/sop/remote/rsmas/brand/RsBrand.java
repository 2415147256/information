package com.hd123.baas.sop.remote.rsmas.brand;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RsBrand extends RsMasEntity {
  private static final long serialVersionUID = 1L;

  /** 根路径ID */
  public static final String ROOT_ID = "-";
  /** 级联直接子品牌 */
  public static final String PART_CHILD = "child";
  /** 级联全部子品牌 */
  public static final String PART_CHILDREN = "children";

  @ApiModelProperty(value = "组织类型", required = true)
  private String orgType;
  @ApiModelProperty(value = "组织id", required = true)
  private String orgId;
  @ApiModelProperty(value = "品牌代码", required = true)
  private String code;
  @ApiModelProperty(value = "品牌名称", required = true)
  private String name;
  @ApiModelProperty("上级品牌")
  private String upper;
  @ApiModelProperty("品牌路径")
  private String path;
  @ApiModelProperty("是否启用")
  private Boolean enabled = true;
  @ApiModelProperty("子品牌")
  private List<RsBrand> children = new ArrayList<RsBrand>();
}
