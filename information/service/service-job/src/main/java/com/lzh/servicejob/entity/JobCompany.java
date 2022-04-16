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
 * @since 2022-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Company对象", description="")
public class JobCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公司表id")
    @TableId(value = "id", type = IdType.ID_WORKER)
    private String id;

    @ApiModelProperty(value = "公司简介表")
    @TableField("company_Profile")
    private String companyProfile;

    @ApiModelProperty(value = "公司成立时间")
    private Date establishment;

    @ApiModelProperty(value = "注册金额")
    private String capital;

    @ApiModelProperty(value = "公司图片")
    private String picture;

    @ApiModelProperty(value = "公司名称")
    private String name;

    @ApiModelProperty(value = "公司总部")
    private String headquarters;

    @ApiModelProperty(value = "公司人数")
    private String peopleNum;

    @ApiModelProperty(value = "公司福利")
    private String corporateWelfare;

    @ApiModelProperty(value = "公司类型")
    private String companyType;


}
