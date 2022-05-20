/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsCategorySkuKey.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月6日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platformcategory;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RsCategorySkuKey {

  private String categoryId;
  private List<String> skuIds = new ArrayList<String>();

}
