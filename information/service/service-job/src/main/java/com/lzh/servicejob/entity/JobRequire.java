package com.lzh.servicejob.entity;

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
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Job-require对象", description="")
public class JobRequire implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "绑定的工作表的id")
    @TableField("job_ID")
    private String jobId;

    @ApiModelProperty(value = "工作经验")
    private String expersion;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "公司人数")
    private int peopleNum;

    @ApiModelProperty(value = "是否是找工作")
    // 1 表示发布工作 0 表示找工作
    private String sgin;

    @ApiModelProperty(value = "职位类别")
    private int jobType;


}
