/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-parent
 * 文件名： RsBatchSetDefaultRequest.java
 * 模块说明：
 * 修改历史：
 * 2021年06月02日 - XLT - 创建。
 */
package
  com.hd123.baas.sop.remote.rsmas.pos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("POS收银机批量设置默认请求")
public class RsBatchSetDefaultRequest implements Serializable {
  private static final long serialVersionUID = -18688L;

  @ApiModelProperty(value = "id列表")
  private List<String> ids = new ArrayList<>();
}