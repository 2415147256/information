package com.lzh.servicejob.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
public class JobDeliveryRecordVo {

    @ApiModelProperty(value = "工作的id")
    private String jobId;

    @ApiModelProperty(value = "投递人的id")
    private String userId;

    @ApiModelProperty(value = "职位名称")
    private String jobTitle;

    @ApiModelProperty(value = "公司地址")
    private String address;

    @ApiModelProperty(value = "投递的时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
