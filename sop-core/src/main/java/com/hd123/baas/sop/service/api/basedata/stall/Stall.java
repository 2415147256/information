/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 *
 * 项目名：	mas-company-api
 * 文件名：	Stall.java
 * 模块说明：
 * 修改历史：
 * 2020年12月23日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.stall;

import com.hd123.baas.sop.qcy.service.api.sku.SKU;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 出品部门 （档口）
 *
 * @author hezhenhui
 */
@Getter
@Setter
@ApiModel("出品部门")
public class Stall extends StandardEntity {
  private static final long serialVersionUID = -4297593407959354859L;
  /** 级联SKU*/
  public static final String PART_STALL_SKU = "stall_sku";

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "门店")
  public UCN store;
  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "启禁用状态")
  private Boolean enabled;
  @ApiModelProperty(value = "示例Sku")
  private SKU skuExample;
  @ApiModelProperty(value = "是否打印厨打小票")
  private Boolean receiptPrinting;
  @ApiModelProperty(value = "商品id列表")
  private List<String> skuIdList;
}
