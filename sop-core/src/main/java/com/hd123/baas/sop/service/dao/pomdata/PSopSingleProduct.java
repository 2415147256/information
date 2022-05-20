package com.hd123.baas.sop.service.dao.pomdata;

import com.hd123.baas.sop.service.api.pomdata.SopSingleProduct;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.spms.manager.dao.bill.PSingleProduct;
import com.hd123.spms.service.bill.SingleProduct;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;

public class PSopSingleProduct extends PSingleProduct {
  @Column(title = "周期促销标识", name = PSopSingleProduct.TIME_CYCLE, fieldClass = Boolean.class)
  public static final String TIME_CYCLE = "timeCycle";

  public static final String[] COLUMNS = ArrayUtils.addAll(
          ArrayUtils.addAll(PSingleProduct.COLUMNS, TIME_CYCLE));

  public static Map<String, Object> toFieldValues(SingleProduct entity) {
    Map<String, Object> fvm = PSingleProduct.toFieldValues(entity);
    if (entity instanceof SopSingleProduct) {
      fvm.put(TIME_CYCLE, ((SopSingleProduct) entity).isTimeCycle());
    }
    return fvm;
  }
}
