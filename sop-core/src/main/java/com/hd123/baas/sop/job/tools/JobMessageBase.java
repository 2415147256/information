/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobMessageBase.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.tools;

import java.io.Serializable;

import com.hd123.rumba.commons.json.JsonObject;

/**
 * 所有作业消息对象基类。
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public abstract class JobMessageBase implements Serializable {

  private static final long serialVersionUID = -7288511533521650828L;

  private JobMessageBase upper;

  /**
   * 返回上级作业消息对象，返回null表示当前为顶级对象。
   */
  public JobMessageBase getUpper() {
    return upper;
  }

  /**
   * 设置上级作业消息对象。
   * 
   * @param upper
   */
  public void setUpper(JobMessageBase upper) {
    this.upper = upper;
  }

  /**
   * 返回顶级作业消息对象。
   */
  public JobMessageBase getTop() {
    if (upper == null) {
      return this;
    } else {
      return upper.getTop();
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((upper == null) ? 0 : upper.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    JobMessageBase other = (JobMessageBase) obj;
    if (upper == null) {
      if (other.upper != null)
        return false;
    } else if (!upper.equals(other.upper))
      return false;
    return true;
  }

  protected abstract JsonObject toJson();
}
