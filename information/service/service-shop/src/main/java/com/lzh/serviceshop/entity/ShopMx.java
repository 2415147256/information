package com.lzh.serviceshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value="ShopMx对象", description="")
public class ShopMx implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ID_WORKER)
    private String id;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "价格区间")
    private String price;

    @ApiModelProperty(value = "成色")
    private String colour;

    @ApiModelProperty(value = "购买数量")
    private String buyNum;

    @ApiModelProperty(value = "上市时间")
    private Date listedTime;


}
