/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-api
 * 文件名：	ParameterCondition.java
  * 模块说明：	
 * 修改历史：

 * 2019年10月12日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.task;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class RsParameterCondition {

  private String name;
  private RsParameterOperator operator;
  private String[] values;

}
