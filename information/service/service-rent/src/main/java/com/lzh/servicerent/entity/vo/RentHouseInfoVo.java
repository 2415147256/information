package com.lzh.servicerent.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
public class RentVo {

    @ApiModelProperty(value = "月租")
    private String monthRent;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "时间")
    private Date date;
}
