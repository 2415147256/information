package com.lzh.servicejob.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2022-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="JobDeliveryRecord对象", description="")
public class JobDeliveryRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主表的id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "工作的id")
    private String jobId;

    @ApiModelProperty(value = "投递人的id")
    private String userId;

    @ApiModelProperty(value = "投递的时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
