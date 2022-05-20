/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsSkuComboCreation.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月11日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.sku;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@ApiModel
public class RsSkuComboCreation {
  private String skuId;
  private RsSkuInputCode inputCode;
  private BigDecimal quantity;
}
