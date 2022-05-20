package com.hd123.baas.sop.service.impl.price.priceadjustment.calculate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd123.baas.sop.service.api.price.priceadjustment.PriceGrade;
import com.hd123.rumba.commons.biz.entity.OperateInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/23.
 * 
 *         计算信息上下文
 * 
 */
@Getter
@Setter
public class CalculateContext {

  // 当前所有的价格级
  private List<PriceGrade> grades = new ArrayList<>();
  // key=商品类别。即商品类别下各定位的加价率信息
  private Map<String, List<GroupPositionIncreaseRate>> positionIncreaseRateMap = new HashMap<>();
  // key=商品类别。即商品类别下各价格带的加价率信息
  private Map<String, List<GroupRangeIncreaseRate>> rangeIncreaseRateMap = new HashMap<>();
  // key=商品ID。即商品各价格级加价率信息
  private List<SkuIncreaseRate> skuIncreaseRateList = new ArrayList<>();
  // 操作人信息
  private OperateInfo operateInfo;

}
