package com.lzh.serviceshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@ApiModel(value="ShopStore对象", description="")
public class ShopStore implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID号")
    @TableId(value = "store_id", type = IdType.ID_WORKER_STR)
    private String storeId;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    @ApiModelProperty(value = "微信回复")
    private String reply;

    @ApiModelProperty(value = "店铺信誉")
    private String credibility;

    @ApiModelProperty(value = "服务水平")
    private String service;

    @ApiModelProperty(value = "店铺图片")
    private String img;


}
