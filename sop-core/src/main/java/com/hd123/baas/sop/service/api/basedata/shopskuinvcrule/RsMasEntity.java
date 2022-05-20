/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	product-api
 * 文件名：	MasEntity.java
  * 模块说明：	
 * 修改历史：

 * 2019年8月20日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
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
@ApiModel
public class RsMasEntity extends StandardEntity {
  private static final long serialVersionUID = -5517914881723609872L;

  /** 租户 */
  @ApiModelProperty(value = "租户")
  private String tenant;
  /** ID */
  @ApiModelProperty(value = "ID")
  private String id;

}
