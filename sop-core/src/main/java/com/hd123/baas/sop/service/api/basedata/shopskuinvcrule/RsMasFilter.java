/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-biz
 * 文件名：	MasFilter.java
  * 模块说明：	
 * 修改历史：

 * 2019年8月20日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 
 *
 * @author lsz
 */
@Getter
@Setter
public class RsMasFilter extends RsFilter {
  private static final long serialVersionUID = -8726123425152114879L;

  @ApiModelProperty(value = "UUID等于")
  private String uuidEq;
  @ApiModelProperty(value = "UUID在范围")
  private List<String> uuidIn;
  @ApiModelProperty(value = "ID等于")
  private String idEq;
  @ApiModelProperty(value = "是否已删除等于")
  private Boolean deletedEq = Boolean.FALSE;
  @ApiModelProperty(value = "ID在...范围内")
  private List<String> idIn;

  @ApiModelProperty(value = "创建时间起始于")
  private Date createdGt;
  @ApiModelProperty(value = "创建时间截止于")
  private Date createdLt;

  @ApiModelProperty(value = "最后修改时间起始于")
  private Date modifiedGt;
  @ApiModelProperty(value = "最后修改时间截止于")
  private Date modifiedLt;

}
