package com.hd123.baas.sop.remote.rsmas.area;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("区域库-V2")
public class RsArea extends RsMasEntity {
  /** 根路径ID */
  public static final String ROOT_CODE = "-";
  /** 级联直接子类 */
  public static final String PART_CHILD = "child";
  /** 级联全部子级区域 */
  public static final String PART_CHILDRENS = "childrens";

  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "上级代码/ID")
  private String upper;
  @ApiModelProperty(value = "层级")
  private int level;
  @ApiModelProperty(value = "节点数据")
  private String data;
  @ApiModelProperty(value = "子级地址")
  private List<RsArea> childrens = new ArrayList<RsArea>();
  @ApiModelProperty(value = "说明")
  private String remark;
}
