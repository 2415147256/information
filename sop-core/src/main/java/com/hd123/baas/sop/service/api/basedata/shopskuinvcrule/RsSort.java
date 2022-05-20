/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-biz
 * 文件名：	Sort.java
  * 模块说明：	
 * 修改历史：

 * 2019年8月20日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 排序
 * 
 * @author lsz
 */
@Getter
@Setter
public class RsSort {

  @ApiModelProperty(value = "排序字段")
  private String sortKey;
  @ApiModelProperty(value = "排序方向")
  private boolean desc;

  public RsSort() {
    super();
  }

  public RsSort(String sortKey, boolean desc) {
    super();
    this.sortKey = sortKey;
    this.desc = desc;
  }

}
