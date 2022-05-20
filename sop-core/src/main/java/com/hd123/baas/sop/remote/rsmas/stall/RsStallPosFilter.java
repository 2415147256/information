/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 
 * 项目名：	mas-company-api
 * 文件名：	ShopFilter.java
 * 模块说明：	
 * 修改历史：
 * 2019年9月4日 - sulin - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.stall;

import com.hd123.baas.sop.remote.rsmas.RsFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@ApiModel("出品部门与收银机关联关系查询条件")
public class RsStallPosFilter extends RsFilter {
  private static final long serialVersionUID = 5199330950743527088L;

  @ApiModelProperty(value = "所属组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "所属组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "门店id等于")
  private String storeIdEq;
  @ApiModelProperty(value = "门店id在...之中")
  private List<String> storeIdIn;
  @ApiModelProperty(value = "收银机id等于")
  private String posIdEq;
  @ApiModelProperty(value = "收银机id在...之中")
  private List<String> posIdIn;
  @ApiModelProperty(value = "出品部门id等于")
  private String stallIdEq;
  @ApiModelProperty(value = "出品部门id在...之中")
  private List<String> stallIdIn;

}
