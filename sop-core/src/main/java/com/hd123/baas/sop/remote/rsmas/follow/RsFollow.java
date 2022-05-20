/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-parent
 * 文件名： RsFollow.java
 * 模块说明：
 * 修改历史：
 * 2021年05月25日 - XLT - 创建。
 */
package
  com.hd123.baas.sop.remote.rsmas.follow;

import com.hd123.rumba.commons.biz.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("收藏")
public class RsFollow extends Entity {
  private static final long serialVersionUID = -118L;

  @ApiModelProperty(value = "租户")
  private String tenant;
  @ApiModelProperty(value = "用户ID")
  private String userId;
  @ApiModelProperty(value = "收藏类型")
  private String followType;
  @ApiModelProperty(value = "收藏")
  private Object followObj;
}