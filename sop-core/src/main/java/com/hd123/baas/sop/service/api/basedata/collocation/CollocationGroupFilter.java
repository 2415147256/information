/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	TasteGroupFilter.java
 * 模块说明：
 * 修改历史：
 * 2020/12/22 - lzy - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.collocation;

import com.hd123.baas.sop.service.api.basedata.Filter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lzy
 */
@Getter
@Setter
@ApiModel("加料组查询条件")
public class CollocationGroupFilter extends Filter {

  @ApiModelProperty(value = "代码等于")
  private String codeEq;
  @ApiModelProperty(value = "代码/名称类似于...")
  private String keywordLike;

}
