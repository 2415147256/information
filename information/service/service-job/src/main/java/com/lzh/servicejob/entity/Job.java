package com.lzh.servicejob.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
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
 * @since 2022-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Job对象", description="")
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "兼职信息的id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String jobId;

    @ApiModelProperty(value = "兼职要求")
    private String title;

    @ApiModelProperty(value = "公司id")
    @TableField("companyID")
    private String companyID;

    @ApiModelProperty(value = "工作内容")
    private String content;

    @ApiModelProperty(value = "工作时间")
    private String time;

    @ApiModelProperty(value = "公司地址")
    private String address;

    @ApiModelProperty(value = "联系人")
    @TableField("linkName")
    private String linkName;

    @ApiModelProperty(value = "电话号码")
    private String telephone;

    @ApiModelProperty(value = "职位")
    private String position;

    @ApiModelProperty(value = "发布时间")
    @TableField("startTime")
    private Date startTime;

    @ApiModelProperty(value = "类别表")
    private String sign;

    @ApiModelProperty(value = "招聘结束时间")
    @TableField("endTime")
    private Date endTime;

    @ApiModelProperty(value = "职位名称")
    private String jobTitle;

    @ApiModelProperty(value = "职位类型")
    private String jobType;

    @ApiModelProperty(value = "工作经验")
    private String expersion;

    @ApiModelProperty(value = "工作薪水")
    private String pay;

    @ApiModelProperty(value = "学历要求")
    private String education;


}
