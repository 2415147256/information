package com.hd123.baas.sop.remote.uas;

import java.util.List;

import com.qianfan123.baas.common.entity.BEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 * @since 1.1.0
 */
@Getter
@Setter
public class BRole extends BEntity {
  @ApiModelProperty(value = "编号", example = "123")
  private String code;
  @ApiModelProperty(value = "名称", example = "仓管人员")
  private String name;
  @ApiModelProperty(value = "备注", example = "123")
  private String remark;
  @ApiModelProperty(value = "权限列表")
  private List<BPermission> permissions;
}
