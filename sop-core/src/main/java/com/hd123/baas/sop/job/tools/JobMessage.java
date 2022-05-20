/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobMessage.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.tools;

import com.hd123.rumba.commons.json.JsonObject;

/**
 * 只含有一个消息文本的作业消息，
 * 
 * @author huzexiong
 * @since 1.0
 */
public class JobMessage extends JobMessageBase {

  private static final long serialVersionUID = -1086413919068501335L;

  private static final String ATTR_TEXT = "text";

  public static JobMessage fromJson(JsonObject json) {
    if (json == null) {
      return null;
    }
    JobMessage target = new JobMessage();
    if (json.has(ATTR_TEXT)) {
      target.setText(json.getString(ATTR_TEXT));
    }
    return target;
  }

  private String text = "";

  /** 消息文本。 */
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    JobMessage other = (JobMessage) obj;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    return true;
  }

  @Override
  protected JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put(ATTR_TEXT, text);
    return json;
  }

  @Override
  public String toString() {
    return text;
  }

}