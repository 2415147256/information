package com.hd123.baas.sop.remote.uas;

import java.util.List;

import com.qianfan123.baas.common.entity.BEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 * @since 1.1.0
 **/
@Getter
@Setter
public class BUser extends BEntity {
  @ApiModelProperty(value = "组织ID", example = "*")
  private String orgId;
  @ApiModelProperty(value = "登录用户名", required = true, example = "zhangsan")
  private String loginId;
  @ApiModelProperty(value = "用户昵称", example = "张三")
  private String nickName;
  @ApiModelProperty(value = "用户手机号码", example = "18979305822")
  private String mobile;
  @ApiModelProperty(value = "联系方式", example = "18979305822")
  private String linkMan;
  @ApiModelProperty(value = "状态，ENABLE：启用、DISABLE：停用", required = true, example = "enable")
  private String state;
  @ApiModelProperty(value = "角色列表", required = false, example = "[\"123456\"]")
  private List<BRole> roles;
  @ApiModelProperty(value = "数据授权", required = false)
  private List<BDataPermission> dataPermissions;
  @ApiModelProperty(value = "用户岗位信息", required = false)
  private BUserPosition userPosition;
}
