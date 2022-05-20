/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-parent
 * 文件名： RsFollowFilter.java
 * 模块说明：
 * 修改历史：
 * 2021年05月25日 - XLT - 创建。
 */
package
  com.hd123.baas.sop.remote.rsmas.follow;

import com.hd123.baas.sop.remote.rsmas.RsFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("收藏查询条件")
public class RsFollowFilter extends RsFilter {
  @ApiModelProperty(value = "租户等于")
  private String tenantEq;
  @ApiModelProperty(value = "收藏类型等于")
  private String followTypeEq;
  @ApiModelProperty(value = "用户ID等于")
  private String userIdEq;
  @ApiModelProperty("内容精确匹配")
  private List<String> followObjIn;
}