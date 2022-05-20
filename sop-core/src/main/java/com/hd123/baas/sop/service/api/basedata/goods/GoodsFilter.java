/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	GoodsFilter.java
 * 模块说明：	
 * 修改历史：
 * 2020年11月9日 - hasee - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.goods;

import java.util.List;

import com.hd123.baas.sop.service.api.basedata.Filter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hasee
 *
 */
@Getter
@Setter
@ApiModel(description = "库存商品查询条件")
public class
GoodsFilter extends Filter {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "ID在...范围内")
  private List<String> idIn;
  @ApiModelProperty(value = "所属组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "所属组织ID等于")
  private String orgIdEq;

}
