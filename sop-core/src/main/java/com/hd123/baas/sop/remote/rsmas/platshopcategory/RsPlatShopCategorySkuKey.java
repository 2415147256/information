/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-parent
 * 文件名： RsPlatShopCategorySkuKey.java
 * 模块说明：
 * 修改历史：
 * 2021年02月25日 - XLT - 创建。
 */
package
  com.hd123.baas.sop.remote.rsmas.platshopcategory;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel
public class RsPlatShopCategorySkuKey {

  private String platShopCategoryId;
  private List<String> skuIds = new ArrayList<String>();

  public RsPlatShopCategorySkuKey() {
    super();
  }

  public RsPlatShopCategorySkuKey(String platShopCategoryId, List<String> skuIds) {
    super();
    this.platShopCategoryId = platShopCategoryId;
    this.skuIds = skuIds;
  }
}