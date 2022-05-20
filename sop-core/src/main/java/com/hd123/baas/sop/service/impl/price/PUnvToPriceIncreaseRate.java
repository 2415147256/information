package com.hd123.baas.sop.service.impl.price;
/*
 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 
 项目名：	com.hd123.baas.sop.service.impl.price
 文件名：	PUnvToPriceIncreaseRate.java
 模块说明：	
 修改历史：
 2021年03月17日 - wangdanhua - 创建。
 */

import com.hd123.baas.sop.service.api.entity.PUnv;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceGrade;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseRate;
import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;

/**
 * @author wangdanhua
 **/
public class PUnvToPriceIncreaseRate implements Converter<PUnv, PriceIncreaseRate> {
  @Override
  public PriceIncreaseRate convert(PUnv source) throws ConversionException {
    if (source == null) {
      return null;
    }
    PriceIncreaseRate target = new PriceIncreaseRate();
    PriceGrade priceGrade = new PriceGrade();
    priceGrade.setId(source.getUuid());
    priceGrade.setName(source.getName());
    target.setGrade(priceGrade);
    target.setRate(source.getValue());
    return target;
  }
}
