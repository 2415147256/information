/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： RSGoodsQueryFilter.java
 * 模块说明：
 * 修改历史：
 * 2021年06月04日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.screen;

/**
 * @author huangjunxian
 * @since 1.0
 */

import java.util.List;

import com.hd123.baas.sop.service.api.invxfapply.AbstractQueryFilter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel(description = "商品信息查询过滤器")
@EqualsAndHashCode(callSuper = true)
public class RSGoodsQueryFilter extends AbstractQueryFilter {
  private static final long serialVersionUID = 1592664059355952523L;

  /** 排序字段定义 */
  public static final String SORT_KEY_STORE_CODE = "store.code";
  public static final String SORT_KEY_SORT_CODE = "sort.code";
  public static final String SORT_KEY_GOODS_NAME = "gdName";
  public static final String SORT_KEY_GOODS_CODE = "gdCode";
  public static final String SORT_KEY_MUNIT = "munit";
  public static final String SORT_KEY_QPC = "qpc";
  public static final String SORT_KEY_RTL_PRC = "rtlPrc";

  @ApiModelProperty(value = "门店标识等于", required = false)
  private String storeUuidEquals;
  @ApiModelProperty(value = "类别代码起始于", required = false)
  private String sortCodeStartWith;
  @ApiModelProperty(value = "类别代码in", required = false)
  private List<String> sortCodeIn;
  @ApiModelProperty(value = "商品代码起始于/名称类似于", required = false)
  private String gdCodeNameLike;
  @ApiModelProperty("门店代码或名称类似于")
  private String storeCodeNameLike;
}
