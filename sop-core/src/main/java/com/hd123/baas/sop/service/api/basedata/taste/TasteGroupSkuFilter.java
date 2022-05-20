/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	TasteGroupFilter.java
 * 模块说明：
 * 修改历史：
 * 2020/12/22 - lzy - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.taste;

import com.hd123.baas.sop.service.api.basedata.Filter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lzy
 */
@Getter
@Setter
@ApiModel("加料组关联商品查询条件")
public class TasteGroupSkuFilter extends Filter {

  @ApiModelProperty("加料组id等于")
  private String tasteGroupIdEq;
  @ApiModelProperty("加料组名称类似于")
  private String tasteGroupNameLike;

  @ApiModelProperty("商品SKUID在之中")
  private List<String> skuIdIn;
  @ApiModelProperty("商品SKUID类似于")
  private String skuIdLike;
  @ApiModelProperty("商品SKU名称类似于")
  private String skuNameLike;

  @ApiModelProperty("商品SKU条码类似于")
  private String skuInputcodeLike;

  @ApiModelProperty("商品售卖状态等于")
  private String skuSaleStatusEq;

  @ApiModelProperty("商品出品部门ID等于")
  private String skuStallIdEq;

  @ApiModelProperty("商品后台分类ID等于")
  private String skuCategoryIdEq;

}
