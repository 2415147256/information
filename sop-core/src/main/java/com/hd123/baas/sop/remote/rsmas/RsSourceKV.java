/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-parent
 * 文件名： RsSourceKV.java
 * 模块说明：
 * 修改历史：
 * 2021年05月25日 - XLT - 创建。
 */
package
  com.hd123.baas.sop.remote.rsmas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsSourceKV {
  @ApiModelProperty(value = "KEY等于")
  private String key;
  @ApiModelProperty(value = "值类似于")
  private String value;
}