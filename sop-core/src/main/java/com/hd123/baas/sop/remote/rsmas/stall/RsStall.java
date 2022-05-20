package com.hd123.baas.sop.remote.rsmas.stall;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lina
 */
@Data
public class RsStall extends RsMasEntity {

  private static final long serialVersionUID = 3735590422846305651L;

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "启禁用状态")
  private Boolean enabled;
  @ApiModelProperty(value = "商品id列表")
  private List<String> skuIdList;
  @ApiModelProperty(value = "门店ID")
  private String storeId;
  @ApiModelProperty(value = "是否打印厨打小票")
  private Boolean receiptPrinting;
}
