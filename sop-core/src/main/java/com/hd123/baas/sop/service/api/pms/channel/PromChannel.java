/**
 * <p>
 * 项目名: sop-pms-parent
 * 文件名: PromChannel.java
 * 模块说明:
 * 修改历史:
 * 2020年11月01日 - wushuaijun- 创建
 */
package com.hd123.baas.sop.service.api.pms.channel;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Data
@ApiModel("促销渠道")
@EqualsAndHashCode(callSuper = true)
public class PromChannel extends StandardEntity {

  public static final String FILTER_CODE_LIKES = "code:%=%";
  public static final String FILTER_NAME_LIKES = "name:%=%";

  @ApiModelProperty("租户")
  private String tenant;
  private String code;
  private String name;
  @ApiModelProperty("排序")
  private Integer lineNo;

}
