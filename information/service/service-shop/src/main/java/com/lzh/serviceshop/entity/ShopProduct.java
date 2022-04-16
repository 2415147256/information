package com.lzh.serviceshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author lzh
 * @since 2022-04-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ShopProduct对象", description="")
public class ShopProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品价格")
    private Double productPrice;

    @ApiModelProperty(value = "商品库存")
    private Integer productNum;

    @ApiModelProperty(value = "商品图片")
    private String productImag;

    @ApiModelProperty(value = "商品一级类别")
    private String productType;

    @ApiModelProperty(value = "商品的卖家的id")
    @TableField("product_userID")
    private String productUserid;

    @ApiModelProperty(value = "商品二级类别")
    @TableField("product_twoType")
    private String productTwotype;


    private String storeId;


    private String salaryNum;

}
