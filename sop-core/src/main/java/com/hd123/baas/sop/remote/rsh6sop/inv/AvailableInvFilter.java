package com.hd123.baas.sop.remote.rsh6sop.inv;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 可用库存查询对象
 *
 * @author liuhaoxin
 * @date 2021-11-30
 */
@Data
@ApiModel(description = "可用库存查询对象")
public class AvailableInvFilter {
  /** 查询条件定义 */
  public static final String KEYWORD_LIKE = "keyword:%商品名称/代码%";
  public static final String ORG_GID_EQ = "orgGid:=";
  public static final String WRH_GID_EQ = "wrhGid:=";
  public static final String QTY_NO_EQ = "qty!=0";

  @ApiModelProperty(value = " 是否排除总件数为0商品，默认否")
  Boolean excludeZeroTotalCount = false;
  @ApiModelProperty(value = " 商品代码名称类似于")
  String keywordLikes;
  @ApiModelProperty(value = " 组织GID等于")
  Integer orgGidEquals;
  @ApiModelProperty(value = "仓位GID等于")
  private Integer wrhGidEquals;
  @NotNull
  @ApiModelProperty(value = "页码，从0开始", example = "0", required = true)
  private Integer page;
  @NotNull
  @ApiModelProperty(value = "每页条数，默认为20", example = "20", required = true)
  private Integer pageSize;
}
