/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	TasteGroup.java
 * 模块说明：
 * 修改历史：
 * 2020/12/22 - lzy - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.tastegroup;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzy
 */
@Getter
@Setter
@ApiModel("口味组")
public class RsTasteGroup extends RsMasEntity {


  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
  @ApiModelProperty(value = "口味选项")
  private List<RsTaste> tastes = new ArrayList<>();

  @ApiModelProperty("是否多选")
  private Boolean multiSelect;

}
