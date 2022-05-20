/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-cms-api
 * 文件名：	SkuCategoryKey.java
  * 模块说明：	
 * 修改历史：

 * 2019年11月5日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.platshopcategory;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class SkuPlatShopCategoryKey {

  private String skuId;
  private String shopId;
  private List<String> platShopCategoryIds = new ArrayList<String>();

}
