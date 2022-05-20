/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-commons
 * 文件名： PlatformShopBatchUpdateBusinessHour.java
 * 模块说明：
 * 修改历史：
 * 2021年03月31日 - XLT - 创建。
 */
package
    com.hd123.baas.sop.service.api.basedata.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("批量更新平台门店营业时间对象")
public class ShopBatchUpdateBusinessHour {
  @ApiModelProperty("门店id列表")
  List<String> idsRequest;
  @ApiModelProperty("门店营业时间")
  List<String> businessHours;
}