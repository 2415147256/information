/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-parent 文件名：	RsBatchSetSerialNumRequest.java 模块说明： 修改历史： 2021/7/1 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.pos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XLT
 */
@Getter
@Setter
@ApiModel("POS收银机批量设置序号请求")
public class RsBatchSetSerialNumRequest implements Serializable {
  private static final long serialVersionUID = 1150371346086200470L;


  @ApiModelProperty(value = "POS收银机批量设置序号请求明细")
  private List<RsBatchSetSerialNumRequestItem> items = new ArrayList<>();
}