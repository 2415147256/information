/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsYcPlatformShopUpdate.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月7日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platformshop;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
public class RsYcPlatformShopUpdate {
  @ApiModelProperty("平台门店营业时间")
  private List<String> businessHour;
}
