/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	workspace_shop
 * 文件名：	RsLoginPwdModifyResponse.java
 * 模块说明：
 * 修改历史：
 * 2020年02月11日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author huangjunxian
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "登录密码修改响应对象")
public class RsLoginPwdModifyResponse implements Serializable {
  private static final long serialVersionUID = -1751948056934721834L;

  public static final int CODE_SUCCESS = 2000;
  public static final int CODE_NOT_EXISTS = CODE_SUCCESS + 1;

  @ApiModelProperty(value = "响应结果代码：2000-修改成功，5001-账号不存在", example = "2000",
    required = true)
  private int code;

}
