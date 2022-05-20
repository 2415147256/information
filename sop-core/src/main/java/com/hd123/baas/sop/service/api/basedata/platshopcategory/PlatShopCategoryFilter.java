/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 *
 * 项目名：	mas-cms-api
 * 文件名：	PlatformCategoryFilter.java
  * 模块说明：
 * 修改历史：

 * 2019年9月19日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.platshopcategory;

import com.hd123.baas.sop.service.api.basedata.Filter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel("元初门店类目查询条件")
public class PlatShopCategoryFilter extends Filter {
  private static final long serialVersionUID = 891399348711972039L;

  @ApiModelProperty("名称等于")
  private String nameEq;
  @ApiModelProperty("名称在...范围内")
  private List<String> nameIn;
  @ApiModelProperty(value = "上级类目等于")
  private String upperIdEq;
  @ApiModelProperty(value = "路径起始于")
  private String pathStartWith;
  @ApiModelProperty(value = "ID在...范围内")
  private List<String> idIn;
  @ApiModelProperty(value = "code在...范围内")
  private List<String> codeIn;
  @ApiModelProperty("门店id在...范围")
  private List<String> shopIdIn;
  @ApiModelProperty("是否启用")
  private Boolean enabledEq;
  @ApiModelProperty("是否显示")
  private Boolean isShowEq;
}
