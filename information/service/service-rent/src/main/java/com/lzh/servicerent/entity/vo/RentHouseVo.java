package com.lzh.servicerent.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
public class RentHouseVo {
    @ApiModelProperty(value = "id")
    @TableId(value = "house_id", type = IdType.ID_WORKER_STR)
    private Integer houseId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "详细信息")
    private String content;

    @ApiModelProperty(value = "面积")
    private String area;

    @ApiModelProperty(value = "几室")
    private String rooms;

    @ApiModelProperty(value = "小区名称")
    private String cellName;

    @ApiModelProperty(value = "租金")
    private Integer rent;

    @ApiModelProperty(value = "房屋照片")
    @TableField("Image")
    private String Image;

    @ApiModelProperty(value = "属于哪个区")
    private String region;

    @ApiModelProperty(value = "联系人的姓名")
    private String userName;
}
