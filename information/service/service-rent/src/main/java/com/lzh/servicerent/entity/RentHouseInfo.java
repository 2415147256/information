package com.lzh.servicerent.entity;

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
 * @since 2022-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RentHouseInfo对象", description="")
public class RentHouseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "联系人的id")
    @TableField("user_Id")
    private String userId;

    @ApiModelProperty(value = "房屋照片")
    @TableField("Image")
    private String Image;

    @ApiModelProperty(value = "属于哪个区")
    private String region;

    private String DetailedAddress;

    private String floor;

}
