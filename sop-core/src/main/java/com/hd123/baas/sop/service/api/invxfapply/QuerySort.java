/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	swallows-parent
 * 文件名：	QuerySort.java
 * 模块说明：
 * 修改历史：
 * 2020年02月06日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.service.api.invxfapply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.BooleanUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author huangjunxian
 * @since 1.0
 */
@ApiModel(description = "查询排序参数")
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuerySort implements Serializable {
  private static final long serialVersionUID = -5149770059035437297L;
  @ApiModelProperty(value = "排序字段", required = true)
  @NotBlank
  private String field;
  @ApiModelProperty(value = "排序方式，默认倒叙", required = false)
  private Boolean asc;

  public static QuerySort create(String field) {
    return create(field, false);
  }

  public static QuerySort create(String field, boolean asc) {
    return new QuerySort().setField(field).setAsc(asc);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(field);
    sb.append(" ");
    sb.append(BooleanUtils.isTrue(asc) ? "asc" : "desc");
    return sb.toString();
  }
}
