package com.hd123.baas.sop.remote.uas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BPositionUserShop {
  @ApiModelProperty(value = "岗位代码", required = true, example = "102223")
  private String code;
  @ApiModelProperty(value = "岗位名称", example = "技术岗")
  private String name;
  @ApiModelProperty(value = "用户uuid", example = "3332211")
  private String uuid;
  @ApiModelProperty(value = "登录用户名", required = true, example = "zhangsan")
  private String loginId;
  @ApiModelProperty(value = "用户昵称", example = "张三")
  private String nickName;
  @ApiModelProperty(value = "门店id", example = "1233222")
  private String shopId;
  @ApiModelProperty(value = "门店代码", required = true, example = "102223")
  private String shopCode;
  @ApiModelProperty(value = "门店名称", example = "技术岗")
  private String shopName;
}
