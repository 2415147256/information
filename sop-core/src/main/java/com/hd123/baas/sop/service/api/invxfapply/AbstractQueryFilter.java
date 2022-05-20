/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	workspace_shop
 * 文件名：	AbstractQueryFilter.java
 * 模块说明：
 * 修改历史：
 * 2020年05月22日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.service.api.invxfapply;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询过滤器基类。
 *
 * @author huangjunxian
 * @since 1.0
 */
@Data
public abstract class AbstractQueryFilter implements Serializable {

  private static final long serialVersionUID = -1544462353382621494L;
  @NotNull
  @ApiModelProperty(value = "页码，从0开始", example = "0", required = true)
  private Integer page;
  @NotNull
  @ApiModelProperty(value = "分页大小，从1开始，最大不超过100", example = "100", required = true)
  private Integer pageSize;
  @ApiModelProperty(value = "排序条件", required = false)
  private List<QuerySort> sorts = new ArrayList<>();
}
