package com.lzh.servicerent.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
public class RentHouseInfoVo {

    @ApiModelProperty(value = "月租")
    private String rent;

    @ApiModelProperty(value = "几室")
    private String rooms;
}
