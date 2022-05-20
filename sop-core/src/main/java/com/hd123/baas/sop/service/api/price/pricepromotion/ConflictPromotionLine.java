package com.hd123.baas.sop.service.api.price.pricepromotion;

import com.hd123.rumba.commons.biz.entity.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class ConflictPromotionLine extends Entity {
  // 促销单号
  private String promotionFlowNo;
  // 创建日期
  private Date created;
  // 创建人
  private String creatorId;
  // 创建人名称
  private String creatorName;
  // 门店名称
  private String shopName;
  // 门店code
  private String shopCode;
  // 商品名称
  private String skuName;
  // 商品代码
  private String skuCode;
  // 自定义类别
  private String skuGroup;
  // 自定义类别名称
  private String skuGroupName;
  // 促销
  private String type;
  // 促销规则", notes = "如果是指定价格，则传金额，如果是指定公式，则传公式
  private String rule;
  // 起始日期
  private Date effectiveStartDate;
  // 结束日期
  private Date effectiveEndDate;

}
