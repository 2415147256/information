/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 
 * 项目名：	mas-client-api
 * 文件名：	MasRequest.java
 * 模块说明：	
 * 修改历史：
 * 2019年5月9日 - __Silent - 创建。
 */
package com.hd123.baas.sop.remote.rsmas;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author __Silent
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsMasRequest<T> {

  @ApiModelProperty(value = "请求数据")
  private T data;
}