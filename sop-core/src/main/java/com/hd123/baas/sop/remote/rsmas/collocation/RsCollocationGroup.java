/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	TasteGroup.java
 * 模块说明：
 * 修改历史：
 * 2020/12/22 - lzy - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.collocation;

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
@ApiModel("搭配组")
public class RsCollocationGroup extends RsMasEntity {

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
  @ApiModelProperty(value = "是否多选", required = true)
  private Boolean isMulti;
  @ApiModelProperty(value = "搭配选项")
  private List<RsCollocation> collocations = new ArrayList<>();

}
