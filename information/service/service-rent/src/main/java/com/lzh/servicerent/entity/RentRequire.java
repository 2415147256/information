package com.lzh.servicerent.entity;

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
 * @since 2022-04-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RentRequire对象", description="")
public class RentRequire implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id号")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "房屋详细描述")
    private String content;

    @ApiModelProperty(value = "房东要求")
    private String landlordAsked;

    @ApiModelProperty(value = "房屋特点")
    private String sgin;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "房东的id")
    private String userId;

    @ApiModelProperty(value = "房屋的id")
    private String houseId;


}
