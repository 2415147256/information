package com.hd123.baas.sop.remote.rsh6sop.explosivev2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 方案下架对象
 *
 * @author liuhaoxin
 * @since 2021-11-29
 */
@Data
@ApiModel(description = "爆品活动对象")
public class HotActivity {
  @ApiModelProperty(value = "活动标识")
  private String activityId;
  @ApiModelProperty(value = "开始日期", example = "2020-11-13")
  private Date beginDate;
  @ApiModelProperty(value = "截止日期", example = "2020-11-14")
  private Date endDate;
  @ApiModelProperty(value = "最后修改时间", example = "2021-12-01 12:00:01")
  private Date lstupdTime;

  @ApiModelProperty(value = "所属组织", example = "1000000")
  private String orgGid;
  @ApiModelProperty(value = "爆品活动明细")
  private List<HotActivityDtl> storeGoodss;
}
