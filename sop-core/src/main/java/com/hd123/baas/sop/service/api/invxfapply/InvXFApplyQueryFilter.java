/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	InvXFApplyQueryFilter.java
 * 模块说明：
 * 修改历史：
 * 2020/10/31 - Leo - 创建。
 */

package com.hd123.baas.sop.service.api.invxfapply;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Leo
 */
@Data
public class InvXFApplyQueryFilter extends AbstractQueryFilter {
  private static final long serialVersionUID = -2200406667826668687L;
  //排序条件定义
  public static final String ORDER_BY_NUM = "num";
  public static final String ORDER_BY_TYPE = "type";
  public static final String ORDER_BY_STAT = "stat";
  public static final String ORDER_BY_RECCNT = "recCnt";
  public static final String ORDER_BY_TOTAL = "total";
  public static final String ORDER_BY_FILDATE = "filDate";
  public static final String ORDER_BY_FILLER = "filler";
  public static final String ORDER_BY_FROMSTORECODE = "fromStoreCode";
  public static final String ORDER_BY_FROMSTORENAME = "fromStoreName";
  public static final String ORDER_BY_TOSTORECODE = "toStoreCode";
  public static final String ORDER_BY_TOSTORENAME = "toStoreName";

  @ApiModelProperty(value = "单号等于", example = "9999202010280001", required = false)
  private String numEq;
  @ApiModelProperty(value = "提交时间（申请日期）大于等于", example = "2020-10-1", required = false)
  private Date submitTimeGreaterOrEq;
  @ApiModelProperty(value = "提交时间（申请日期）小于等于", example = "2020-10-2", required = false)
  private Date submitTimeLessOrEq;
  @ApiModelProperty(value = "调出门店标识等于", example = "1000000", required = false)
  private String fromStoreCodeEq;
  @ApiModelProperty(value = "调入门店标识等于", example = "1000000", required = false)
  private String toStoreCodeEq;
  @ApiModelProperty(value = "状态等于", required = false)
  private Integer statEq;
  @ApiModelProperty(value = "状态在...之中", required = false)
  private List<Integer> statIn = new ArrayList<>();
  @ApiModelProperty(value = "是否获取明细", required = false)
  private boolean fetchLines = false;
}
