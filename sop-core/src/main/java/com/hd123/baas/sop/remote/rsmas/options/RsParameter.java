/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	cloudscm-pay-api
 * 文件名：	PayParameter.java
 * 模块说明：	
 * 修改历史：
 * 2016年11月17日 - ChenYuLong - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.options;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 参数
 * 
 * @author ChenYuLong
 *
 */
@Getter
@Setter
public class RsParameter implements Serializable {

  private static final long serialVersionUID = 2296288309611984169L;

  public RsParameter() {
    super();
  }

  public RsParameter(String name, String value) {
    this();
    this.name = name;
    this.value = value;
  }

  private String name;
  private String value;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    RsParameter other = (RsParameter) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }
}
