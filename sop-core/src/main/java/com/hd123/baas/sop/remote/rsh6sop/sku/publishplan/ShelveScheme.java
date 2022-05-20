/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	h6-sop-api
 * 文件名：	ShelveScheme.java
 * 模块说明：	
 * 修改历史：
 * 2021年11月11日 - panzhibin - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.sku.publishplan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author panzhibin
 *
 */
@Data
@ApiModel(description = "上架方案对象")
public class ShelveScheme implements Serializable {

  private static final long serialVersionUID = 8588098269425111991L;

  @NotBlank
  @Length(max = 38)
  @ApiModelProperty(value = "方案编码", example = "20211101", required = true)
  private String schemeNo;
  @NotNull
  @ApiModelProperty(value = "方案类型，0-门店，1-组织间", required = true)
  private Integer type;
  @NotNull
  @ApiModelProperty(value = "范围类型，0-所有单位上架，1-指定单位上架", required = true)
  private Integer scopeType;
  @NotNull
  @ApiModelProperty(value = "开始日期", example = "2020-11-13", required = true)
  private Date beginDate;
  @NotNull
  @ApiModelProperty(value = "截止日期", example = "2020-11-14", required = true)
  private Date endDate;
  @NotBlank
  @Length(max = 38)
  @ApiModelProperty(value = "所属组织", example = "1000000", required = true)
  private String orgGid;
  @NotNull
  @ApiModelProperty(value = "最后修改时间", example = "2021-12-01 12:00:01", required = true)
  private Date lstupdTime;
  @NotNull
  @ApiModelProperty(value = "顺序号，H6按此字段顺序加工", example = "202112021812510001", required = true)
  private Long sequenceNo;

  /** 上架单位明细 */
  @ApiModelProperty(value = "上架单位明细", required = true)
  private List<ShelveSchemeStore> storeDetails = new ArrayList<>();

  /** 商品明细 */
  @NotEmpty
  @ApiModelProperty(value = "商品明细", required = true)
  private List<ShelveSchemeDtl> details = new ArrayList<>();

}
