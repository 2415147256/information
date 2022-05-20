package com.hd123.baas.sop.remote.rsmas.groupTag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName RsSkuGroupTagSave
 * @Description:
 * @Author zhanzhijie
 * @Date 2022/4/15
 * @Version
 **/
@Getter
@Setter
@ApiModel("修改商品分组标签")
public class RsSkuGroupTagSave implements Serializable {

  private static final long serialVersionUID = 18L;

  @ApiModelProperty(value = "分组名称")
  private String group;
  @ApiModelProperty(value = "标签名称")
  private String name;
}
