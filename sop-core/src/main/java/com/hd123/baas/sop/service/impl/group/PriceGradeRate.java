package com.hd123.baas.sop.service.impl.group;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class PriceGradeRate {
    //序号
    private int seq;
    //加价率 4位小数
    private BigDecimal increaseRate = BigDecimal.ZERO;
    private BigDecimal mValue;
}
