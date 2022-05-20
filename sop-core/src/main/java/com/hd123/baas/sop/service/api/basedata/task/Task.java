/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-api
 * 文件名：	Task.java
  * 模块说明：	
 * 修改历史：

 * 2019年10月9日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.task;

import com.hd123.rumba.commons.biz.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 作业
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class Task extends Entity {
  private static final long serialVersionUID = -3023425941543738247L;

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "ID")
  private String id;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "备注")
  private String remark;

}
