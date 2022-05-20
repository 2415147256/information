/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	HEADING SOP AS SERVICE
 * 文件名：	RsSkuFilter.java
 * 模块说明：
 * 修改历史：
 * <p>
 * 2021年1月5日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.options;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel("选项查询条件")
public class RsOptionsFilter extends RsMasFilter {
  @ApiModelProperty(value = "选项名等于")
  private String keyEq;
  @ApiModelProperty(value = "是否已启用等于")
  private Boolean enableEq;
  @ApiModelProperty(value = "是否已删除等于")
  private Boolean deletedEq;
  @ApiModelProperty(value = "所属组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "所属组织ID等于")
  private String orgIdEq;
}
