package com.hd123.baas.sop.service.api.price;
/*
 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 
 项目名：	com.hd123.baas.sop.service.api.price
 文件名：	PriceAdjustmentLineEdit.java
 模块说明：	
 修改历史：
 2021年03月17日 - wangdanhua - 创建。
 */

import com.hd123.baas.sop.service.api.entity.PUnv;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author wangdanhua
 **/
@Getter
@Setter
public class PriceAdjustmentLineEdit {

  /** 采购价 */
  private BigDecimal inPrice;
  /** 到店价 */
  private BigDecimal basePrice;
  /** 后台加价率 */
  private BigDecimal increaseRate;

  private PUnv firstPriceGrade;
  private PUnv secondPriceGrade;

  private String remark;

}
