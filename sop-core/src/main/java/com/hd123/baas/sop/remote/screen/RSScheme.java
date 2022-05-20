/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： RSTip.java
 * 模块说明：
 * 修改历史：
 * 2021年06月04日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.screen;

/**
 * @author huangjunxian
 * @since 1.0
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "方案")
public class RSScheme implements Serializable {
  private static final long serialVersionUID = -6582802634534598456L;

  @ApiModelProperty(value = "门店数据标识列表，不传则为全部门店", required = false)
  private List<String> storeUuids;
  @ApiModelProperty(value = "方案id", required = true)
  private String id;
  @ApiModelProperty(value = "全部门店", required = true)
  private Integer allStore;
  @ApiModelProperty(value = "所属组织id", required = true)
  private String orgUuid;
  @ApiModelProperty(value = "方案名称", required = true)
  private String name;
  @ApiModelProperty(value = "开始时间", required = true)
  private Date beginTime;
  @ApiModelProperty(value = "结束时间", required = true)
  private Date endTime;
  @ApiModelProperty(value = "创建时间", required = true)
  private Date createTime;
  @ApiModelProperty(value = "创建人", required = true)
  private String creator;
  @ApiModelProperty(value = "轮播图明细列表", required = true)
  private List<RSBanner> banners = new ArrayList<>();
  @ApiModelProperty(value = "广告语明细列表", required = true)
  private List<String> tips = new ArrayList<>();

}
