package com.lzh.serviceshop.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
public class ShopConditionVo {

    @ApiModelProperty(value = "商品一级类别")
    private String productType;

    @ApiModelProperty(value = "商品二级类别")
    @TableField("product_twoType")
    private String productTwotype;

    @ApiModelProperty(value = "商品价格")
    private double productPrice;

    @ApiModelProperty(value = "商品库存")
    private Integer productNum;

}
