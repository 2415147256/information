/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	h6-sop-api
 * 文件名：	ShelveSchemeStore.java
 * 模块说明：	
 * 修改历史：
 * 2021年11月11日 - panzhibin - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.sku.publishplan;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * storeGid 为*表示所有单位都上架<br/>
 * 公司间上架方案：所有指定组织下的组织都上架，范围取视图v_sop_org
 * 门店上架方案：所有指定组织下的普通门店都上架，范围取视图v_sop_store
 * 
 * @author panzhibin
 *
 */
@Data
@ApiModel(description = "上架方案上架单位明细")
public class ShelveSchemeStore implements Serializable {

  private static final long serialVersionUID = 1905059702195911089L;

  @NotBlank
  @Length(max = 38)
  @ApiModelProperty(value = "门店GID", example = "1003711", required = true)
  private String storeGid;

}
