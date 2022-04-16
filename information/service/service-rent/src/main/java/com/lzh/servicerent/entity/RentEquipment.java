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
@ApiModel(value="RentEquipment对象", description="")
public class RentEquipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "furniture_ID", type = IdType.ID_WORKER_STR)
    private String furnitureId;

    @ApiModelProperty(value = "床")
    private String bed;

    @ApiModelProperty(value = "衣柜")
    private String wardrobe;

    @ApiModelProperty(value = "沙发")
    private String couch;

    @ApiModelProperty(value = "电视")
    private String tv;

    @ApiModelProperty(value = "冰箱")
    @TableField("Refrigerator")
    private String Refrigerator;

    @ApiModelProperty(value = "洗衣机")
    private String washer;

    @ApiModelProperty(value = "空调")
    private String airConditioning;

    @ApiModelProperty(value = "热水器")
    private String waterHeater;

    @ApiModelProperty(value = "宽带")
    private String broadband;

    @ApiModelProperty(value = "暖气")
    private String heating;

    @ApiModelProperty(value = "燃气灶")
    private String gasStove;

    @ApiModelProperty(value = "阳台")
    private String balcony;

    @ApiModelProperty(value = "智能门锁")
    private String smartDoor;

    @ApiModelProperty(value = "油烟机")
    private String rangeHood;

    @ApiModelProperty(value = "可做饭")
    private String cookie;

    @ApiModelProperty(value = "房屋的id")
    @TableField("house_Id")
    private Integer houseId;


}
