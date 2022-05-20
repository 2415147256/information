/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-parent
 * 文件名： RsPlatformShopBatchUpdateBusinessState.java
 * 模块说明：
 * 修改历史：
 * 2021年03月31日 - XLT - 创建。
 */
package
  com.hd123.baas.sop.remote.rsmas.platformshop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("批量更新平台门店营业状态对象")
public class RsPlatformShopBatchUpdateBusinessState {
  @ApiModelProperty("平台门店id列表")
  List<String> idsRequest;
  @ApiModelProperty("平台门店营业状态")
  String businessState;
}