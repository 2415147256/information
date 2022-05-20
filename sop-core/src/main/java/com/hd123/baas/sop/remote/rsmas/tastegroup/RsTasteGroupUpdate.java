/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	TasteGroupCreation.java
 * 模块说明：
 * 修改历史：
 * 2020/12/22 - lzy - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.tastegroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lzy
 */
@Getter
@Setter
@ApiModel("口味组更新")
public class RsTasteGroupUpdate implements Serializable {

  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
  @ApiModelProperty(value = "口味")
  private List<RsTaste> tastes = new ArrayList<>();
  @ApiModelProperty(value = "绑定的SKU列表")
  private List<String> bindSKUs = new ArrayList<String>();
  @ApiModelProperty("是否多选")
  private Boolean multiSelect;

}
