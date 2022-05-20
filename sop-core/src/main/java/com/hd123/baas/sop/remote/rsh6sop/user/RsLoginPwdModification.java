/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	workspace_shop
 * 文件名：	PasswordModification.java
 * 模块说明：
 * 修改历史：
 * 2020年02月11日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author huangjunxian
 * @since 1.0
 */
@Data
@ApiModel(description = "登录密码修改对象")
public class RsLoginPwdModification implements Serializable {
  private static final long serialVersionUID = 3434413403913818181L;
  @ApiModelProperty(value = "登录账号,支持传代码", example = "test", required = true)
  @NotBlank
  private String username;
  @ApiModelProperty(value = "新密码", example = "abc123", required = true)
  @NotBlank
  @Length(max = 32)
  private String newPwd;

}
