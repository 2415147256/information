/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	InvXFApply.java
 * 模块说明：
 * 修改历史：
 * 2020/10/31 - Leo - 创建。
 */

package com.hd123.baas.sop.service.api.invxfapply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Leo
 */
@Data
@ApiModel(description = "门店调拨申请单")
public class InvXFApply implements Serializable {
  private static final long serialVersionUID = -8754148375179582566L;

  @ApiModelProperty(value = "组织ID", example = "1000000", required = true)
  private String orgGid;
  @ApiModelProperty(value = "单号", example = "9999202002110001", required = true)
  @NotBlank
  private String num;
  @ApiModelProperty(value = "状态", example = "0", required = true)
  private Integer stat;
  @ApiModelProperty(value = "调拨类型", example = "increase")
  private InvXFType type;
  @ApiModelProperty(value = "调出门店标识", required = true)
  private String fromStoreUuid;
  @ApiModelProperty(value = "调出门店代码", required = true)
  private String fromStoreCode;
  @ApiModelProperty(value = "调出门店名称", required = true)
  private String fromStoreName;
  @ApiModelProperty(value = "调入门店标识", required = true)
  private String toStoreUuid;
  @ApiModelProperty(value = "调入门店代码", required = true)
  private String toStoreCode;
  @ApiModelProperty(value = "调入门店名称", required = true)
  private String toStoreName;
  @ApiModelProperty(value = "发起门店标识", required = true)
  private String initiatorUuid;
  @ApiModelProperty(value = "发起门店代码", required = true)
  private String initiatorCode;
  @ApiModelProperty(value = "发起门店名称", required = true)
  private String initiatorName;
  @ApiModelProperty(value = "申请金额", example = "10.00", required = true)
  private BigDecimal total;
  @ApiModelProperty(value = "申请税额", example = "2.00", required = true)
  private BigDecimal tax;
  @ApiModelProperty(value = "调拨原因")
  private String reason;
  @ApiModelProperty(value = "备注")
  private String note;
  @ApiModelProperty(value = "填单日期", example = "2020-10-22 22:22:22", required = true)
  private Date filDate;
  @ApiModelProperty(value = "填单人", example = "系统管理员[hdposadmin]", required = true)
  private String filler;
  @ApiModelProperty(value = "最后修改时间", example = "2020-10-22 22:22:22")
  private Date lstupdTime;
  @ApiModelProperty(value = "最后修改人", example = "系统管理员[hdposadmin]")
  private String lastModifyOper;
  @ApiModelProperty(value = "商品种类", example = "3", required = true)
  private int recCnt;
  @ApiModelProperty(value = "商品明细")
  private List<InvXFApplyLine> lines = new ArrayList<>();

}
