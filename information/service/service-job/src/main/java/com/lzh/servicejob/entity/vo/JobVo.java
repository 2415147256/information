package com.lzh.servicejob.entity.vo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
public class JobVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工作经验")
    private String expersion;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "公司人数")
    private String peopleNum;

    @ApiModelProperty(value = "是否是找工作")
    // 1 表示发布工作 0 表示找工作
    private String sgin;

    @ApiModelProperty(value = "职位类别")
    private String jobType;

    @ApiModelProperty(value = "薪水")
    private String pay;


}
