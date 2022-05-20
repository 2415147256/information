package com.hd123.baas.sop.service.api.price.priceadjustment;

/**
 * @Author: maodapeng
 * @Date: 2020/12/7 17:12
 */

import lombok.Getter;
import lombok.Setter;

/**
 * 试算商品与竞价商品组合信息
 */
@Setter
@Getter
public class AdjustmentCompetitorLine {
    private PriceAdjustmentLine priceAdjustmentLine;
    private PriceCompetitorLine priceCompetitorLine;
}
