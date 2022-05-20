/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	mas-product-api
 * 文件名：	SkuCollocationGroup.java
 * 模块说明：
 * 修改历史：
 * 2020年12月24日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.collocation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品加料组
 *
 * @author hezhenhui
 */
@Getter
@Setter
@ApiModel("门店商品加料组绑定请求")
public class CollocationGroupShopSkuBind implements Serializable {

  private static final long serialVersionUID = -4529965393644793227L;

  @ApiModelProperty(value = "门店商品加料组绑定请求明细")
  private List<CollocationGroupShopSkuBindLine> lines = new ArrayList<>();

}
